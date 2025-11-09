package com.example.project_uth

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {

    // --- Chuyển đổi cho LocalDate ---
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }
    @TypeConverter
    fun dateToEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    // --- Chuyển đổi cho LocalTime ---
    @TypeConverter
    fun fromSecondOfDay(value: Int?): LocalTime? {
        return value?.let { LocalTime.ofSecondOfDay(it.toLong()) }
    }
    @TypeConverter
    fun timeToSecondOfDay(time: LocalTime?): Int? {
        return time?.toSecondOfDay()
    }

    // --- Chuyển đổi cho List<LocalDate> (dùng cho exceptionDates) ---
    // Biến nó thành một String, ví dụ: "19320,19321,19322"
    @TypeConverter
    fun fromEpochDayString(value: String?): List<LocalDate> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(',').map { LocalDate.ofEpochDay(it.toLong()) }
    }
    @TypeConverter
    fun dateListToEpochDayString(dates: List<LocalDate>?): String? {
        return dates?.joinToString(",") { it.toEpochDay().toString() }
    }
}