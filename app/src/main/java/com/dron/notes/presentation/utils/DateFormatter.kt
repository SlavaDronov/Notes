package com.dron.notes.presentation.utils


import android.icu.text.DateFormat
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object DateFormatter {
    val millisInHour = TimeUnit.HOURS.toMillis(1)
    val millisInDay = TimeUnit.HOURS.toMillis(1)
    val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)


    fun formatCurrentDate(): String {
        return formatter.format(System.currentTimeMillis())
    }
    fun formatDateToString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < millisInHour -> "Just Now"
            diff < millisInDay -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours h ago"
            }
            else -> {
                formatter.format(timestamp)
            }
        }
    }
}