package uas.pam.habiter.model

import java.util.Date

data class ProgressData(
    val date: Date? = null,
    val totalDone: Int? = null,
    val totalTask: Int? = null
)
