package com.example.project_uth.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Import các thư viện cần thiết
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.project_uth.R // Đảm bảo bạn có R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen1(navController: NavController) {
    var email by remember { mutableStateOf("") }

    // Nền tối (giống Login/Register)
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2D2D2D)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Thẻ trắng
            Surface(
                modifier = Modifier
                    .width(360.dp)
                    .padding(16.dp),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ===================================
                    // THAY THẾ AVATAR BẰNG ICON LỊCH
                    // ===================================
                    Image(
                        painter = painterResource(id = R.drawable.calander),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier
                            .size(255.dp) // Kích thước giống Login/Register
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // ===================================

                    // Trường nhập Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Nhập Email") },
                        placeholder = { Text("Yourmail@gmail.com") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Nút Xác nhận (Tạm bợ)
                    Button(
                        onClick = {
                            /* TODO: Hôm sau tích hợp Firebase tại đây (ví dụ: gửi email reset) */
                            /* Sau khi gửi, điều hướng: navController.navigate("forgot_password_2") */
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                    ) {
                        Text("Xác nhận", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreen1Preview() {
    // Dùng LocalContext.current để tạo NavController "giả" cho Preview
    val dummyNavController = NavController(LocalContext.current)
    // YourProjectTheme { // Bạn có thể bọc Theme ở đây
    Surface {
        ForgotPasswordScreen1(navController = dummyNavController)
    }
    // }
}