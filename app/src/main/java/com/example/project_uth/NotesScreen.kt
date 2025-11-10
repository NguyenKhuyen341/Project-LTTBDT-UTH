package com.example.project_uth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Note(
    val title: String,
    val content: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavHostController,
    savedNotes: MutableList<Note>
) {
    var noteText by remember { mutableStateOf("") }
    val dateFormatter = DateTimeFormatter.ofPattern("h:mm a, d MMM yyyy")

    Scaffold(
        bottomBar = {
            // ✅ Thêm thanh bottom navigation vào đây
            CalendarBottomNavNewStyle(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F5F6))
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // --- Tiêu đề ---
            Text(
                text = "Ghi chú",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            // --- Ngày hiện tại ---
            Text(
                text = "Hôm nay, " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("d MMM, yyyy")),
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Ô nhập ghi chú ---
            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 3.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    TextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("Nhập ghi chú...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                val newNote = Note(
                                    title = "Ghi chú mới",
                                    content = noteText,
                                    time = LocalDateTime.now().format(dateFormatter)
                                )
                                savedNotes.add(0, newNote)
                                noteText = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        ),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Lưu", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray)
            Text(
                text = "Đã lưu",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Divider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))

            // --- Danh sách ghi chú ---
            LazyColumn {
                items(savedNotes) { note ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = note.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = note.content,
                                fontSize = 13.sp
                            )
                            Text(
                                text = note.time,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}
