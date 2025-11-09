package com.example.project_uth

import android.app.Application // <-- IMPORT MỚI
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_uth.ui.theme.Project_uthTheme
import java.time.LocalDate
// import java.time.LocalTime // (Không cần dùng ở đây nữa)
import com.example.project_uth.ui.user.ForgotPasswordScreen1
import com.example.project_uth.ui.user.ForgotPasswordScreen2
import com.example.project_uth.ui.user.ForgotPasswordScreen3
import com.example.project_uth.ui.user.LoginScreen
import com.example.project_uth.ui.user.RegisterScreen
// --- THÊM CÁC IMPORT NÀY ---
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

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

                    // --- 1. LẤY VIEWMODEL ---
                    // Lấy application context
                    val application = LocalContext.current.applicationContext as Application
                    // Tạo ViewModel bằng Factory
                    val eventViewModel: EventViewModel = viewModel(
                        factory = EventViewModelFactory(application)
                    )

                    // --- 2. LẤY DỮ LIỆU TỪ VIEWMODEL ---
                    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

                    // Lấy allEvents từ ViewModel.
                    // .collectAsState() sẽ tự động "lắng nghe" Flow
                    // và cập nhật (recompose) UI khi có dữ liệu mới.
                    val allEvents by eventViewModel.allEvents.collectAsState(initial = emptyList())

                    // (Bạn có thể đổi startDestination thành "login" nếu muốn)
                    NavHost(navController = nav, startDestination = "calendar") {

                        // Các route cho Lịch (Calendar) của bạn
                        composable("calendar") {
                            CalendarMainScreen(
                                navController = nav,
                                initialSelectedDate = selectedDate,
                                // --- 3. SỬA DỮ LIỆU ---
                                allEvents = allEvents, // <--- Đã sửa (lấy từ ViewModel)
                                onDateSelected = { d -> selectedDate = d },

                                // Gọi các hàm của ViewModel
                                onDeleteEvent = { event -> eventViewModel.deleteEvent(event) },
                                // Sửa lại onUpdateEvent, chỉ cần truyền event mới (Room tự biết)
                                onUpdateEvent = { oldEvent, newEvent -> eventViewModel.updateEvent(newEvent) }
                            )
                        }
                        composable("add_event") {
                            AddEventScreen(
                                navController = nav,
                                selectedDate = selectedDate,
                                // Gọi hàm của ViewModel
                                onSaveEvent = { e -> eventViewModel.insertEvent(e) }
                            )
                        }

                        // 5 route cho các màn hình Xác thực (Auth)
                        // (Giữ nguyên)
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