plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.labelprint"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.labelprint"
        minSdk = 28
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
        applicationVariants.all {
            outputs.all {
                this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
                //val timestamp = SimpleDateFormat("ddMMyyyy_hhmm_aa").format(Date())
                val apkName = "LabelPrints.apk"
                outputFileName = apkName
            }
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.1.0")
    implementation("com.squareup.retrofit2:converter-gson:2.0.2")
    //implementation("com.github.ISchwarz23:SortableTableView:2.8.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}