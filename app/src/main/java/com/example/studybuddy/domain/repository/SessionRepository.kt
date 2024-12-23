package com.example.studybuddy.domain.repository

import com.example.studybuddy.domain.model.Sessions
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(sessions: Sessions)

    suspend fun deleteSession(sessions: Sessions)

    fun getAllSessions(): Flow<List<Sessions>>

    fun getRecentFiveSessions(): Flow<List<Sessions>>

    fun getRecentTenSessionsForSubject(subjectId:Int):Flow<List<Sessions>>

    fun getTotalSessionDuration(): Flow<Long>

    fun getTotalSessionDurationBySubject(subjectId:Int): Flow<Long>

}