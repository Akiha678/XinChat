plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)


    implementation(project(":core:data"))
    implementation(project(":core:model"))

    // 导航测试需要构造 AppState，因此补充其依赖的数据源契约与登录态失效通知
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(project(":core:datastore"))
    testImplementation(project(":core:network"))
    testImplementation(project(":core:result"))
}
