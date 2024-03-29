package com.devinidn.smartalarm.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm(
    @PrimaryKey( autoGenerate = true)
    val id: Int,
    val date: String,
    val time: String,
    val massage: String
)
