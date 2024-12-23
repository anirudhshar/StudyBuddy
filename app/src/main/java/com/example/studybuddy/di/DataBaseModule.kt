package com.example.studybuddy.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.studybuddy.data.local.AppDatabase
import com.example.studybuddy.data.local.SessionDao
import com.example.studybuddy.data.local.SubjectDao
import com.example.studybuddy.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideDataBase(
        application: Application
    ): AppDatabase {

        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "study_buddy_database.db"
        ).build()


    }

    @Provides
    @Singleton
    fun provideSubjectDao(appDatabase: AppDatabase) : SubjectDao {
        return appDatabase.subjectDao()
    }


    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase) : TaskDao {
        return appDatabase.taskDao()
    }


    @Provides
    @Singleton
    fun provideSessionDao(appDatabase: AppDatabase) : SessionDao {
        return appDatabase.sessionDao()
    }

}