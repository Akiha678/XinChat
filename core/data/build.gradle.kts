plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
}

android {
    namespace = "com.seanchen.xinchat.core.data"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)

    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.util)
    implementation(projects.core.datastore)
    implementation(projects.core.result)
    implementation(projects.core.database)
}
