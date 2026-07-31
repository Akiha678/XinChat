import com.android.build.api.dsl.LibraryExtension
import com.seanchen.xinchat.configureKotlinAndroid
import com.seanchen.xinchat.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library") // 应用Android库插件
            }
            extensions.configure<LibraryExtension> {
                val projectDir = project.projectDir.path

                val featureMatch = Regex(".*/(feature/[^/]+).*").find(projectDir)
                // 匹配core模块路径
                val coreMatch = Regex(".*/(core/[^/]+).*").find(projectDir)

                // 根据模块类型生成命名空间
                namespace = when {
                    // feature模块命名空间
                    featureMatch != null -> {
                        val featurePath = featureMatch.groupValues[1].replace("/", ".")
                        "${libs.findVersion("namespace").get()}.$featurePath"
                    }
                    // core模块命名空间
                    coreMatch != null -> {
                        val corePath = coreMatch.groupValues[1].replace("/", ".")
                        "${libs.findVersion("namespace").get()}.$corePath"
                    }
                    // 其他模块命名空间
                    else -> {
                        val modulePath = project.path.removePrefix(":").replace(":", ".")
                        "${libs.findVersion("namespace").get()}.$modulePath"
                    }
                }
                println("配置模块: ${project.path} 的命名空间为: ${namespace}")

                // 使用统一的 Kotlin Android 配置
                configureKotlinAndroid(this)

                flavorDimensions += listOf("env")
                productFlavors {
                    create("dev") {
                        dimension = "env"
                        // 开发环境地址跟生产环境的地址暂时一样
                        buildConfigField("String", "BASE_URL", "\"https://mall.dusksnow.top/app/\"")
//                        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8001/app/\"")
                        buildConfigField("Boolean", "DEBUG", "true")
                    }
                    create("prod") {
                        dimension = "env"
                        buildConfigField("String", "BASE_URL", "\"https://mall.dusksnow.top/app/\"")
                        buildConfigField("Boolean", "DEBUG", "false")
                    }
                }
            }
            configureDependencies()

        }
    }
}

/**
 * 配置库模块的通用依赖
 */
internal fun Project.configureDependencies() {
    dependencies {
        "implementation"(libs.findLibrary("androidx.core.ktx").get())
        "implementation"(libs.findLibrary("androidx.appcompat").get())
        "implementation"(libs.findLibrary("material").get())
        "testImplementation"(libs.findLibrary("junit").get())
        "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
        "androidTestImplementation"(libs.findLibrary("androidx.espresso.core").get())
    }
}