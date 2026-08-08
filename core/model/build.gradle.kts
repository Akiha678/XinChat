plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}