package com.example.project_uth.ui.user

// Import cho Preview (LocalContext)
import androidx.compose.ui.platform.LocalContext

// Các import cơ bản cho UI
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_uth.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    // Chỉ giữ lại các biến trạng thái cho ô nhập liệu
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Nền tối bao bọc (Tái sử dụng từ LoginScreen)
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2D2D2D)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Thẻ (card) trắng ở giữa (Tái sử dụng từ LoginScreen)
            Surface(
                modifier = Modifier
                    .width(360.dp) // Cố định chiều rộng của card
                    .padding(16.dp),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                // Thêm scrollable cho column để tránh bàn phím che mất input
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState), // Giúp màn hình tự cuộn khi bàn phím hiện
                    horizontalAlignment = Alignment.CenterHorizontally // Căn giữa các item theo chiều ngang
                ) {

                    // Icon lịch (Tái sử dụng từ LoginScreen)
                    Image(
                        painter = painterResource(id = R.drawable.calander), // Đảm bảo ID này đúng
                        contentDescription = "Calendar Icon",
                        modifier = Modifier
                            .size(255.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Trường Tên người dùng (đã xóa 'enabled = !isLoading')
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Tên người dùng") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Email (đã xóa 'enabled = !isLoading')
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Mật khẩu (đã xóa 'enabled = !isLoading')
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(), // Để ẩn mật khẩu
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Xác nhận mật khẩu (đã xóa 'enabled = !isLoading')
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Xác nhận mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp)) // Khoảng cách với nút Đăng ký

                    // Nút Đăng ký (Đã trả về trạng thái ban đầu)
                    Button(
                        onClick = {
                            /* TODO: Xử lý đăng ký (chưa có Firebase) */
                            // Ví dụ: chỉ cần quay lại login
                            // navController.navigate("login")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)) // Màu nền nút
                    ) {
                        // Chỉ hiển thị chữ, không có vòng xoay loading
                        Text("Đăng kí", color = Color.White)
                    }

                    // Đã xóa hộp thoại AlertDialog
                }
            }
        }
    }
}

// Đã xóa hàm private fun getFirebaseAuthErrorMessage(...)

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val dummyNavController = NavController(LocalContext.current)
    Surface {
        RegisterScreen(navController = dummyNavController)
    }
}