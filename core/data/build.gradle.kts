plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.data"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
}
