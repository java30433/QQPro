plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("momoi.plugin.apkmixin") apply true
}

android {
    namespace = "momoi.mod.qqpro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tencent.qqlite"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":ApkMixin-annotation"))
    compileOnly(libs.androidx.appcompat)
    compileOnly(fileTree("./libs"))
    compileOnly(libs.androidx.fragment)
    compileOnly(libs.androidx.constraintlayout)
    compileOnly(libs.androidx.recyclerview)
    compileOnly(libs.androidx.viewpager2)
    compileOnly(libs.androidx.core)
}

apkMixin {
    versionName = "1.5.2"
    targetApk = "source.apk"
    useProcessorCountAsThreadCount = project.properties["useProcessorCountAsThreadCount"] == "true"

    signing {
        keyFile = file("mixin/testkey.pk8")
        certFile = file("mixin/testkey.x509.pem")
    }

    output {
        signedFileName = "QQPro_${versionName}.apk"
    }
}