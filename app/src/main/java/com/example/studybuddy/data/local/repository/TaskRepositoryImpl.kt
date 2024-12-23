package com.example.studybuddy.data.local.repository

import com.example.studybuddy.data.local.TaskDao
import com.example.studybuddy.domain.model.Task
import com.example.studybuddy.domain.repository.TaskRepository
//import com.example.studybuddy.tasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao) : TaskRepository {

    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
       return taskDao.getTaskById(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectInt: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectInt)
            .map {      //used to sort and show only those tasks that are not complete
                tasks ->
                tasks.filter {
                    !it.isComplete
                }
            }
            .map {
                tasks ->   //sort tasks in order of priority i.e 1 ,2 or 3.
                sortTasks(tasks)
            }

    }

    override fun getCompletedTaskForSubject(subjectInt: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectInt)
            .map {      //used to sort and show only those tasks that are  complete
                    tasks ->
                tasks.filter {
                    it.isComplete
                }
            }
            .map {
                    tasks ->   //sort tasks in order of priority i.e 1 ,2 or 3.
                sortTasks(tasks)
            }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
            .map{
                tasks ->
                tasks.filter {
                    !it.isComplete
                }
            }
            .map {
                tasks ->
                sortTasks(tasks)
            }
    }

    private fun sortTasks(tasks: List<Task>): List<Task> {  // this is a seperate fucntion
        return tasks.sortedWith(        // to show tasks in order of their prioroity i.e high to low
            compareBy<Task> { it.dueDate }
                .thenByDescending { it.priority }

        )
    }


}