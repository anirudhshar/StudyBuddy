package com.example.studybuddy.ui.theme.subject

import androidx.compose.ui.graphics.Color
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Subject
import com.example.studybuddy.domain.model.Task

data class SubjectStates(
    val currentSubjectId: Int? = null,
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColors.random(),
    val studiedHours: Float = 0f,
    val progress: Float = 0f,
    val recentSessions: List<Sessions> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Sessions? = null



    )