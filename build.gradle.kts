plugins {
    id("com.android.application") version "8.13.0" apply false
    //id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
