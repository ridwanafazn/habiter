package uas.pam.habiter.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import uas.pam.habiter.model.DeleteResponse
import uas.pam.habiter.model.ProgressData
import uas.pam.habiter.model.Task

interface ApiService {
    // GET all tasks for a specific user
    @GET("/user/{userId}/tasks")
    fun getAllTasks(@Path("userId") userId: String): List<Task>

    // GET all tasks for a specific user by current date or custom date
    @GET("/user/{userId}/task-today")
    fun getTasksByDate(
        @Path("userId") userId: String,
        @Query("date") date: String? = null
    ): List<Task>

    // GET a specific task for a specific user
    @GET("/user/{userId}/task/{id}")
    fun getTaskById(
        @Path("userId") userId: String,
        @Path("id") taskId: String
    ): Task

    // POST a new task for a specific user
    @POST("/user/{userId}/task")
    fun createTask(
        @Path("userId") userId: String,
        @Body task: Task
    ): Call<Task?>

    // PUT update progress for a task
    @PUT("/user/{userId}/task/{id}/update-progress")
    fun updateProgress(
        @Path("userId") userId: String,
        @Path("id") taskId: String,
        @Body progressData: ProgressData
    ): Task

    // DELETE a task
    @DELETE("/user/{userId}/task/{id}")
    fun deleteTask(
        @Path("userId") userId: String,
        @Path("id") taskId: String
    ): DeleteResponse
}