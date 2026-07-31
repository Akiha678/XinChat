package com.seanchen.xinchat

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension
) {
    // 启用 Compose 构建功能
    commonExtension.buildFeatures.compose = true

    // 配置 Compose 相关依赖
    dependencies {
        // 使用 Compose 统一依赖版本
        val bom = libs.findLibrary("androidx.compose.bom").get()
        "implementation"(platform(bom))

        // 核心 UI 组件
        "implementation"(libs.findLibrary("androidx.ui").get())
        "implementation"(libs.findLibrary("androidx.ui.graphics").get())
        "implementation"(libs.findLibrary("androidx.ui.tooling.preview").get())
        "implementation"(libs.findLibrary("androidx.material3").get())

        // Compose 集成支持
        "implementation"(libs.findLibrary("androidx.activity.compose").get())
        "implementation"(libs.findLibrary("androidx.lifecycle.runtime.ktx").get())

        // 开发调试工具
        "debugImplementation"(libs.findLibrary("androidx.ui.tooling").get())
        "debugImplementation"(libs.findLibrary("androidx.ui.test.manifest").get())

        // 测试依赖
        "androidTestImplementation"(platform(bom))
        "androidTestImplementation"(libs.findLibrary("androidx.ui.test.junit4").get())
    }
}