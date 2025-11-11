package com.example.project_uth

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

// 1. Đánh dấu đây là một "Bảng" trong database
@Entity(tableName = "events")
data class Event(

    // 2. Thêm một "Khóa chính" (ID) tự động tăng
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 0 nghĩa là "để Room tự động tạo"

    // --- Các trường còn lại giữ nguyên (lấy từ file bạn gửi) ---
    val date: LocalDate,
    val endDate: LocalDate? = null,
    val title: String = "",
    val isAllDay: Boolean = false,

    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    val colorInt: Int? = null,

    val reminderMinutes: Int? = null,
    val repeat: String? = null,
    val calendarType: String? = null,

    val repeatInterval: Int? = null,
    val repeatUnit: String? = null,
    val repeatEndDate: LocalDate? = null,

    val exceptionDates: List<LocalDate> = emptyList()
)

// Hàm helper (giữ nguyên)
fun Event.isRepeating(): Boolean = repeatInterval != null && repeatUnit != null