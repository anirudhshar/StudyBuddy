package com.example.studybuddy.ui.theme.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Task

sealed class DashBoardEvent {

    data object SaveSubject :DashBoardEvent()

    data object DeleteSession :DashBoardEvent()

    data class OnDeleteSessionButtonClick(val sessions: Sessions): DashBoardEvent()

    data class OnTaskIsCompletedChange(val task: Task): DashBoardEvent()

    data class OnSubjectCardColorChange(val color: List<Color>): DashBoardEvent()

    data class OnSubjectNameChange(val name: String): DashBoardEvent()

    data class OnGoalStudyHoursChange(val hours: String): DashBoardEvent()


}