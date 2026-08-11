plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
}

android {
    namespace = "com.seanchen.xinchat.core.network"
}

dependencies {
    implementation(projects.core.model)


    implementation(libs.retrofit)
}