plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.twt.virtualpet"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(libs.ashley)
    api(libs.badlogicgames.gdx.ai)
    api(libs.gdx.box2d)
    api(libs.gdx.freetype)
    api(libs.gdx)
    api(libs.vis.ui)
    api(libs.ktx.actors)
    api(libs.ktx.ai)
    api(libs.ktx.app)
    api(libs.ktx.artemis)
    api(libs.ktx.ashley)
    api(libs.ktx.assets.async)
    api(libs.ktx.assets)
    api(libs.ktx.async)
    api(libs.ktx.box2d)
    api(libs.ktx.collections)
    api(libs.ktx.freetype.async)
    api(libs.ktx.freetype)
    api(libs.ktx.graphics)
    api(libs.ktx.i18n)
    api(libs.ktx.inject)
    api(libs.ktx.json)
    api(libs.ktx.log)
    api(libs.ktx.math)
    api(libs.ktx.preferences)
    api(libs.ktx.reflect)
    api(libs.ktx.scene2d)
    api(libs.ktx.style)
    api(libs.ktx.tiled)
    api(libs.ktx.vis.style)
    api(libs.ktx.vis)
    api(libs.artemis.odb)
    //noinspection GradleDependency
    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core)
}