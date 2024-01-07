

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.devtools.ksp")
    id("com.google.protobuf")
    id("androidx.navigation.safeargs.kotlin")

}
//plugins {
//    id 'com.google.gms.google-services'
//}

android {
    namespace = "com.evanzheng.bibliobarcode"
    compileSdk = 34

    packaging.resources {
        // The Rome library JARs embed some internal utils libraries in nested JARs.
        // We don't need them so we exclude them in the final package.
        excludes += "/*.jar"

        // Multiple dependency bring these files in. Exclude them to enable
        // our test APK to build (has no effect on our AARs)
        excludes += "/META-INF/DEPENDENCIES"
        excludes += "/META-INF/AL2.0"
        excludes += "/META-INF/LGPL2.1"
    }



    defaultConfig {
        applicationId  = "com.evanzheng.bibliobarcode"

        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled  = true

        //versionCode Integer.valueOf(System.env.VERSION_CODE ?: 2)
        //versionName "1.0.0-${System.env.VERSION_SHA}"

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

        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    // Gradle automatically adds 'android.test.runner' as a dependency.
    useLibrary ("android.test.runner")
    useLibrary ("android.test.base")
    useLibrary ("android.test.mock")

    // for dependency injection
    buildFeatures {
        //compose = true
        //dataBinding = true
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    // work manager (workers)
    val workVersion = "2.9.0"

    // this solves on recent Android  the crash signaled in Logcat with
    // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified
    // when creating a PendingIntent. Strongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE
    // if some functionality depends on the PendingIntent being mutable, e.g. if it needs to be used
    // with inline replies or bubbles.
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    androidTestImplementation("androidx.work:work-testing:$workVersion")
    implementation ("androidx.work:work-multiprocess:$workVersion")

    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.core:core-ktx:1.12.0")

    //implementation fileTree(dir: 'libs', include: ['*.jar'])

    //def camerax_version = "1.3.0-alpha05"

    val acraVersion = "5.11.3"
    implementation("ch.acra:acra-mail:$acraVersion")
    implementation("ch.acra:acra-core:$acraVersion")
    implementation("ch.acra:acra-dialog:$acraVersion")
    implementation("ch.acra:acra-notification:$acraVersion")
    implementation("ch.acra:acra-notification:$acraVersion")
    implementation("ch.acra:acra-limiter:$acraVersion")
    implementation("ch.acra:acra-advanced-scheduler:$acraVersion")



    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    val room_version = "2.6.1"
    implementation ("androidx.room:room-runtime:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    ksp ("androidx.room:room-compiler:$room_version")

    val camerax_version = "1.3.1"
    implementation ("androidx.camera:camera-core:$camerax_version")
    implementation ("androidx.camera:camera-camera2:$camerax_version")
    implementation ("androidx.camera:camera-view:$camerax_version")
    implementation ("androidx.camera:camera-extensions:$camerax_version")
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")

    // ML kit
    // Use this dependency to bundle the model with your app
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")

    implementation ("com.google.android.gms:play-services-vision:20.1.3")

    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.google.api-client:google-api-client:2.2.0")
    implementation ("com.android.volley:volley:1.2.1")

    implementation ("org.jetbrains:annotations:24.1.0")


    implementation ("com.github.deano2390:MaterialShowcaseView:1.3.7@aar")

    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

}
