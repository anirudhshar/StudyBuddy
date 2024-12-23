package com.example.studybuddy.ui.theme.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Subject
import com.example.studybuddy.domain.model.Task
import com.example.studybuddy.domain.repository.SessionRepository
import com.example.studybuddy.domain.repository.SubjectRepository
import com.example.studybuddy.domain.repository.TaskRepository
import com.example.studybuddy.util.SnackBarEvent
import com.example.studybuddy.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = combine(
        _state,
        subjectRepository.getTotalSubjectsCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionDuration()
    ) { state, subjectCount, goalHours, subjects, totalSessionDuration ->
        state.copy(
            totalSubjectCount = subjectCount,
            totalGoalStudyHours = goalHours,
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toHours()
        )


    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = DashboardState()
    )

    val tasks: StateFlow<List<Task>> = taskRepository.getAllUpcomingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentSessions: StateFlow<List<Sessions>> = sessionRepository.getRecentFiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _snackbarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    fun onEvent(event: DashBoardEvent) {

        when (event) {

            is DashBoardEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)

                }

            }

            is DashBoardEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }

            }

            is DashBoardEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }

            is DashBoardEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(
                        session = event.sessions
                    )
                }
            }

            DashBoardEvent.SaveSubject -> {
                saveSubject()  //a seprate function down side
            }

            DashBoardEvent.DeleteSession -> {deleteSession()}
            is DashBoardEvent.OnTaskIsCompletedChange -> {
                updateTask(event.task)
            }

        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {

                taskRepository.upsertTask(
                    task = task.copy(
                        isComplete = !task.isComplete
                    )

                )

                _snackbarEventFlow.emit(SnackBarEvent.ShowSnackbar("Saved in Completed Tasks"))

            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackBarEvent.ShowSnackbar("Couldn't update Task"))
                SnackbarDuration.Long

                e.printStackTrace()

            }
        }

    }

    private fun saveSubject() {   // this function is used to interact with net repository
        viewModelScope.launch {
            try {

                subjectRepository.upsertSubject(  // the impl interacts with the DAO where we have provided the sqlite querry
                    subject = Subject(
                        // the querry is to add the subject to the database
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() },
                    )
                )
                _state.update {   //after filling the add subject  , we need to update and remove the name and other fields too.
                    it.copy(       // the net repo and net repo impl are binded together in repo module.
                        subjectName = "",
                        goalStudyHours = " ",
                        subjectCardColors = Subject.subjectCardColors.random()
                    )

                }
                _snackbarEventFlow.emit(SnackBarEvent.ShowSnackbar("Subject Saved"))

            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackBarEvent.ShowSnackbar("Couldn't save subject"))
                SnackbarDuration.Long

                e.printStackTrace()

            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackbarEventFlow.emit(
                        SnackBarEvent.ShowSnackbar(message = "Session deleted successfully")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackBarEvent.ShowSnackbar(
                        message = "Couldn't delete session. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }


}