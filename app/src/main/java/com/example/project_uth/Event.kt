package com.example.project_uth

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalTime



data class Event(

    val date: LocalDate,
    val endDate: LocalDate? = null,
    val title: String = "",
    val isAllDay: Boolean = false,

    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    val color: Color? = null,

    val reminderMinutes: Int? = null,
    val repeat: String? = null,
    val calendarType: String? = null,

    val repeatInterval: Int? = null,
    val repeatUnit: String? = null,
    val repeatEndDate: LocalDate? = null,

    )
fun Event.isRepeating(): Boolean = repeatInterval != null && repeatUnit != null