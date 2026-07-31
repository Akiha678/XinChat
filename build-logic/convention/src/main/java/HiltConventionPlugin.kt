import com.seanchen.xinchat.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                "ksp"(libs.findLibrary("hilt.compiler").get())
            }

            pluginManager.withPlugin("com.android.base") {
                apply(plugin = "dagger.hilt.android.plugin")
                dependencies {
                    "implementation"(libs.findLibrary("hilt.android").get())
                    // 添加 Hilt ViewModel Navigation3 支持
                    "implementation"(libs.findLibrary("hilt.lifecycle.viewmodel.compose").get())
                    // 添加 Hilt 测试支持
                    "kspAndroidTest"(libs.findLibrary("hilt.compiler").get())
                    "androidTestImplementation"(libs.findLibrary("hilt.android.testing").get())
                }
            }
        }
    }
}