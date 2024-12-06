import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.zubaku.memoir"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zubaku.memoir"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val secretsProperties = Properties().apply {
                file("../env.properties").inputStream().use { load(it) }
            }

            val smtpSenderEmail =
                secretsProperties["smtp_sender_email"]?.toString() ?: "default_email"
            val smtpSenderPassword =
                secretsProperties["smtp_sender_password"]?.toString() ?: "default_password"

            buildConfigField("String", "SMTP_SENDER_EMAIL", "\"$smtpSenderEmail\"")
            buildConfigField("String", "SMTP_SENDER_PASSWORD", "\"$smtpSenderPassword\"")
        }

        getByName("debug") {
            val secretsProperties = Properties().apply {
                file("../env.properties").inputStream().use { load(it) }
            }

            val smtpSenderEmail =
                secretsProperties["smtp_sender_email"]?.toString() ?: "default_email"
            val smtpSenderPassword =
                secretsProperties["smtp_sender_password"]?.toString() ?: "default_password"

            buildConfigField("String", "SMTP_SENDER_EMAIL", "\"$smtpSenderEmail\"")
            buildConfigField("String", "SMTP_SENDER_PASSWORD", "\"$smtpSenderPassword\"")
        }
    }

    buildFeatures {
        buildConfig = true
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
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.analytics)
    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.firestore)
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.auth)
    // Add the dependency for the Firebase Storage library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.google.firebase.storage)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.android.mail)
    implementation(libs.android.activation)
}