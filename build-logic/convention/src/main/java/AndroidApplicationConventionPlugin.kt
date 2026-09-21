import com.android.build.api.dsl.ApplicationExtension
import com.seanchen.xinchat.configureKotlinAndroid
import com.seanchen.xinchat.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project>{


    override fun apply(target: Project) {
        with(target) {
            with(pluginManager){
                apply("com.android.application")    // 应用Android应用插件
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                namespace = libs.findVersion("namespace").get().toString()

                // 默认配置
                defaultConfig {
                    // 设置应用ID
                    applicationId = libs.findVersion("namespace").get().toString()
                    // 设置目标 SDK 版本
                    targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                    // 设置应用版本号
                    versionCode = libs.findVersion("versionCode").get().toString().toInt()
                    // 设置应用版本名称
                    versionName = libs.findVersion("versionName").get().toString()

                    // 设置Android测试运行器
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                }
                flavorDimensions += listOf("env")
                productFlavors {
                    create("dev") {
                        dimension = "env"
                        applicationIdSuffix = ".dev"
                        versionNameSuffix = "-dev"
                    }
                    create("prod") {
                        dimension = "env"
                    }
                }
            }
        }
    }
}
