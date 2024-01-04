package uas.pam.habiter.model

import java.util.Date

data class Task(
    val title: String,
    val type: String,
    val repeatDay: List<Int>? = null,
    val startDate: Date? = Date(),
    val endDate: Date? = null,
    @Transient
    val progress: (() -> Unit)?= null
) {
    data class Progress(
        val date: Date,
        val totalDone: Int,
        val totalTask: Int,
        val dailyProgress: String = "none"
    )
}

//data class Task(
//    val title: String,
//    val type: String,
//    val repeatDay: List<Int>? = null,
//    val startDate: Date? = Date(),
//    val endDate: Date? = null,
//    val progress: Progress = Progress()
//) {
//    constructor(): this("","",null, null,null, Progress())
//    data class Progress(
//        val date: Date,
//        val totalDone: Int,
//        val totalTask: Int,
//        val dailyProgress: String = "none"
//    ){
//        constructor(): this(Date(), 0,0,"none")
//    }
//}