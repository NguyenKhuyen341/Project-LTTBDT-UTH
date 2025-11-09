package com.example.project_uth.ui.user

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project_uth.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // <-- Lấy Web Client ID
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isLoading = true
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GOOGLE_SIGN_IN", "Đã lấy tài khoản Google: ${account.id}")
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { firebaseTask ->
                        isLoading = false
                        if (firebaseTask.isSuccessful) {
                            Log.d("FIREBASE_AUTH", "Đăng nhập Google (Firebase) thành công!")
                            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                            navController.navigate("calendar") {
                                popUpTo(0)
                            }
                        } else {
                            Log.w("FIREBASE_AUTH", "Đăng nhập Google (Firebase) thất bại", firebaseTask.exception)
                            dialogTitle = "Đăng nhập thất bại"
                            dialogMessage = firebaseTask.exception?.message ?: "Lỗi không xác định."
                            showDialog = true
                        }
                    }
            } catch (e: ApiException) {
                isLoading = false
                Log.w("GOOGLE_SIGN_IN", "Lấy tài khoản Google thất bại", e)
                dialogTitle = "Lỗi"
                dialogMessage = "Không thể lấy tài khoản Google: ${e.statusCode}"
                showDialog = true
            }
        }
    }


    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2D2D2D)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    modifier = Modifier.padding(16.dp).verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.calander),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(255.dp).clip(RoundedCornerShape(16.dp))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email")},
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),

                        // SỬA 1: Thay đổi visualTransformation
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = !isLoading,

                        // SỬA 2: Thêm trailingIcon (nút con mắt)
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.VisibilityOff // Icon con mắt gạch chéo
                            else
                                Icons.Filled.Visibility // Icon con mắt

                            // Mô tả cho accessibility
                            val description = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"

                            IconButton(onClick = { passwordVisible = !passwordVisible }){
                                Icon(imageVector  = image, description)
                            }
                        }
                    )
                    // ==================================================


                    // Nút "Quên mật khẩu?"
                    TextButton(
                        onClick = { navController.navigate("forgot_password_1") },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !isLoading
                    ) {
                        Text("Quên mật khẩu?")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Nút Đăng nhập (Email/Pass)
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Log.d("FIREBASE_AUTH", "Đăng nhập Email thành công!")
                                        Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("calendar") { popUpTo(0) }
                                    } else {
                                        Log.w("FIREBASE_AUTH", "Đăng nhập Email thất bại", task.exception)
                                        dialogTitle = "Đăng nhập thất bại"
                                        dialogMessage = getFirebaseAuthErrorMessage(task.exception)
                                        showDialog = true
                                    }
                                }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text("Đăng Nhập", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Link Đăng ký
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Chưa có tài khoản? ", color = MaterialTheme.colorScheme.onSurface)
                        TextButton(
                            onClick = { navController.navigate("register") },
                            enabled = !isLoading,
                            contentPadding = PaddingValues(start = 4.dp, end = 4.dp)
                        ) {
                            Text("Đăng kí.")
                        }
                    }

                    // Nút Đăng nhập Google (ĐÃ SỬA)
                    OutlinedButton(
                        onClick = {
                            Log.d("GOOGLE_SIGN_IN", "Bắt đầu đăng nhập Google...")
                            val signInIntent = googleSignInClient.signInIntent
                            googleAuthLauncher.launch(signInIntent)
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF5F5F5),
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(painter = painterResource(id = R.drawable.logogg), contentDescription = "Google Icon")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign in with Google", color = Color.Black)
                        }
                    }

                    // Hộp thoại báo lỗi
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text(dialogTitle) },
                            text = { Text(dialogMessage) },
                            confirmButton = {
                                TextButton(onClick = { showDialog = false }) { Text("OK") }
                            }
                        )
                    }
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
        is FirebaseAuthInvalidUserException -> "Tài khoản email này không tồn tại. Vui lòng đăng ký."
        is FirebaseAuthInvalidCredentialsException -> "Sai mật khẩu. Vui lòng thử lại."
        else -> exception?.message ?: "Đã xảy ra lỗi không xác định. Vui lòng thử lại."
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}