plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.eyantra.mind_cure_ai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eyantra.mind_cure_ai"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Properly fetch API key from gradle.properties
        val openAiKey: String? = project.findProperty("OPENAI_API_KEY") as String?
        buildConfigField("String", "OPENAI_API_KEY", "\"${openAiKey ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true  // ✅ Ensure BuildConfig is generated
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ✅ Add Required Dependencies
    implementation("com.google.code.gson:gson:2.9.0") // Gson for JSON parsing
    implementation("com.squareup.okhttp3:okhttp:4.9.3") // OkHttp for API calls
}
