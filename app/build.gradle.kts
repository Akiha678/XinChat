plugins {
    alias(libs.plugins.xinchat.android.application.compose)
    alias(libs.plugins.xinchat.hilt)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.seanchen.xinchat"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.seanchen.xinchat"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = "true"
        }
        release {
            manifestPlaceholders["usesCleartextTraffic"] = "false"
            /**
             * 是否启用代码压缩、优化、混淆
             * 1.删除无用代码
             * 2.代码混淆
             * 3.代码优化
             * 4. 移除调试信息
             */
            isMinifyEnabled = true
            /**
             * 删除未被使用的资源文件
             */
            isShrinkResources = true
            // 配置ProGuard规则文件
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// dev 变体的包名为 com.seanchen.xinchat.dev，google-services.json 中没有对应客户端配置，
// 跳过该变体的 google-services 处理任务，避免构建失败
tasks.matching { task ->
    task.name.startsWith("processDev") && task.name.endsWith("GoogleServices")
}.configureEach {
    enabled = false
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(project(":core:util"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:contact"))
    implementation(project(":feature:user"))
    implementation(project(":feature:main"))

    implementation(libs.common.widget)

    // Firebase 只在 prod 变体接入：google-services.json 中只有 com.seanchen.xinchat 的客户端配置
    "prodImplementation"(platform("com.google.firebase:firebase-bom:34.19.0"))
    "prodImplementation"("com.google.firebase:firebase-analytics")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
