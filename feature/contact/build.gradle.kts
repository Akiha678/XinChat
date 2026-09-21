plugins {
    alias(libs.plugins.xinchat.android.feature)
}

android {
    namespace = "com.seanchen.xinchat.feature.contact"
}

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(projects.core.network)
    implementation(libs.common.widget)
}
