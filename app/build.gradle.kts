plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.minimalshoppingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.minimalshoppingapp"
        minSdk = 24
        targetSdk = 34
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
    
    // Add ViewBinding support
    buildFeatures {
        viewBinding = true
    }
    
    // Configure lint to ignore certain issues
    lint {
        abortOnError = false
        checkReleaseBuilds = false
        baseline = file("lint-baseline.xml")
        disable += setOf(
            "ObsoleteSdkInt",
            "NotificationPermission",
            "DeprecatedMethod"
        )
    }
}

dependencies {
    // Use specific versions compatible with SDK 34
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Material Components
    implementation("com.google.android.material:material:1.11.0")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")
    
    // ViewPager2 for onboarding
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}