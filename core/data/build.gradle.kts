plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
}

android {
    namespace = "com.seanchen.xinchat.core.data"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.util)
    implementation(projects.core.datastore)
    implementation(projects.core.result)
    implementation(projects.core.database)
}
