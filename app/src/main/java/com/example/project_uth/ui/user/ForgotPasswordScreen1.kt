package com.example.project_uth.ui.user

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// ==================================================
// THÊM CÁC IMPORT MỚI
// ==================================================
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.project_uth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.lang.Exception
// ==================================================


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen1(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    // ==================================================

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

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

                    Image(
                        painter = painterResource(id = R.drawable.calander),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier
                            .size(255.dp) // Kích thước giống Login/Register
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Trường nhập Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Nhập Email") },
                        placeholder = { Text("Yourmail@gmail.com") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        enabled = !isLoading // Vô hiệu hóa khi đang tải
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Nút Xác nhận (Đã tích hợp Firebase)
                    Button(
                        onClick = {
                            val emailTrimmed = email.trim()

                            // 1. Kiểm tra (Validate) dữ liệu
                            if (emailTrimmed.isBlank()) {
                                Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
                                Toast.makeText(context, "Định dạng email không hợp lệ", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // 2. Bắt đầu Loading
                            isLoading = true

                            // 3. Gọi API của Firebase
                            auth.sendPasswordResetEmail(emailTrimmed)
                                .addOnCompleteListener { task ->
                                    isLoading = false // Dừng loading
                                    if (task.isSuccessful) {
                                        // 4. Nếu thành công -> Chuẩn bị hộp thoại
                                        Log.d("FIREBASE_AUTH", "Gửi email reset thành công!")
                                        dialogTitle = "Thành công"
                                        dialogMessage = "Đã gửi link đặt lại mật khẩu đến email của bạn. Vui lòng kiểm tra hộp thư (cả mục spam)."
                                        showDialog = true
                                    } else {
                                        // 5. Nếu thất bại -> Chuẩn bị hộp thoại lỗi
                                        Log.w("FIREBASE_AUTH", "Gửi email thất bại", task.exception)
                                        dialogTitle = "Thất bại"
                                        dialogMessage = getPasswordResetErrorMessage(task.exception)
                                        showDialog = true
                                    }
                                }
                        },
                        enabled = !isLoading, // Vô hiệu hóa nút khi đang tải
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text("Xác nhận", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // ==================================================
                    // HỘP THOẠI THÔNG BÁO
                    // ==================================================
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDialog = false
                                // Nếu thành công, quay về màn hình Login
                                if (dialogTitle == "Thành công") {
                                    navController.popBackStack()
                                }
                            },
                            title = { Text(dialogTitle) },
                            text = { Text(dialogMessage) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDialog = false
                                        // Nếu thành công, quay về màn hình Login
                                        if (dialogTitle == "Thành công") {
                                            navController.popBackStack()
                                        }
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            containerColor = Color.White // Giữ hộp thoại màu trắng
                        )
                    }
                    // ==================================================
                }
            }
        }
    }
}

/**
 * Hàm trợ giúp "dịch" lỗi Firebase
 */
private fun getPasswordResetErrorMessage(exception: Exception?): String {
    return when (exception) {
        is FirebaseAuthInvalidUserException -> "Email này không tồn tại trong hệ thống. Vui lòng kiểm tra lại."
        // Các lỗi khác
        else -> exception?.message ?: "Đã xảy ra lỗi không xác định. Vui lòng thử lại."
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