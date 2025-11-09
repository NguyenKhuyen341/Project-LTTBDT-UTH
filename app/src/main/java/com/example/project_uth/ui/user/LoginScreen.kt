package com.example.project_uth.ui.user

import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- IMPORT ĐÃ THÊM
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // <-- IMPORT ĐÃ THÊM
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_uth.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Nền tối bao bọc (Dùng M3 Surface)
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2D2D2D)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Thẻ (card) trắng ở giữa (Dùng M3 Surface)
            Surface(
                modifier = Modifier
                    .width(360.dp)
                    .padding(16.dp),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                val scrollState = rememberScrollState() // <-- THÊM DÒNG NÀY

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState), // <-- THÊM MODIFIER NÀY
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.calander), // <-- KIỂM TRA LẠI TÊN TỆP
                        contentDescription = "Calendar Icon",
                        modifier = Modifier
                            .size(255.dp)
                            .clip(RoundedCornerShape(16.dp))

                    )

                    Spacer(modifier = Modifier.height(0.dp))

                    Text(
                        "Chào mừng trở lại! 👋",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Đừng bỏ lỡ bất kỳ sự kiện nào. Đăng nhập để lên kế hoạch ngay hôm nay.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // FIX: Dùng màu M3
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Trường Email (Dùng M3 OutlinedTextField)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email")},
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Mật khẩu (Dùng M3 OutlinedTextField)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    TextButton(
                        onClick = { navController.navigate("forgot_password_1") },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Quên mật khẩu?") // M3 TextButton
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nút Đăng nhập (Dùng M3 Button)
                    Button(
                        onClick = { /* TODO: Xử lý đăng nhập */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        // FIX: 'backgroundColor' trong M3 đổi thành 'containerColor'
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                    ) {
                        Text("Đăng Nhập", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Link Đăng ký (Dùng M3 TextButton)
                    Row(
                        verticalAlignment = Alignment.CenterVertically // Căn 2 mẩu chữ cho thẳng hàng
                    ) {
                        // 1. Phần chữ màu đen (không bấm được)
                        Text(
                            text = "Chưa có tài khoản? ",
                            color = MaterialTheme.colorScheme.onSurface // Màu đen/xám chuẩn của M3
                        )

                        TextButton(
                            onClick = { navController.navigate("register") },
                            // Giảm padding mặc định của TextButton để nó nằm sát chữ
                            contentPadding = PaddingValues(start = 4.dp, end = 4.dp)
                        ) {
                            Text("Đăng kí.") // Sẽ tự có màu xanh (màu primary)
                        }
                    }

                    // Nút Đăng nhập Google (FIX: Dùng M3 OutlinedButton)
                    OutlinedButton(
                        onClick = { /* TODO: Xử lý Google Sign In */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF5F5F5), // Màu nền
                            contentColor = Color.Black // Màu chữ
                        ),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center, // Căn giữa
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // TODO: Thêm Icon Google
                            Image(painter = painterResource(id = R.drawable.logogg), contentDescription = "Google Icon")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Sign in with Google",
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}