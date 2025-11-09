package com.example.project_uth.ui.user

// Import cho Preview (LocalContext)
import androidx.compose.ui.platform.LocalContext

// ==================================================
// CÁC IMPORT MỚI
// ==================================================
import android.util.Log
import android.util.Patterns // <-- 1. THÊM IMPORT ĐỂ KIỂM TRA EMAIL
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.lang.Exception
// ==================================================

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
import androidx.compose.ui.graphics.Color // <-- 2. THÊM IMPORT ĐỂ SỬ DỤNG MÀU TRẮNG
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

    // 1. Trạng thái Loading
    var isLoading by remember { mutableStateOf(false) }

    // 2. Trạng thái hiển thị Hộp thoại
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccessDialog by remember { mutableStateOf(false) }
    // ==================================================

    // Lấy đối tượng Firebase Auth
    val auth = FirebaseAuth.getInstance()
    // Lấy Context để dùng cho Toast (nếu cần)
    val context = LocalContext.current


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

                    // Trường Tên người dùng (vô hiệu hóa khi loading)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Tên người dùng") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Email (vô hiệu hóa khi loading)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Mật khẩu (vô hiệu hóa khi loading)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(), // Để ẩn mật khẩu
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trường Xác nhận mật khẩu (vô hiệu hóa khi loading)
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Xác nhận mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(24.dp)) // Khoảng cách với nút Đăng ký

                    // Nút Đăng ký (Đã tích hợp Firebase)
                    Button(
                        onClick = {
                            val emailTrimmed = email.trim()
                            val passwordTrimmed = password.trim()

                            // 1. Kiểm tra (Validate) dữ liệu
                            if (username.isBlank() || emailTrimmed.isBlank() || passwordTrimmed.isBlank()) {
                                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                                return@Button // Dừng lại
                            }

                            // ==================================================
                            // RÀNG BUỘC MỚI (CLIENT-SIDE)
                            // ==================================================
                            if (passwordTrimmed.length < 8) {
                                Toast.makeText(context, "Mật khẩu phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show()
                                return@Button // Dừng lại
                            }
                            if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
                                Toast.makeText(context, "Định dạng email không hợp lệ", Toast.LENGTH_SHORT).show()
                                return@Button // Dừng lại
                            }
                            // ==================================================

                            if (passwordTrimmed != confirmPassword.trim()) {
                                Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                                return@Button // Dừng lại
                            }

                            // 2. Bắt đầu Loading
                            isLoading = true

                            // 3. Gọi API của Firebase để tạo người dùng
                            auth.createUserWithEmailAndPassword(emailTrimmed, passwordTrimmed)
                                .addOnCompleteListener { task ->
                                    isLoading = false // Dừng loading
                                    if (task.isSuccessful) {
                                        // 4. Nếu thành công -> Chuẩn bị hộp thoại thành công
                                        Log.d("FIREBASE_AUTH", "Tạo người dùng thành công!")
                                        dialogTitle = "Thành công!"
                                        dialogMessage = "Đăng ký tài khoản thành công. Bạn sẽ được chuyển đến trang đăng nhập."
                                        isSuccessDialog = true
                                        showDialog = true // Mở hộp thoại

                                    } else {
                                        // 5. Nếu thất bại -> Chuẩn bị hộp thoại lỗi
                                        Log.w("FIREBASE_AUTH", "Tạo người dùng thất bại", task.exception)
                                        dialogTitle = "Đăng ký thất bại"
                                        dialogMessage = getFirebaseAuthErrorMessage(task.exception) // "Dịch" lỗi
                                        isSuccessDialog = false
                                        showDialog = true // Mở hộp thoại
                                    }
                                }
                        },
                        enabled = !isLoading, // Vô hiệu hóa nút khi đang loading
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)) // Màu nền nút
                    ) {
                        // Hiển thị vòng xoay hoặc chữ
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White, // Màu vòng xoay
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text("Đăng kí", color = Color.White)
                        }
                    }

                    // ==================================================
                    // HỘP THOẠI THÔNG BÁO (ĐÃ SỬA MÀU)
                    // ==================================================
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                // Xử lý khi người dùng bấm ra ngoài hộp thoại
                                showDialog = false
                                if (isSuccessDialog) {
                                    // Nếu là thành công, vẫn điều hướng về login
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            title = { Text(dialogTitle) },
                            text = { Text(dialogMessage) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDialog = false // Đóng hộp thoại
                                        if (isSuccessDialog) {
                                            // Nếu là thành công, điều hướng về login
                                            navController.navigate("login") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                        // Nếu thất bại, người dùng ở lại để sửa lỗi
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            // SỬA ĐỔI: THÊM DÒNG NÀY ĐỂ HỘP THOẠI LUÔN LÀ MÀU TRẮNG
                            containerColor = Color.White
                        )
                    }
                    // ==================================================
                }
            }
        }
    }
}

/**
 * Hàm trợ giúp (helper) "dịch" lỗi Firebase sang Tiếng Việt
 */
private fun getFirebaseAuthErrorMessage(exception: Exception?): String {
    return when (exception) {
        // Lỗi này của Firebase là 6 ký tự, nhưng logic 8 ký tự của chúng ta sẽ chặn trước
        is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu. Vui lòng chọn mật khẩu mạnh hơn."
        is FirebaseAuthUserCollisionException -> "Email này đã được sử dụng. Vui lòng chọn email khác."
        // Lỗi "email badly formatted" giờ sẽ bị chặn bởi logic phía client
        else -> exception?.message ?: "Đã xảy ra lỗi không xác định. Vui lòng thử lại."
    }
}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val dummyNavController = NavController(LocalContext.current)
    Surface {
        RegisterScreen(navController = dummyNavController)
    }
}