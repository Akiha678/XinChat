import com.android.build.api.dsl.LibraryExtension
import com.seanchen.xinchat.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用 Android 库插件
            pluginManager.apply("com.seanchen.xinchat.android.library")
            // 应用 Kotlin Compose 编译器插件
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val exception = extensions.findByType(LibraryExtension::class.java)
            exception?.let { configureAndroidCompose(it) }
        }
    }
}