plugins {
    alias(libs.plugins.xinchat.android.library)
    alias(libs.plugins.xinchat.hilt)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seanchen.xinchat.core.database"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
}