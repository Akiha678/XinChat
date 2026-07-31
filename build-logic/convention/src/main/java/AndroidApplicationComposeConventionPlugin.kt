import com.android.build.api.dsl.ApplicationExtension
import com.seanchen.xinchat.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationComposeConventionPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.seanchen.xinchat.android.application")

            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            // 获取 Android 通用拓展并配置 Compose
            val extension = extensions.findByType(ApplicationExtension::class.java)
            extension?.let { configureAndroidCompose(it) }
        }
    }
}