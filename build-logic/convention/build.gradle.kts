import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

// 定义构建逻辑模块的组名
group = "com.seanchen.xinchat.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    // 添加Android Gradle插件依赖
    compileOnly(libs.android.gradlePlugin)
    // 添加Kotlin Gradle插件依赖
    compileOnly(libs.kotlin.gradlePlugin)
    // 添加KSP注解处理器插件依赖
    compileOnly(libs.ksp.gradlePlugin)
}


gradlePlugin {
    plugins {
        // 注册Android应用程序插件
        register("xinchatAndroidApplication") {
            id = "com.seanchen.xinchat.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        // 注册Android应用程序Compose插件
        register("xinchatAndroidApplicationCompose") {
            id = "com.seanchen.xinchat.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        // 注册Android库插件
        register("xinchatAndroidLibrary") {
            id = "com.seanchen.xinchat.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        // 注册Android库Compose插件
        register("xinchatAndroidLibraryCompose") {
            id = "com.seanchen.xinchat.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        // 注册Android Feature模块插件
        register("xinchatAndroidFeature") {
            id = "com.seanchen.xinchat.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        // 注册Android测试插件
        register("xinchatAndroidTest") {
            id = "com.seanchen.xinchat.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        // 注册Hilt依赖注入插件
        register("xinchatHilt") {
            id = "com.seanchen.xinchat.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}