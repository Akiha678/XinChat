plugins {
    alias(libs.plugins.xinchat.android.feature)
}

android {
    namespace = "com.seanchen.xinchat.feature.user"
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.common.widget)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(projects.core.network)
    testImplementation(projects.core.datastore)
}
