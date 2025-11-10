package com.example.project_uth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ===== DỮ LIỆU NHIỆM VỤ MẪU =====
data class Task(
    val title: String,
    val time: String,
    val color: Color,
    val progress: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(navController: NavController) {
    val todayTasks1 = listOf(
        Task("Hoàn thành báo cáo", "11:00 AM", Color(0xFFB2EBF2)),
        Task("Soạn bài thuyết trình", "5:00 AM", Color(0xFF80DEEA))
    )

    val todayTasks2 = listOf(
        Task("Gọi cho khách hàng", "12:00 AM", Color(0xFFFFFF99))
    )

    val upcomingTasks = listOf(
        Task("Họp dự án mới", "7:00 AM", Color(0xFFE1BEE7)),
        Task("Cuộc họp quan trọng", "9:00 AM", Color(0xFFD1C4E9))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nhiệm vụ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = null) }

                }
            )
        },
                    bottomBar = {
                        CalendarBottomNavNewStyle(navController = navController)
                    }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DayHeader("thứ 3", "15 tháng 10")
            }

            item { SectionTitle("Nhiệm vụ hôm nay 1") }
            items(todayTasks1) { TaskCard(it) }

            item { SectionTitle("Nhiệm vụ hôm nay 2") }
            items(todayTasks2) { TaskCard(it) }

            item { SectionTitle("Nhiệm vụ sắp tới") }
            items(upcomingTasks) { TaskCard(it) }
        }
    }
}

@Composable
fun DayHeader(dayOfWeek: String, dateText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(color = Color(0xFFFF3366), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(dayOfWeek, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Text(dateText, color = Color.Gray)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(task.color)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ô phần trăm tròn
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.White.copy(alpha = 0.4f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${task.progress}%",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(task.time, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
