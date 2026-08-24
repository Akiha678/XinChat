plugins {
    alias(libs.plugins.xinchat.android.feature)
}

android {
    namespace = "com.seanchen.xinchat.feature.chat"
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
}