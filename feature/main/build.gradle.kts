plugins {
    alias(libs.plugins.xinchat.android.feature)
}

android {
    namespace = "com.seanchen.xinchat.feature.main"
}

dependencies {
    implementation(libs.lottie.compose)

    implementation(projects.feature.contact)
    implementation(projects.feature.chat)
    implementation(projects.feature.user)
}