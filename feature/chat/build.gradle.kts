plugins {
    alias(libs.plugins.xinchat.android.feature)
}

android {
    namespace = "com.seanchen.xinchat.feature.chat"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.common.widget)

    // JVM 侧行为取证用（skill-test）：驱动真实 WebSocketManager，不进入产物。
    // timber 在 :core:util 里是 implementation，不会传递到本模块的测试编译类路径，需显式声明。
    testImplementation(libs.timber)
    testImplementation(libs.mockwebserver)
}