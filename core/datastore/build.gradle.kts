plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.datastore"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.util)
}