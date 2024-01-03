package uas.pam.habiter.model;

import java.util.Date;

data class Task(
    val userId: String,
    val title: String,
    val type: String,
    val startDate: Date,
    val endDate: Date? = null, // Optional endDate
    val progress: Progress
) {
    data class Progress(
        val date: Date,
        val totalDone: Int,
        val totalTask: Int,
        val dailyProgress: String = "none" // Default dailyProgress
    )
}
