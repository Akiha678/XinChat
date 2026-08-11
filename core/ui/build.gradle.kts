plugins {
    alias(libs.plugins.xinchat.android.library.compose)
}

android {
    namespace = "com.seanchen.xinchat.core.ui"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.util)
}