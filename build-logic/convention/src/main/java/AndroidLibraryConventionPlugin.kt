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
                val devBaseUrl = providers.gradleProperty("xinchat.devBaseUrl")
                    .orElse("http://10.0.2.2:8080/")
                    .get()
                    .withTrailingSlash()
                val prodBaseUrl = providers.gradleProperty("xinchat.prodBaseUrl")
                    .orElse("https://api.xinchat.invalid/")
                    .get()
                    .withTrailingSlash()
                require(prodBaseUrl.startsWith("https://")) {
                    "XinChat 生产服务地址必须使用 https://"
                }

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
                        buildConfigField("String", "BASE_URL", devBaseUrl.asBuildConfigString())
                        buildConfigField("Boolean", "DEBUG", "true")
                    }
                    create("prod") {
                        dimension = "env"
                        buildConfigField("String", "BASE_URL", prodBaseUrl.asBuildConfigString())
                        buildConfigField("Boolean", "DEBUG", "false")
                    }
                }
            }
            configureDependencies()

        }
    }
}

private fun String.withTrailingSlash(): String = trim().let { value ->
    require(value.startsWith("http://") || value.startsWith("https://")) {
        "XinChat 服务地址必须使用 http:// 或 https://"
    }
    if (value.endsWith('/')) value else "$value/"
}

private fun String.asBuildConfigString(): String =
    "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

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
