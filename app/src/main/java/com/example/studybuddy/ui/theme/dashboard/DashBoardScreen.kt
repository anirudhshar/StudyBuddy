package com.example.studybuddy.ui.theme.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studybuddy.R
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Task
import com.example.studybuddy.ui.theme.Task.TaskScreenNavArgs
import com.example.studybuddy.ui.theme.components.AddSubjectDialog
import com.example.studybuddy.ui.theme.components.CountCard
import com.example.studybuddy.ui.theme.components.DeleteSubjectDialog
import com.example.studybuddy.ui.theme.components.SubjectCard
import com.example.studybuddy.ui.theme.components.studySessionsList
import com.example.studybuddy.ui.theme.components.taskList
import com.example.studybuddy.ui.theme.destinations.SessionScreenRouteDestination
import com.example.studybuddy.ui.theme.destinations.SubjectScreenRouteDestination
import com.example.studybuddy.ui.theme.destinations.TaskScreenRouteDestination
import com.example.studybuddy.ui.theme.subject.SubjectScreenNavArgs
import com.example.studybuddy.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow

@RootNavGraph(start = true)
@Destination
@Composable
fun DashBoardScreenRoute(
    navigator: DestinationsNavigator
){
    val vm: DashBoardViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val recentSessions by vm.recentSessions.collectAsStateWithLifecycle()

    DashBoardScreen(
        state = state,
        tasks = tasks,
        recentSessions = recentSessions,
        onEvent = vm::onEvent,
        onSubjectCardClick ={
            subjectId ->
            subjectId?.let {
                val navArg = SubjectScreenNavArgs(subjectId= subjectId)
                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArg))
            }

        } ,
        onTaskCardClick ={taskId ->
            val navArg = TaskScreenNavArgs(taskId= taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        } ,
        snackbarEvent = vm.snackbarEventFlow,
        onStartSessionButtonClick = {
            navigator.navigate(SessionScreenRouteDestination)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen(
    state: DashboardState,
    tasks: List<Task>,
    recentSessions: List<Sessions>,
    onEvent: (DashBoardEvent) -> Unit,
    snackbarEvent :SharedFlow<SnackBarEvent>,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionButtonClick: () -> Unit,

    ){

    var isAddSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var isDeleteSessionDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val snackbarHostState= remember {

        SnackbarHostState()
    }

    LaunchedEffect(key1 = snackbarEvent){   //Launcher Effect is useful for non composable code or the code that required coroutine
        snackbarEvent.collect{event ->      //the value of key1 is set to true i.e the launching of this block will not depend on this key.
            when(event){
                is SnackBarEvent.ShowSnackbar ->{
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration,
                    )
                }

                else -> {
                    SnackBarEvent.NavigateUp

                }
            }
        }
    }



    
    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen ,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours ,
        onSubjectNameChange = {
            onEvent(DashBoardEvent.OnSubjectNameChange(it))
        },
        onGoalHoursChange = {
            onEvent(DashBoardEvent.OnGoalStudyHoursChange(it))
        } ,
        selectedColors = state.subjectCardColors  ,
        onColorChange = {
            onEvent(DashBoardEvent.OnSubjectCardColorChange(it))
                    },
        onDismissRequest = { isAddSubjectDialogOpen=false },
        onConfirmButtonClick = {
            onEvent(DashBoardEvent.SaveSubject)
            isAddSubjectDialogOpen=false

        },

    )
    
    DeleteSubjectDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session" ,
        bodyText ="Are you sure, you want to delete this session? Your study hours will be reduced by this session's hours.",
        onDismissRequest = {
            onEvent(DashBoardEvent.DeleteSession)
            isDeleteSessionDialogOpen= false },
        onConfirmButtonClick = { isDeleteSessionDialogOpen= false })




    Scaffold(
        snackbarHost= {SnackbarHost(hostState = snackbarHostState)},
        topBar = {
        DashBoardScreenTopAppBar()
        

    }
    ) {
        paddingValues ->

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues))
        {
            item {
                CountCardsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString()
                )
            }

            item {

                SubjectCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddIconClicked = {
                        isAddSubjectDialogOpen=true

                    },
                    onSubjectCardClick = onSubjectCardClick)
            }

            item {

                Button(onClick =  onStartSessionButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)) {

                    Text(text = "Start New Study Session")

                }

            }

            taskList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks \n Click the add task button to add new task.",
                tasks = tasks,
                onCheckBoxClick = {
                    onEvent(DashBoardEvent.OnTaskIsCompletedChange(it))
                },
                onTaskClick = onTaskCardClick
            )
            item { 
                Spacer(modifier = Modifier.height(20.dp))
            }

            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any study sessions \n Click the new study session button to add new session.",
                sessions = recentSessions,
                onDeleteIconClick = {
                    onEvent(DashBoardEvent.OnDeleteSessionButtonClick(it))
                    isDeleteSessionDialogOpen = true}
            )
            






        }
        
    }

    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashBoardScreenTopAppBar(){
    CenterAlignedTopAppBar(title = { Text(text = "Study Buddy" ,
        style = MaterialTheme.typography.headlineMedium) })
}

@Composable
private fun CountCardsSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String
) {
    Row(modifier = modifier) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = "$subjectCount"
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Study Goal Hours",
            count = goalHours
        )
    }
}

@Composable
private fun SubjectCardsSection(
    modifier: Modifier,
    subjectList: List<com.example.studybuddy.domain.model.Subject>,
    emptyListText: String = "You don't have any subjects.\n Click the + button to add new subject.",
    onAddIconClicked: () -> Unit,
    onSubjectCardClick: (Int?) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = onAddIconClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject"
                )
            }
        }
        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img_books),
                contentDescription = emptyListText
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList){
                subject ->
                SubjectCard(subjectName = subject.name, gradientColors = subject.colors.map{Color(it)}, onClick = {onSubjectCardClick(subject.subjectId)})
            }
        }

    }
}

