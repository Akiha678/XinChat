plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)
}
