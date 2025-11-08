package com.example.project_uth

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_uth.ui.theme.RedLight
import com.example.project_uth.ui.theme.RedPrimary
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTopAppBar() {
    TopAppBar(
        title = { /* trống */ },
        navigationIcon = {
            IconButton(onClick = { /* mở menu */ }) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
        },
        actions = {
            IconButton(onClick = { /* search */ }) {
                Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(8.dp))
            // Nếu chưa có drawable này, comment 3 dòng dưới
            Image(
                painter = painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = "Avatar",
                modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, Color.Gray, CircleShape)
            )
            Spacer(Modifier.width(16.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun CalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("vi", "VN"))
    val monthTitle = currentMonth.format(formatter).replaceFirstChar { it.uppercase() }
    val daysList = remember(currentMonth) { generateCalendarDays(currentMonth) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Tháng trước")
            }
            Text(monthTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Tháng sau")
            }
        }
        Spacer(Modifier.height(16.dp))
        val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold, color = if (day == "CN") Color.Red else Color.Black
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Column {
            daysList.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        if (date == null) {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val isSelected = date == selectedDate
                            DayCell(
                                date = date,
                                isSelected = isSelected,
                                onDayClick = onDateSelected,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val day = date.dayOfMonth.toString()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> RedPrimary
                    isPressed -> RedLight
                    else -> Color.Transparent
                }
            )
            .clickable(
                onClick = { onDayClick(date) },
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = when {
                isSelected -> Color.White
                isPressed -> RedPrimary
                else -> Color.Black
            },
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun EventList(events: List<Event>, modifier: Modifier = Modifier) {
    if (events.isEmpty()) {
        Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Không có sự kiện nào cho ngày này.", color = Color.Gray, textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(events) { e -> EventCard(event = e) }
        }
    }
}

@Composable
private fun EventCard(event: Event) {
    val cardColor = event.color ?: MaterialTheme.colorScheme.surfaceVariant
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                val timeText = if (event.isAllDay) {
                    "Cả ngày"
                } else {
                    val s = event.startTime?.format(timeFormatter).orEmpty()
                    val e = event.endTime?.format(timeFormatter).orEmpty()
                    if (s.isNotEmpty() && e.isNotEmpty()) "$s - $e" else s
                }
                if (timeText.isNotEmpty()) Text(timeText, fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(Modifier.width(8.dp))
            AvatarStack()
        }
    }
}

@Composable
private fun AvatarStack() {
    // Nếu chưa có drawable avatar_placeholder, comment toàn bộ composable này và chỗ gọi nó.
    Box(contentAlignment = Alignment.CenterEnd) {
        val imageSize = 24.dp
        val overlap = 8.dp
        Image(
            painterResource(R.drawable.avatar_placeholder), "avatar 3",
            modifier = Modifier.padding(end = (overlap * 2)).size(imageSize).clip(CircleShape).border(1.dp, Color.White, CircleShape)
        )
        Image(
            painterResource(R.drawable.avatar_placeholder), "avatar 2",
            modifier = Modifier.padding(end = overlap).size(imageSize).clip(CircleShape).border(1.dp, Color.White, CircleShape)
        )
        Image(
            painterResource(R.drawable.avatar_placeholder), "avatar 1",
            modifier = Modifier.size(imageSize).clip(CircleShape).border(1.dp, Color.White, CircleShape)
        )
    }
}

@Composable
private fun CalendarBottomNavNewStyle(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    BottomAppBar(modifier = modifier, containerColor = Color.Transparent, tonalElevation = 0.dp) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NewBottomNavItem(Icons.Default.CalendarMonth, "Lịch", selected = true) {}
                NewBottomNavItem(Icons.AutoMirrored.Filled.Notes, "Ghi chú") {}
                NewAddButton { navController.navigate("add_event") }
                NewBottomNavItem(Icons.Default.Checklist, "Nhiệm vụ") {}
                NewBottomNavItem(Icons.Default.Settings, "Cài đặt") {}
            }
        }
    }
}

@Composable
private fun NewAddButton(onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 4.dp)) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0F0F0)).clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.Black, modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.height(4.dp))
        Text("", fontSize = 12.sp)
    }
}

@Composable
private fun NewBottomNavItem(
    icon: ImageVector, text: String, selected: Boolean = false, onClick: () -> Unit
) {
    val selectedColor = Color(0xFFE91E63)
    val unselectedColor = Color.Gray
    val iconColor = if (selected) Color.White else unselectedColor
    val textColor = if (selected) selectedColor else unselectedColor

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 4.dp)) {
        Box(
            modifier = if (selected) Modifier.size(40.dp).clip(CircleShape).background(selectedColor)
            else Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = text, tint = iconColor, modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.height(4.dp))
        Text(text, color = textColor, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

private fun generateCalendarDays(currentMonth: YearMonth): List<LocalDate?> {
    val days = mutableListOf<LocalDate?>()
    val firstDay = currentMonth.atDay(1)
    val firstDow = firstDay.dayOfWeek.value // 1=Mon..7=Sun
    val empty = if (firstDow == 1) 0 else firstDow - 1
    repeat(empty) { days.add(null) }
    val numDays = currentMonth.lengthOfMonth()
    for (d in 1..numDays) days.add(currentMonth.atDay(d))
    while (days.size % 7 != 0) days.add(null)
    return days
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarMainScreen(
    navController: NavController,
    initialSelectedDate: LocalDate,
    allEvents: SnapshotStateList<Event>,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialSelectedDate) }

    val dayEvents by remember(allEvents.size, selectedDate) {
        derivedStateOf {
            allEvents.filter { event ->
                event.isOccurringOn(selectedDate)
            }
        }
    }

    Scaffold(
        topBar = { CalendarTopAppBar() },
        bottomBar = { CalendarBottomNavNewStyle(navController = navController) }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .background(Color(0xFFF9F9F9))
        ) {
            CalendarView(
                selectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                    onDateSelected(newDate)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            EventList(
                events = dayEvents,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// --- CHUYỂN HÀM RA ĐÂY ---
// Đặt hàm này ở cuối file, bên ngoài tất cả các composable khác.
// Bạn có thể giữ 'private' nếu chỉ muốn dùng trong file CalendarScreen.kt
private fun Event.isOccurringOn(selectedDate: LocalDate): Boolean {
    // 1. Nếu sự kiện không lặp lại
    if (!this.isRepeating()) {
        if (this.endDate == null) {
            return this.date.isEqual(selectedDate)
        } else {
            // Sự kiện kéo dài nhiều ngày (không lặp lại)
            return !selectedDate.isBefore(this.date) && !selectedDate.isAfter(this.endDate)
        }
    }

    // 2. Nếu là sự kiện lặp lại
    val startDate = this.date
    val repeatEndDate = this.repeatEndDate

    if (selectedDate.isBefore(startDate)) return false
    if (repeatEndDate != null && selectedDate.isAfter(repeatEndDate)) return false

    // 3. Tính toán các lần lặp
    val interval = this.repeatInterval ?: 1
    val unit = this.repeatUnit ?: "Ngày"

    try {
        when (unit) {
            "Ngày" -> {
                val daysBetween = ChronoUnit.DAYS.between(startDate, selectedDate)
                return daysBetween % interval == 0L
            }
            "Tuần" -> {
                val sameDayOfWeek = startDate.dayOfWeek == selectedDate.dayOfWeek
                if (!sameDayOfWeek) return false
                val weeksBetween = ChronoUnit.WEEKS.between(startDate, selectedDate)
                return weeksBetween % interval == 0L
            }
            "Tháng" -> {
                val sameDayOfMonth = startDate.dayOfMonth == selectedDate.dayOfMonth
                if (!sameDayOfMonth) return false
                val monthsBetween = ChronoUnit.MONTHS.between(startDate, selectedDate)
                return monthsBetween % interval == 0L
            }
            "Năm" -> {
                val sameMonthAndDay = (startDate.month == selectedDate.month && startDate.dayOfMonth == selectedDate.dayOfMonth)
                if (!sameMonthAndDay) return false
                val yearsBetween = ChronoUnit.YEARS.between(startDate, selectedDate)
                return yearsBetween % interval == 0L
            }
            else -> return startDate.isEqual(selectedDate)
        }
    } catch (e: Exception) {
        return startDate.isEqual(selectedDate)
    }
}