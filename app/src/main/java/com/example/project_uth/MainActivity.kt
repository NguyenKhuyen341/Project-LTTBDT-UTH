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


class MainActivity : ComponentActivity() {

    private fun createFakeDatabase(): List<Event> {
        val today = LocalDate.now()
        val dayAfterTomorrow = today.plusDays(2)
        return listOf(
            Event(
                date = today,
                title = "Họp ý tưởng cho dự án Magenta",
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(10, 0)
            ),
            Event(
                date = today,
                title = "Standup đội UX",
                startTime = LocalTime.of(12, 0),
                endTime = LocalTime.of(13, 0)
            ),
            Event(
                date = dayAfterTomorrow,
                title = "Họp Scrum hàng tuần",
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(15, 0)
            )
        )
    }

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

                    // State tổng (in-memory để demo)
                    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
                    val allEvents = remember {
                        mutableStateListOf<Event>().also { it.addAll(createFakeDatabase()) }
                    }

                    NavHost(navController = nav, startDestination = "calendar") {
                        composable("calendar") {
                            CalendarMainScreen(
                                navController = nav,
                                initialSelectedDate = selectedDate,
                                allEvents = allEvents,
                                onDateSelected = { d -> selectedDate = d }
                            )
                        }
                        composable("add_event") {
                            AddEventScreen(
                                navController = nav,
                                selectedDate = selectedDate,
                                onSaveEvent = { e -> allEvents.add(e) }
                            )
                        }
                    }
                }
            }
        }
    }
}