import com.seanchen.xinchat.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project>{

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply{
                apply("com.seanchen.xinchat.android.library.compose")   // 应用Android库和Compose配置
                apply("com.seanchen.xinchat.hilt") // 应用Hilt依赖注入
            }

            // 配置Feature模块配置
            dependencies {
                "implementation"(project(":core:navigation")) // 导航模块
                "implementation"(project(":core:designsystem")) // 设计系统
                "implementation"(project(":core:ui")) // UI组件库
                "implementation"(project(":core:util")) // 工具类
                "implementation"(project(":core:data")) // 数据
                "implementation"(project(":core:common")) // 公共
                "implementation"(project(":core:model")) // 模型
                "implementation"(project(":core:result")) // 结果处理

                // Navigation3
                "implementation"(libs.findLibrary("androidx.navigation3.runtime").get())
                "implementation"(libs.findLibrary("androidx.navigation3.ui").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewmodel.navigation3").get())

                "implementation"(
                    libs.findLibrary("hilt.lifecycle.viewmodel.compose").get()
                ) // Hilt导航集成
                "kspAndroidTest"(libs.findLibrary("hilt.compiler").get()) // 测试用Hilt编译器
                "androidTestImplementation"(
                    libs.findLibrary("hilt.android.testing").get()
                ) // Hilt测试支持
            }
        }
    }
}