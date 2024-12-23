package com.example.studybuddy.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Task
import kotlinx.coroutines.flow.Flow


@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: Sessions)

    @Delete
    suspend fun deleteSession(session: Sessions)

    @Query("SELECT * FROM Sessions")
    fun getAllSessions(): Flow<List<Sessions>>

    @Query("SELECT * FROM Sessions WHERE sessionSubjectId = :subjectId")
    fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Sessions>>

    @Query("SELECT SUM(duration) FROM Sessions")
    fun getTotalSessionsDuration(): Flow<Long>

    @Query("SELECT SUM(duration) FROM Sessions WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionsDurationBySubject(subjectId: Int): Flow<Long>

    @Query("DELETE FROM Sessions WHERE sessionSubjectId = :subjectId")
    fun deleteSessionsBySubjectId(subjectId: Int)
}