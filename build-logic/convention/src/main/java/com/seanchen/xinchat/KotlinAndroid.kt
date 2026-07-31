package com.seanchen.xinchat

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
){
    commonExtension.enableKotlin = true
    commonExtension.compileSdk = libs.findLibrary("compileSdk").get().toString().toInt()
    commonExtension.defaultConfig.minSdk = libs.findVersion("minSdk").get().toString().toInt()
    // 统一启动 BuildConfig 生成
    commonExtension.buildFeatures.buildConfig = true

    commonExtension.compileOptions.sourceCompatibility = JavaVersion.VERSION_11
    commonExtension.compileOptions.targetCompatibility = JavaVersion.VERSION_11
    commonExtension.compileOptions.isCoreLibraryDesugaringEnabled = true

    configureKotlinCompilerOptions()

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("android.desugarJdkLibs").get())
    }
}


internal fun Project.configureKotlinJvm(){
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    configureKotlinCompilerOptions()
}


/**
 * 配置基础的Kotlin编译选项
 */
internal fun Project.configureKotlinCompilerOptions(){
    val warningsAsErrors = providers.gradleProperty("warningsAsErrors").map {
        it.toBoolean()
    }.orElse(false)

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            allWarningsAsErrors.set(warningsAsErrors)
            freeCompilerArgs.add(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
            freeCompilerArgs.add(
                "-Xconsistent-data-class-copy-visibility"
            )
        }
    }
}