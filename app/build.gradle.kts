plugins {
    alias(libs.plugins.xinchat.android.application.compose)
    alias(libs.plugins.xinchat.hilt)
}

android {
    namespace = "com.seanchen.xinchat"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.seanchen.xinchat"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

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
