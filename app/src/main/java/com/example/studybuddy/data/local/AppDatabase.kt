package com.example.studybuddy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Subject
import com.example.studybuddy.domain.model.Task


@Database(
    entities = [
        Subject::class , Sessions::class, Task::class
    ],
    version = 1
)
@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun subjectDao(): SubjectDao

    abstract fun taskDao(): TaskDao

    abstract fun sessionDao(): SessionDao


}