plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.util"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.model)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.toaster)
    implementation(libs.xxpermissions)
    implementation(libs.mmkv)
    implementation(libs.timber)
}