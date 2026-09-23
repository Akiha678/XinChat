plugins {
    alias(libs.plugins.xinchat.android.feature)
}

android {
    namespace = "com.seanchen.xinchat.feature.auth"
}

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp)
    testImplementation(projects.core.network)
    testImplementation(projects.core.datastore)
    implementation(libs.common.widget)
}