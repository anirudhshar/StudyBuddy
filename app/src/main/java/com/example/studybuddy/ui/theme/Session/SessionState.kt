package com.example.studybuddy.ui.theme.Session

import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Subject
import com.example.studybuddy.domain.model.Task

data class SessionState (

    val subjects: List<Subject> = emptyList(),
    val sessions: List<Sessions> = emptyList(),
    val relatedToSubject: String? = null,
    val subjectId :Int? = null,
    val session :Sessions? = null


)