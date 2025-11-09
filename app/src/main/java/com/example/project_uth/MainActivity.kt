package com.example.project_uth

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
import java.time.LocalTime
import com.example.project_uth.ui.user.ForgotPasswordScreen1
import com.example.project_uth.ui.user.ForgotPasswordScreen2
import com.example.project_uth.ui.user.ForgotPasswordScreen3
import com.example.project_uth.ui.user.LoginScreen
import com.example.project_uth.ui.user.RegisterScreen

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

                    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
                    val allEvents = remember {
                        mutableStateListOf<Event>()
                    }

                    // Đổi startDestination thành "login"
                    NavHost(navController = nav, startDestination = "calendar") {

                        // Các route cho Lịch (Calendar) của bạn
                        composable("calendar") {
                            CalendarMainScreen(
                                navController = nav,
                                initialSelectedDate = selectedDate,
                                allEvents = allEvents,
                                onDateSelected = { d -> selectedDate = d },
                                onDeleteEvent = { event -> allEvents.remove(event) },
                                onUpdateEvent = { oldEvent, newEvent -> val index = allEvents.indexOf(oldEvent);
                                    if (index != -1) {
                                        allEvents[index] = newEvent
                                    } },
                            )
                        }
                        composable("add_event") {
                            AddEventScreen(
                                navController = nav,
                                selectedDate = selectedDate,
                                onSaveEvent = { e -> allEvents.add(e) }
                            )
                        }

                        // 5 route cho các màn hình Xác thực (Auth)
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