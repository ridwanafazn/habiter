package uas.pam.habiter.model

import java.util.Date

data class Task(
    val _id: String? = null,
    val userId: String?,
    val title: String?,
    val label: String?,
    val date: Date? = null,
    val status: String? = null,
)