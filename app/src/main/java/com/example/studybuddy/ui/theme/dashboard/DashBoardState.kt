package com.example.studybuddy.ui.theme.dashboard

import android.graphics.Color
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Subject

data class DashboardState(
    val totalSubjectCount: Int = 0,
    val totalStudiedHours: Float = 0f,
    val totalGoalStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<androidx.compose.ui.graphics.Color> = Subject.subjectCardColors.random(),
    val session: Sessions? = null
)