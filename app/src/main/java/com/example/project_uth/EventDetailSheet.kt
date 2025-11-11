package com.example.project_uth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_uth.ui.theme.RedPrimary
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class DeleteType { SINGLE, ALL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailSheet(
    event: Event,
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onDelete: (Event, DeleteType, LocalDate) -> Unit,
    onEdit: (Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val timeFormatter = remember(event.isAllDay) {
        if (event.isAllDay) DateTimeFormatter.ofPattern("EEEE, dd MMM", Locale("vi", "VN"))
        else DateTimeFormatter.ofPattern("HH:mm", Locale("vi", "VN"))
    }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale("vi", "VN")) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showRepeatingDeleteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    actions = {
                        IconButton(onClick = { /* TODO: More actions */ }) {
                            Icon(Icons.Default.MoreVert, "Tùy chọn khác")
                        }
                        IconButton(onClick = {
                            if (event.isRepeating()) {
                                showRepeatingDeleteDialog = true
                            } else {
                                showDeleteConfirmDialog = true
                            }
                        }) {
                            Icon(Icons.Default.DeleteOutline, "Xóa", tint = RedPrimary)
                        }
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, "Chia sẻ")
                        }
                        IconButton(onClick = { onEdit(event) }) {
                            Icon(Icons.Default.Edit, "Chỉnh sửa")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(event.colorInt?.let { Color(it) } ?: Color.Gray)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        event.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(24.dp))

                InfoRow(
                    icon = Icons.Default.AccessTime,
                    text = if (event.isAllDay) "Cả ngày" else "Bắt đầu",
                    value = if (event.isAllDay) event.date.format(dateFormatter)
                    else "${event.date.format(dateFormatter)}, ${event.startTime?.format(timeFormatter)}"
                )
                Box(Modifier.height(24.dp).padding(start = 12.dp)) {
                    Spacer(Modifier.width(1.dp).fillMaxHeight().background(Color.LightGray))
                }
                InfoRow(
                    icon = null,
                    text = if (event.isAllDay) "Kết thúc" else "Kết thúc",
                    value = if (event.isAllDay) (event.endDate ?: event.date).format(dateFormatter)
                    else "${(event.endDate ?: event.date).format(dateFormatter)}, ${event.endTime?.format(timeFormatter)}"
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    text = "Lịch",
                    value = event.calendarType ?: "Cá nhân"
                )

                Spacer(Modifier.height(24.dp))

                InfoRow(
                    icon = Icons.Default.Notifications,
                    text = "Lời nhắc",
                    value = event.reminderMinutes?.toReminderString() ?: "Không có"
                )
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Xóa sự kiện?") },
            text = { Text("Bạn có chắc muốn xóa sự kiện \"${event.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(event, DeleteType.ALL, selectedDate)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = RedPrimary)
                ) { Text("Xóa") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Hủy") }
            }
        )
    }

    if (showRepeatingDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showRepeatingDeleteDialog = false },
            title = { Text("Xóa sự kiện lặp lại?") },
            text = { Text("Bạn muốn xóa chỉ sự kiện này, hay tất cả các sự kiện trong chuỗi?") },
            confirmButton = {
                Column(Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            onDelete(event, DeleteType.SINGLE, selectedDate)
                            showRepeatingDeleteDialog = false
                        }
                    ) { Text("Chỉ sự kiện này", color = RedPrimary) }

                    TextButton(
                        onClick = {
                            onDelete(event, DeleteType.ALL, selectedDate)
                            showRepeatingDeleteDialog = false
                        }
                    ) { Text("Tất cả sự kiện", color = RedPrimary) }
                }
            },
            dismissButton = {
                TextButton(onClick = { showRepeatingDeleteDialog = false }) { Text("Hủy") }
            }
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector?, text: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(icon, contentDescription = text, tint = Color.Gray, modifier = Modifier.size(24.dp))
        } else {
            Spacer(Modifier.width(24.dp))
        }
        Spacer(Modifier.width(24.dp))
        Column {
            Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontSize = 16.sp)
        }
    }
}

private fun Int.toReminderString(): String {
    return when (this) {
        0 -> "Khi sự kiện bắt đầu"
        5 -> "5 phút trước"
        10 -> "10 phút trước"
        15 -> "15 phút trước"
        30 -> "30 phút trước"
        60 -> "1 giờ trước"
        else -> if (this > 60) "${this / 60} giờ trước" else "$this phút trước"
    }
}