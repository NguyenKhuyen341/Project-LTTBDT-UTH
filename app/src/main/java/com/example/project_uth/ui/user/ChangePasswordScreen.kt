package com.example.project_uth.ui.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    // 3 BIẾN STATE CHO 3 Ô NHẬP LIỆU
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    // Biến state cho loading và dialog
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2D2D2D)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
                    Spacer(modifier = Modifier.height(24.dp))

                    // Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar Placeholder",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // ==================================================
                    // BƯỚC 2: THÊM Ô MẬT KHẨU CŨ
                    // ==================================================
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Mật khẩu cũ") },
                        placeholder = { Text("Nhập mật khẩu hiện tại") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // ==================================================

                    // Trường Mật khẩu mới
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu mới") },
                        placeholder = { Text("Nhập mật khẩu mới (ít nhất 8 ký tự)") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Xác nhận mật khẩu mới
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Xác nhận mật khẩu mới") },
                        placeholder = { Text("Nhập lại mật khẩu mới") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // ==================================================
                    // BƯỚC 3: TÍCH HỢP LOGIC ĐỔI MẬT KHẨU
                    // ==================================================
                    Button(
                        onClick = {
                            // 1. Kiểm tra dữ liệu
                            if (oldPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
                                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (newPassword.length < 8) { // Ràng buộc 8 ký tự
                                Toast.makeText(context, "Mật khẩu mới phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (newPassword != confirmNewPassword) {
                                Toast.makeText(context, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true
                            val user = auth.currentUser

                            if (user == null || user.email == null) {
                                // Lỗi lạ, bắt đăng nhập lại
                                isLoading = false
                                Toast.makeText(context, "Không tìm thấy người dùng, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show()
                                navController.navigate("login") { popUpTo(0) }
                                return@Button
                            }

                            // 2. Bảo mật: Yêu cầu Firebase xác thực lại bằng mật khẩu CŨ
                            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword.trim())
                            user.reauthenticate(credential)
                                .addOnCompleteListener { reAuthTask ->
                                    if (reAuthTask.isSuccessful) {
                                        // 3. Nếu xác thực mật khẩu cũ thành công -> Đặt mật khẩu MỚI
                                        Log.d("AUTH", "Xác thực lại thành công")
                                        user.updatePassword(newPassword.trim())
                                            .addOnCompleteListener { updateTask ->
                                                isLoading = false
                                                if (updateTask.isSuccessful) {
                                                    // Thành công cuối cùng
                                                    dialogTitle = "Thành công"
                                                    dialogMessage = "Đổi mật khẩu thành công!"
                                                    showDialog = true // Mở hộp thoại
                                                } else {
                                                    dialogTitle = "Thất bại"
                                                    dialogMessage = updateTask.exception?.message ?: "Lỗi không xác định"
                                                    showDialog = true
                                                }
                                            }
                                    } else {
                                        // 4. Nếu xác thực mật khẩu cũ thất bại
                                        isLoading = false
                                        Log.w("AUTH", "Xác thực lại thất bại", reAuthTask.exception)
                                        dialogTitle = "Thất bại"
                                        dialogMessage = if (reAuthTask.exception is FirebaseAuthInvalidCredentialsException) {
                                            "Mật khẩu cũ không chính xác."
                                        } else {
                                            reAuthTask.exception?.message ?: "Lỗi xác thực"
                                        }
                                        showDialog = true
                                    }
                                }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
                        } else {
                            Text("Xác nhận", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Hộp thoại
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDialog = false
                                if (dialogTitle == "Thành công") {
                                    navController.popBackStack() // Quay về màn hình trước (Calendar)
                                }
                            },
                            title = { Text(dialogTitle) },
                            text = { Text(dialogMessage) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDialog = false
                                        if (dialogTitle == "Thành công") {
                                            navController.popBackStack() // Quay về màn hình trước (Calendar)
                                        }
                                    }
                                ) { Text("OK") }
                            },
                            containerColor = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    val dummyNavController = NavController(LocalContext.current)
    Surface {
        ChangePasswordScreen(navController = dummyNavController)
    }
}