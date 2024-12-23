package com.example.studybuddy.ui.theme.Session

import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Subject

sealed class SessionEvent {

    data class OnRelatedToSubjectChange(val subject: Subject) : SessionEvent()

    data class SaveSession (val duration: Long) : SessionEvent()

    data class OnDeleteSessionButtonClick(val session: Sessions) : SessionEvent()

    data object DeleteSession : SessionEvent()

    data object NotifyToUpdateSubject : SessionEvent()

    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId: Int?,
        val relatedToSubject: String?
    ) : SessionEvent()


}