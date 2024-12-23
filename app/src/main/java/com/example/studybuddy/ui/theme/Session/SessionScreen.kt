package com.example.studybuddy.ui.theme.Session

//import com.example.studybuddy.ui.theme.Task.TaskScreen
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studybuddy.ui.theme.Red
import com.example.studybuddy.ui.theme.components.DeleteSubjectDialog
import com.example.studybuddy.ui.theme.components.SubjectListBottomSheet
import com.example.studybuddy.ui.theme.components.studySessionsList
import com.example.studybuddy.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studybuddy.util.Constants.ACTION_SERVICE_START
import com.example.studybuddy.util.Constants.ACTION_SERVICE_STOP
import com.example.studybuddy.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "studybuddy//dashboard/Session"
        )
    ]
)
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator,
    timerService: StudySessionTimerService
) {
    val viewModel: SessionScreenViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    SessionScreen(
        state = state,
        snackbarEvent = viewModel.snackbarEventFlow,
        onEvent = viewModel::onEvent,
        onBackButtonClick = { navigator.navigateUp() },
        timerService = timerService
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    state: SessionState,
    snackbarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (SessionEvent) -> Unit,
    onBackButtonClick: () -> Unit,
    timerService: StudySessionTimerService
) {
    val vm: SessionScreenViewModel = hiltViewModel()

    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState

    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember {
        mutableStateOf(false)
    }

    var deleteSessionDialogOpen by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                is SnackBarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackBarEvent.NavigateUp -> {}
            }
        }
    }

    LaunchedEffect(key1 = state.subjects) {
        val subjectId = timerService.subjectId.value
        onEvent(
            SessionEvent.UpdateSubjectIdAndRelatedSubject(
                subjectId = subjectId,
                relatedToSubject = state.subjects.find { it.subjectId == subjectId }?.name
            )
        )
    }




    SubjectListBottomSheet(sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = { subject ->
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    isBottomSheetOpen = false
                }
            }
            onEvent(SessionEvent.OnRelatedToSubjectChange(subject))
            isBottomSheetOpen = false
        },
        bottomSheetTitle = "Select Subject",
        onDismissRequest = { isBottomSheetOpen = false }
    )

    DeleteSubjectDialog(isOpen = deleteSessionDialogOpen,
        title = "Delete Session ?",
        bodyText = "Are you sure you want to Delete Session ?",
        onDismissRequest = { deleteSessionDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(SessionEvent.DeleteSession)
            deleteSessionDialogOpen = false
        })

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SessionScreenTopBar(onBackButtonClick = onBackButtonClick)

        }
    ) { paddingValues ->


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
        {
            item {
                Spacer(modifier = Modifier.height(30.dp))

            }

            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
            }

            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = state.relatedToSubject ?: "",
                    selectSubjectButtonClick = { isBottomSheetOpen = true },
                    seconds = seconds
                )
            }
            item {
                ButtonSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {
                        if (state.subjectId != null && state.relatedToSubject != null) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = if (currentTimerState == TimerState.STARTED) {
                                    ACTION_SERVICE_STOP
                                } else ACTION_SERVICE_START
                            )
                            timerService.subjectId.value = state.subjectId
                        } else {
                            onEvent(SessionEvent.NotifyToUpdateSubject)
                        }
                    },
                    cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL
                        )
                    },
                    finishButtonClick = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        if (duration >= 36) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = ACTION_SERVICE_CANCEL
                            )
                        }
                        onEvent(SessionEvent.SaveSession(duration))
                    },
                    timerState = currentTimerState,
                    seconds = seconds
                )
            }
            studySessionsList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                emptyListText = "You don't have any recent study sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = state.sessions,
                onDeleteIconClick = { session ->
                    deleteSessionDialogOpen = true
                    onEvent(SessionEvent.OnDeleteSessionButtonClick(session))
                }
            )
        }


    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreenTopBar(
    onBackButtonClick: () -> Unit
) {
    TopAppBar(navigationIcon = {
        IconButton(onClick = { }) {

            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")

        }
    }, title = {
        Text(
            text = "Study Sessions",
            style = MaterialTheme.typography.headlineSmall
        )
    })
}


@Composable
fun TimerSection(
    modifier: Modifier,
    hours: String,
    minutes: String,
    seconds: String
) {

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    )
    {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )

        Row() {
            AnimatedContent(targetState = hours,
                label = hours,
                transitionSpec = { timerTextAnimation() }) { hours ->

                Text(
                    text = "$hours:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(targetState = minutes,
                label = minutes,
                transitionSpec = { timerTextAnimation() }) { minutes ->

                Text(
                    text = "$minutes:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(targetState = seconds, label = seconds,
                transitionSpec = { timerTextAnimation() }) { seconds ->

                Text(
                    text = "$seconds",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }
    }

}


@Composable
fun RelatedToSubjectSection(
    modifier: Modifier,
    relatedToSubject: String,
    selectSubjectButtonClick: () -> Unit,
    seconds: String
) {

    Column(modifier = modifier) {

        Text(text = "Related to Subject", style = MaterialTheme.typography.bodySmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = relatedToSubject, style = MaterialTheme.typography.bodyLarge)

            IconButton(
                onClick = selectSubjectButtonClick,
                enabled = seconds == "00"
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "select subject"
                )

            }


        }
    }

}

@Composable
private fun ButtonSection(
    modifier: Modifier,
    startButtonClick: () -> Unit,
    cancelButtonClick: () -> Unit,
    finishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = cancelButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = "Cancel"
            )
        }
        Button(
            onClick = startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = when (timerState) {
                    TimerState.STARTED -> "Stop"
                    TimerState.STOPPED -> "Resume"
                    else -> "Start"
                }
            )
        }
        Button(
            onClick = finishButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = "Finish"
            )
        }
    }
}

private fun timerTextAnimation(duration: Int = 600): ContentTransform {

    return slideInVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)) { fullHeight ->
                fullHeight
            } +
            fadeOut(animationSpec = tween(duration))
}
