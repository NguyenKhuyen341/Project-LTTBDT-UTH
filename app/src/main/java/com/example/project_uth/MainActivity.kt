package com.example.project_uth

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_uth.ui.theme.Project_uthTheme
import com.example.project_uth.ui.user.ForgotPasswordScreen1
import com.example.project_uth.ui.user.ForgotPasswordScreen2
import com.example.project_uth.ui.user.ForgotPasswordScreen3
import com.example.project_uth.ui.user.LoginScreen
import com.example.project_uth.ui.user.RegisterScreen
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project_uthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val nav = rememberNavController()

                    // --- 1. Lấy ViewModel ---
                    val application = LocalContext.current.applicationContext as Application
                    val eventViewModel: EventViewModel = viewModel(
                        factory = EventViewModelFactory(application)
                    )

                    // --- 2. Biến lưu ngày hiện tại ---
                    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

                    // --- 3. Lấy danh sách sự kiện từ ViewModel ---
                    val allEvents by eventViewModel.allEvents.collectAsState(initial = emptyList())

                    // --- 4. Dữ liệu ghi chú (được lưu khi quay lại màn hình) ---
                    val savedNotes = rememberSaveable(
                        saver = listSaver(
                            save = { list ->
                                list.flatMap { listOf(it.title, it.content, it.time) }
                            },
                            restore = { saved ->
                                saved.chunked(3)
                                    .map { chunk ->
                                        val (title, content, time) = chunk
                                        Note(title, content, time)
                                    }
                                    .toMutableStateList()
                            }
                        )
                    ) { mutableStateListOf<Note>() }

                    // --- 5. Định nghĩa NavHost ---
                    NavHost(navController = nav, startDestination = "calendar") {

                        // --- Màn hình Lịch ---
                        composable("calendar") {
                            CalendarMainScreen(
                                navController = nav,
                                initialSelectedDate = selectedDate,
                                allEvents = allEvents,
                                onDateSelected = { d -> selectedDate = d },
                                onDeleteEvent = { event -> eventViewModel.deleteEvent(event) },
                                onUpdateEvent = { _, newEvent -> eventViewModel.updateEvent(newEvent) }
                            )
                        }

                        composable("add_event") {
                            AddEventScreen(
                                navController = nav,
                                selectedDate = selectedDate,
                                onSaveEvent = { e -> eventViewModel.insertEvent(e) }
                            )
                        }

                        composable("notes") {
                            val savedNotes = remember { mutableStateListOf<Note>() }
                            NotesScreen(navController = nav, savedNotes = savedNotes)
                        }


                        composable("tasks") {
                            TasksScreen(navController = nav)
                        }
                        composable("settings") {
                            SettingsScreen(navController = nav)
                        }

                        composable("login") {
                            LoginScreen(navController = nav)
                        }
                        composable("register") {
                            RegisterScreen(navController = nav)
                        }
                        composable("forgot_password_1") {
                            ForgotPasswordScreen1(navController = nav)
                        }
                        composable("forgot_password_2") {
                            ForgotPasswordScreen2(navController = nav)
                        }
                        composable("forgot_password_3") {
                            ForgotPasswordScreen3(navController = nav)
                        }
                    }
                }
            }
        }
    }
}
