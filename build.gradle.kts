@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.ComposeBuildConfig.composeVersion
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.application") version "7.4.0-alpha10"
}

version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }

    android()

    jvm("desktop")

    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {

        val koinVersion = "3.2.0"

        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                //implementation("org.jetbrains.kotlin:kotlin-stdlib")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.insert-koin:koin-core:$koinVersion")
                //implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")    doesn't work
                implementation("io.github.aakira:napier:2.6.1")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val jsMain by getting {
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.8.0")
                implementation("androidx.appcompat:appcompat:1.5.0")
                implementation("androidx.activity:activity-compose:1.5.1")
                implementation("io.insert-koin:koin-android:$koinVersion")
            }
        }

        all {
            languageSettings {
                optIn("kotlinx.coroutines.ObsoleteCoroutinesApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }
}

android {
    sourceSets {
        val main by getting
        main.java.setSrcDirs(listOf("src/androidMain/kotlin"))
        main.res.setSrcDirs(listOf("src/androidMain/res"))
        main.resources.setSrcDirs(
            listOf(
                "src/androidMain/resources",
                "src/commonMain/resources",
            )
        )
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
    }

    buildTypes {
        debug {

        }
        release {
            //signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

/*    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            //"-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.ui.text.ExperimentalTextApi",
            "-opt-in=coil.annotation.ExperimentalCoilApi",
            "-XXLanguage:+InlineClasses"
        )

    }*/

    /*  buildFeatures {
          compose = true
      }

      composeOptions {
          kotlinCompilerExtensionVersion = "1.2.0"
      }

      packagingOptions {
          resources {
              excludes += "/META-INF/{AL2.0,LGPL2.1}"
          }
      }*/
}

compose {
    desktop {
        application {
            mainClass = "Main_desktopKt"

            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "Tetris MP"
                packageVersion = "1.0.0"

                windows {
                    menuGroup = "Compose Examples"
                    // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                    upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
                }
            }
        }
    }

/*    android {
        useAndroidX = true
        androidxVersion = "1.2.0"
    }*/

    experimental {
        web.application {}
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    rootProject.extensions.configure<NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.10.0"
        nodeVersion = "16.0.0"
    }
}


// TODO: remove when https://youtrack.jetbrains.com/issue/KT-50778 fixed
project.tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile::class.java).configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xir-dce-runtime-diagnostic=log"
    )
}
