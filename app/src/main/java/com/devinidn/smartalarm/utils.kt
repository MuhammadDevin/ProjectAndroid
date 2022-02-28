package com.devinidn.smartalarm

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

fun timeFormatter(hour: Int, minute: Int): String{
    val calendar = getInstance()
    calendar.set(0,0,0, hour, minute)
    val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault())

    return timeFormatted.format(calendar.time)
}