plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.kotlin.compose) apply false
//    id("com.google.gms.google-services") version "4.4.4" apply false
}
