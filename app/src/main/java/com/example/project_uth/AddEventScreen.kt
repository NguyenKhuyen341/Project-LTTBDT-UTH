package com.example.project_uth

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.project_uth.ui.theme.RedPrimary
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

import java.time.temporal.ChronoUnit

// ---------------------- Cấu hình / dữ liệu tĩnh ----------------------

private val eventColors = listOf(
    Color(0xFFFFAB7F), Color(0xFFF6E788), Color(0xFF9EE07F), Color(0xFF7FD1C5),
    Color(0xFF7ED6FF), Color(0xFF81BCFF), Color(0xFFFF96E3), Color(0xFFDEA6FE),
    Color(0xFFD0DFB4), Color(0xFFA1BDC0), Color(0xFFB2B4B3), Color(0xFFF9A2C4),
    Color(0xFFE91E63)
)

private val reminderOptions = listOf(
    "Khi sự kiện bắt đầu", "5 phút trước", "10 phút trước",
    "15 phút trước", "30 phút trước", "1 giờ trước", "Tùy chỉnh"
)

private val calendarTypeOptions = listOf("Cá nhân", "Công việc", "Sinh nhật", "Ngày lễ")

// Đơn vị lặp lại cho dialog mới
private val repeatUnitOptions = listOf("Ngày", "Tuần", "Tháng", "Năm")

// --------------------------- Màn hình chính ---------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    navController: NavController,
    selectedDate: LocalDate,
    onSaveEvent: (Event) -> Unit
) {
    // --- State form cơ bản ---
    var title by remember { mutableStateOf("") }
    var isAllDay by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(selectedDate) }
    var endDate by remember { mutableStateOf(selectedDate) }
    var startTime by remember { mutableStateOf(LocalTime.of(8, 30)) }
    var endTime by remember { mutableStateOf(LocalTime.of(9, 30)) }
    var reminderLabel by remember { mutableStateOf<String?>("15 phút trước") }
    var calendarType by remember { mutableStateOf(calendarTypeOptions[0]) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    // --- State lặp lại (mới) ---
    var isRepeating by remember { mutableStateOf(false) } // Kích hoạt/tắt lặp lại
    var repeatFrequency by remember { mutableStateOf("Ngày") } // Hàng ngày, hàng tuần...
    var repeatInterval by remember { mutableStateOf(1) } // Lặp lại mỗi N (ngày/tuần...)
    var repeatEndDateLabel by remember { mutableStateOf("Vô tận") } // Chuỗi hiển thị
    var repeatActualEndDate by remember { mutableStateOf<LocalDate?>(null) } // Ngày kết thúc thật

    // --- Dialog flags ---
    var showColorPicker by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showCustomReminderDialog by remember { mutableStateOf(false) }
    var showCalendarTypeDialog by remember { mutableStateOf(false) }
    var showRepeatSettingsDialog by remember { mutableStateOf(false) } // Đã đổi tên

    // --- Formatters / Context ---
    val viDateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("vi", "VN")) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val zone = remember { ZoneId.systemDefault() }
    val ctx = LocalContext.current
    val swatch = selectedColor ?: Color.Gray

    // --- Native date pickers cho startDate/endDate ---
    fun showStartDatePicker() {
        DatePickerDialog(
            ctx,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                val picked = LocalDate.of(y, m + 1, d)
                startDate = picked
                if (endDate.isBefore(picked)) endDate = picked
            },
            startDate.year, startDate.monthValue - 1, startDate.dayOfMonth
        ).show()
    }

    fun showEndDatePicker() {
        val minMillis = startDate.atStartOfDay(zone).toInstant().toEpochMilli()
        DatePickerDialog(
            ctx,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                endDate = LocalDate.of(y, m + 1, d)
            },
            endDate.year, endDate.monthValue - 1, endDate.dayOfMonth
        ).apply {
            datePicker.minDate = minMillis
        }.show()
    }

    fun openTimePicker(initial: LocalTime, onPicked: (LocalTime) -> Unit) {
        TimePickerDialog(
            ctx, { _, h, min -> onPicked(LocalTime.of(h, min)) },
            initial.hour, initial.minute, true
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val e = Event(
                                date = startDate,
                                endDate = if (endDate != startDate) endDate else null,
                                title = title.ifBlank { "(Không có tiêu đề)" },
                                isAllDay = isAllDay,
                                startTime = if (isAllDay) null else startTime,
                                endTime = if (isAllDay) null else endTime,
                                color = selectedColor,
                                reminderMinutes = reminderLabel.toMinutesOrNull(),
                                calendarType = calendarType,
                                // Lặp lại (mới) — nếu không lặp lại thì để null
                                repeatInterval = if (!isRepeating) null else repeatInterval,
                                repeatUnit = if (!isRepeating) null else repeatFrequency,
                                repeatEndDate = if (!isRepeating || repeatEndDateLabel == "Vô tận") null else repeatActualEndDate
                            )
                            onSaveEvent(e)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                    ) { Text("Lưu") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Tiêu đề + màu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Box(
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(swatch)
                        .clickable { showColorPicker = true }
                )
                Spacer(Modifier.width(16.dp))
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    placeholder = { Text("Nhập sự kiện ở đây.") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
            HorizontalDivider()

            // Cả ngày
            EventOptionRow(Icons.Default.Schedule, "Cả ngày") {
                Switch(checked = isAllDay, onCheckedChange = { isAllDay = it })
            }

            // Ngày / Giờ
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isAllDay) Icons.Default.CalendarToday else Icons.Default.AccessTime,
                    contentDescription = null, tint = Color.Gray
                )
                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        startDate.format(viDateFormatter),
                        style = if (isAllDay) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
                        color = if (isAllDay) LocalContentColor.current else Color.Gray,
                        modifier = Modifier.clickable { showStartDatePicker() }
                    )
                    if (!isAllDay) {
                        Text(
                            startTime.format(timeFormatter),
                            style = MaterialTheme.typography.titleLarge, fontSize = 20.sp,
                            modifier = Modifier.clickable {
                                openTimePicker(startTime) { picked ->
                                    startTime = picked
                                    if (startDate == endDate && endTime.isBefore(startTime)) {
                                        endTime = startTime.plusHours(1)
                                    }
                                }
                            }
                        )
                    }
                }

                Text(if (isAllDay) " - " else "|", color = Color.LightGray, modifier = Modifier.padding(horizontal = 8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        endDate.format(viDateFormatter),
                        style = if (isAllDay) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
                        color = if (isAllDay) LocalContentColor.current else Color.Gray,
                        modifier = Modifier.clickable { showEndDatePicker() }
                    )
                    if (!isAllDay) {
                        Text(
                            endTime.format(timeFormatter),
                            style = MaterialTheme.typography.titleLarge, fontSize = 20.sp,
                            // *** DÒNG CODE SỬA LỖI 'picked' LÀ DÒNG DƯỚI ĐÂY ***
                            modifier = Modifier.clickable {
                                openTimePicker(endTime) { picked -> // <--- Đã thêm lại hàm bọc này
                                    endTime =
                                        if (startDate == endDate && picked.isBefore(startTime)) startTime.plusHours(1)
                                        else picked
                                } // <--- Và dấu ngoặc này
                            }
                        )
                    }
                }
            }
            HorizontalDivider()

            // Lời nhắc
            EventOptionRowSelectable(
                icon = Icons.Default.Notifications, text = "Lời nhắc", value = reminderLabel ?: "Thêm",
                onClick = { showReminderDialog = true },
                trailingContent = {
                    if (reminderLabel != null) {
                        IconButton(onClick = { reminderLabel = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Xoá", tint = Color.Gray)
                        }
                    } else {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            )

            // Báo động toàn màn hình (demo)
            EventOptionRow(Icons.Default.Alarm, "Báo động toàn màn hình") {
                Text("PRO", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(8.dp))
                Switch(checked = false, onCheckedChange = {}, enabled = false)
            }

            // Lặp lại (ĐÃ THAY ĐỔI)
            EventOptionRowSelectable(
                icon = Icons.Default.Repeat,
                text = "Lặp lại",
                value = if (isRepeating) {
                    val intervalText = if (repeatInterval > 1) "$repeatInterval " else ""
                    val unitText = when (repeatFrequency) {
                        "Ngày" -> "ngày"
                        "Tuần" -> "tuần"
                        "Tháng" -> "tháng"
                        "Năm" -> "năm"
                        else -> ""
                    }
                    "Mỗi $intervalText$unitText"
                } else "Không lặp lại",
                onClick = { showRepeatSettingsDialog = true } // Đã đổi tên dialog
            )


            // Lịch
            EventOptionRowSelectable(
                icon = Icons.Default.CalendarToday, text = "Lịch", value = calendarType,
                onClick = { showCalendarTypeDialog = true },
                leadingContent = { Box(Modifier.size(10.dp).background(color = Color.Green, shape = CircleShape)) }
            )

            Spacer(Modifier.height(32.dp))
        }

        // --------------------- Dialogs ---------------------

        if (showColorPicker) {
            ColorPickerDialog(
                availableColors = eventColors,
                selectedColor = selectedColor,
                onColorSelected = { c -> selectedColor = c; showColorPicker = false },
                onDismiss = { showColorPicker = false }
            )
        }

        if (showReminderDialog) {
            ReminderSelectionDialog(
                title = "Thiết lập nhắc nhở",
                options = reminderOptions,
                currentSelection = reminderLabel,
                onSelectionConfirmed = { selected ->
                    showReminderDialog = false
                    if (selected == "Tùy chỉnh") {
                        showCustomReminderDialog = true
                    } else {
                        reminderLabel = selected
                    }
                },
                onDismiss = { showReminderDialog = false }
            )
        }

        if (showCustomReminderDialog) {
            CustomReminderDialog(
                initialValue = reminderLabel,
                onConfirm = { number, unit ->
                    val unitLabel = when (unit) {
                        "Ngày" -> "ngày"
                        "Tuần" -> "tuần"
                        "Giờ" -> "giờ"
                        else -> "phút"
                    }
                    reminderLabel = "$number $unitLabel trước"
                    showCustomReminderDialog = false
                },
                onDismiss = { showCustomReminderDialog = false }
            )
        }

        if (showCalendarTypeDialog) {
            ReminderSelectionDialog(
                title = "Chọn loại lịch",
                options = calendarTypeOptions,
                currentSelection = calendarType,
                onSelectionConfirmed = { selected ->
                    calendarType = selected ?: calendarType
                    showCalendarTypeDialog = false
                },
                onDismiss = { showCalendarTypeDialog = false }
            )
        }

        // Dialog lặp lại MỚI (ĐÃ THAY THẾ)
        if (showRepeatSettingsDialog) { // Đã đổi tên biến flag
            RepeatSettingsDialog( // Đã đổi tên Composable
                initialIsRepeating = isRepeating,
                initialFrequency = repeatFrequency,
                initialInterval = repeatInterval,
                initialEndDateLabel = repeatEndDateLabel,
                initialActualEndDate = repeatActualEndDate,
                onDismiss = { showRepeatSettingsDialog = false },
                onConfirm = { _isRepeating, _frequency, _interval, _endDateLabel, _actualEndDate ->
                    isRepeating = _isRepeating
                    repeatFrequency = _frequency
                    repeatInterval = _interval
                    repeatEndDateLabel = _endDateLabel
                    repeatActualEndDate = _actualEndDate
                    showRepeatSettingsDialog = false
                }
            )
        }
    }
}

// --------------------- Composables phụ trợ ---------------------

@Composable
private fun EventOptionRow(
    icon: ImageVector,
    text: String,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(Modifier.width(16.dp))
        Text(text, modifier = Modifier.weight(1f))
        content()
    }
}

@Composable
private fun EventOptionRowSelectable(
    icon: ImageVector, text: String, value: String, onClick: () -> Unit,
    leadingContent: @Composable () -> Unit = {},
    trailingContent: @Composable () -> Unit = {
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(Modifier.width(16.dp))
        Text(text, modifier = Modifier.weight(1f))
        leadingContent()
        Spacer(Modifier.width(8.dp))
        Text(value, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(4.dp))
        trailingContent()
    }
}

// Chọn màu
@Composable
private fun ColorPickerDialog(
    availableColors: List<Color>,
    selectedColor: Color?,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Tùy chỉnh màu sự kiện", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableColors) { color ->
                        ColorItem(color = color, isSelected = color == selectedColor) { onColorSelected(color) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorItem(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(
                BorderStroke(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) Icon(Icons.Default.Check, contentDescription = "Đã chọn")
    }
}

// Dialog chọn 1 item (tái dùng cho Lời nhắc / Loại lịch)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSelectionDialog(
    title: String,
    options: List<String>,
    currentSelection: String?,
    onSelectionConfirmed: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSelection by remember { mutableStateOf(currentSelection) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { tempSelection = option }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (tempSelection == option),
                            onClick = { tempSelection = option },
                            colors = RadioButtonDefaults.colors(selectedColor = RedPrimary)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSelectionConfirmed(tempSelection) },
                colors = ButtonDefaults.textButtonColors(contentColor = RedPrimary)
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = RedPrimary)
            ) { Text("HỦY") }
        }
    )
}

// Dialog “Nhắc nhở tùy chỉnh”
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomReminderDialog(
    initialValue: String?,
    onConfirm: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    val (initialNumber, initialUnit) = remember(initialValue) { parseCustomReminder(initialValue) }
    var numberInput by remember { mutableStateOf(initialNumber.toString()) }
    var selectedUnit by remember { mutableStateOf(initialUnit) }
    val units = listOf("Phút", "Giờ", "Ngày", "Tuần")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thời gian nhắc nhở Tùy chỉnh") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = numberInput,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) numberInput = newValue.take(3)
                        },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = { Text("Số") }
                    )
                    Spacer(Modifier.width(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedUnit, onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = { selectedUnit = unit; expanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Trước")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val num = numberInput.toIntOrNull()?.takeIf { it > 0 } ?: 1
                    onConfirm(num, selectedUnit)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = RedPrimary)
            ) { Text("LƯU") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = RedPrimary)
            ) { Text("HỦY") }
        }
    )
}

// Dialog LẶP LẠI MỚI - Theo giao diện ảnh thứ 2
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatSettingsDialog(
    initialIsRepeating: Boolean,
    initialFrequency: String, // "Ngày", "Tuần", "Tháng", "Năm"
    initialInterval: Int,
    initialEndDateLabel: String, // "Vô tận", "Đến một ngày: ..."
    initialActualEndDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (
        isRepeating: Boolean,
        frequency: String,
        interval: Int,
        endDateLabel: String,
        actualEndDate: LocalDate?
    ) -> Unit
) {
    val ctx = LocalContext.current
    val zone = remember { ZoneId.systemDefault() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("vi", "VN")) }

    var isRepeatingState by remember { mutableStateOf(initialIsRepeating) }
    var selectedFrequency by remember { mutableStateOf(initialFrequency) } // "Ngày", "Tuần", "Tháng", "Năm"
    var interval by remember { mutableStateOf(initialInterval.toString()) }
    var endDateLabel by remember { mutableStateOf(initialEndDateLabel) }
    var actualEndDate by remember { mutableStateOf(initialActualEndDate) }

    var showEndDateDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = actualEndDate?.atStartOfDay(zone)?.toInstant()?.toEpochMilli()
            ?: LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli()
    )


    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Đặt lặp lại", style = MaterialTheme.typography.titleLarge)
                    Switch(checked = isRepeatingState, onCheckedChange = { isRepeatingState = it })
                }
                Spacer(Modifier.height(16.dp))

                // Các lựa chọn tần suất (Hàng ngày, Hàng tuần...)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val frequencies = listOf("Ngày", "Tuần", "Tháng", "Năm")
                    frequencies.forEach { freq ->
// Code mới đã sửa lỗi
                        FilterChip(
                            selected = (selectedFrequency == freq),
                            onClick = { selectedFrequency = freq },
                            label = { Text("Hàng $freq".lowercase()) },
                            enabled = isRepeatingState, // 'enabled' giờ được đặt ở đây
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedPrimary.copy(alpha = 0.1f),
                                selectedLabelColor = RedPrimary,
                                // Bạn có thể tùy chỉnh màu cho trạng thái không được chọn (unselected) và bị vô hiệu hóa (disabled)
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                selected = (selectedFrequency == freq), // 'selected' được truyền vào đây
                                enabled = isRepeatingState, // 'enabled' cũng được truyền vào đây
                                selectedBorderColor = RedPrimary,
                                borderColor = Color.Transparent,
                                borderWidth = 1.dp
                            ),
                            modifier = Modifier.weight(1f)
                        )

                    }
                }
                Spacer(Modifier.height(16.dp))

                // Lặp lại mỗi N (Ngày/Tuần/Tháng/Năm)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isRepeatingState) {
                            // Mở dialog riêng để nhập số và chọn đơn vị (nếu cần phức tạp)
                            // Hiện tại dùng TextField và Text
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Lặp lại mỗi",
                        modifier = Modifier.weight(1f),
                        color = if (isRepeatingState) LocalContentColor.current else Color.Gray
                    )
                    Text(
                        "$interval $selectedFrequency >",
                        color = if (isRepeatingState) RedPrimary else Color.Gray
                    )
                }
                HorizontalDivider()

                // Sự lặp lại kết thúc
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isRepeatingState) { showEndDateDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Sự lặp lại kết thúc",
                        modifier = Modifier.weight(1f),
                        color = if (isRepeatingState) LocalContentColor.current else Color.Gray
                    )
                    Text(
                        "$endDateLabel >",
                        color = if (isRepeatingState) RedPrimary else Color.Gray
                    )
                }
                HorizontalDivider()

                Spacer(Modifier.height(24.dp))

                // Nút HỦY / LÀM XONG
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", color = RedPrimary)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val finalInterval = interval.toIntOrNull()?.coerceAtLeast(1) ?: 1
                            onConfirm(
                                isRepeatingState,
                                selectedFrequency,
                                finalInterval,
                                endDateLabel,
                                actualEndDate
                            )
                        }
                    ) {
                        Text("Làm xong", color = RedPrimary)
                    }
                }
            }
        }
    }

    // Dialog chọn ngày kết thúc
    if (showEndDateDialog) {
        DatePickerDialog(
            onDismissRequest = { showEndDateDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        actualEndDate = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
                        endDateLabel = actualEndDate!!.format(dateFormatter)
                    }
                    showEndDateDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    // Tùy chọn: Đặt lại về "Vô tận" nếu hủy
                    // actualEndDate = null
                    // endDateLabel = "Vô tận"
                    showEndDateDialog = false
                }) { Text("HỦY") }
            },
            colors = DatePickerDefaults.colors(selectedDayContainerColor = RedPrimary)
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


// -------------------------- Helpers (Không đổi) --------------------------

private fun String?.toMinutesOrNull(): Int? {
    if (this == null) return null
    val parts = this.split(" ")
    if (parts.size == 3 && parts[2].equals("trước", ignoreCase = true)) {
        val number = parts[0].toIntOrNull()
        if (number != null) {
            return when (parts[1].lowercase()) {
                "phút" -> number
                "giờ" -> number * 60
                "ngày" -> number * 60 * 24
                "tuần" -> number * 60 * 24 * 7
                else -> null
            }
        }
    }
    return when (this) {
        "Khi sự kiện bắt đầu" -> 0
        "5 phút trước" -> 5
        "10 phút trước" -> 10
        "15 phút trước" -> 15
        "30 phút trước" -> 30
        "1 giờ trước" -> 60
        else -> null
    }
}

private fun parseCustomReminder(label: String?): Pair<Int, String> {
    if (label == null) return 15 to "Phút"
    val parts = label.split(" ")
    if (parts.size == 3 && parts[2].equals("trước", ignoreCase = true)) {
        val number = parts[0].toIntOrNull()
        val unit = when (parts[1].lowercase()) {
            "phút" -> "Phút"
            "giờ" -> "Giờ"
            "ngày" -> "Ngày"
            "tuần" -> "Tuần"
            else -> null
        }
        if (number != null && unit != null) return number to unit
    }
    val defaultMinutes = when (label) {
        "Khi sự kiện bắt đầu" -> 0
        "5 phút trước" -> 5
        "10 phút trước" -> 10
        "15 phút trước" -> 15
        "30 phút trước" -> 30
        "1 giờ trước" -> 60
        else -> 15
    }
    val defaultUnit = if (label == "1 giờ trước") "Giờ" else "Phút"
    val defaultNumber = if (label == "1 giờ trước") 1 else defaultMinutes
    return defaultNumber to defaultUnit
}

@Composable
private fun IntervalPickerDialog(
    unit: String, // "Ngày", "Tuần", ...
    initialInterval: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedInterval by remember { mutableStateOf(initialInterval) }
    val options = (1..30).toList() // Tạo danh sách từ 1 đến 30
    // Tự động cuộn đến mục đã chọn ban đầu
    val scrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = (initialInterval - 1).coerceAtLeast(0)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    "Lặp lại mỗi",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(16.dp))
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp) // Giới hạn chiều cao
                ) {
                    items(options) { number ->
                        val isSelected = number == selectedInterval
                        val text = "$number ${unit.lowercase()}"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedInterval = number }
                                .background(if (isSelected) RedPrimary.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text,
                                color = if (isSelected) RedPrimary else LocalContentColor.current,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("HỦY", color = RedPrimary) }
                    TextButton(onClick = { onConfirm(selectedInterval) }) { Text("OK", color = RedPrimary) }
                }
            }
        }
    }
}