import com.seanchen.xinchat.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import kotlin.text.get

class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.seanchen.xinchat.android.library.compose")
                apply("com.seanchen.xinchat.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                "implementation"(project(":core:navigation"))
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:util"))
                "implementation"(project(":core:data"))
                "implementation"(project(":core:common"))
                "implementation"(project(":core:model"))
                "implementation"(project(":core:result"))

                // Navigation3 导航框架
                "implementation"(libs.findLibrary("androidx.navigation3.runtime").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewmodel.navigation3").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtime.compose").get())

                // Hilt依赖注入
                // Hilt依赖注入相关
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
