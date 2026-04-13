plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    layout.buildDirectory.set(file("../build_alt"))
    namespace = "com.example.multiuserwidget"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.multiuserwidget"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}

// Automatic APK export to Desktop
tasks.register<Copy>("copyApkToDesktop") {
    val apkFile = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk")
    from(apkFile)
    into("C:/Users/jeremy/Desktop")
    rename { "multiuser_widget_debug.apk" }
    
    // Only run if the APK actually exists
    onlyIf { apkFile.get().asFile.exists() }
}

afterEvaluate {
    tasks.matching { it.name == "assembleDebug" }.configureEach {
        finalizedBy("copyApkToDesktop")
    }
}



