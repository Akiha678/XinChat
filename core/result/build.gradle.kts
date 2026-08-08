plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.result"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.util)
    implementation(libs.kotlinx.serialization.json)
}