package com.example.studybuddy.data.local.repository

import com.example.studybuddy.data.local.SessionDao
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {
    override suspend fun insertSession(sessions: Sessions) {
        sessionDao.insertSession(sessions)
    }

    override suspend fun deleteSession(sessions: Sessions) {
        sessionDao.deleteSession(sessions)
    }

    override fun getAllSessions(): Flow<List<Sessions>> {

        return sessionDao.getAllSessions().map { sessions ->
            sessions.sortedByDescending { it.date }

        }
    }

    override fun getRecentFiveSessions(): Flow<List<Sessions>> {
        return sessionDao.getAllSessions().map { sessions ->
            sessions.sortedByDescending { it.date }

        }
            .take(count = 5)
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Sessions>> {
        return sessionDao.getRecentSessionsForSubject(subjectId).take(10).map { sessions ->
            sessions.sortedByDescending { it.date }

        }
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return sessionDao.getTotalSessionsDuration()
    }

    override fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionsDurationBySubject(subjectId)
    }


}