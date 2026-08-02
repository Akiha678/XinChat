plugins {
    alias(libs.plugins.xinchat.android.library.compose)
}

android {
    namespace = "com.seanchen.xinchat.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    testImplementation(libs.junit)
}
