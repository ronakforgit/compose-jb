plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
}

version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop")
    ios()
    iosSimulatorArm64()
    js(IR) {
        browser()
        binaries.executable()
    }
    macosX64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    cocoapods {
        summary = "Shared code for the sample"
        homepage = "https://github.com/JetBrains/compose-jb"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                //implementation("org.jetbrains.compose.components:components-resources:1.3.0-beta04-dev871")//todo should work
            }
        }
        val iosX64Main by getting {
            dependencies {
                implementation("org.jetbrains.compose.components:components-resources-iosX64:1.3.0-beta04-dev871")//workaround
            }
        }
        val iosArm64Main by getting {
            dependencies {
                implementation("org.jetbrains.compose.components:components-resources-iosArm64:1.3.0-beta04-dev871")//workaround
            }
        }
        val iosMain by getting
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
            dependencies {
                implementation("org.jetbrains.compose.components:components-resources-iosSimulatorArm64:1.3.0-beta04-dev871")//workaround
            }
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("org.jetbrains.compose.components:components-resources:1.3.0-beta04-dev871")//workaround
            }
        }
        val macosMain by creating {
            dependsOn(commonMain)
        }
        val macosX64Main by getting {
            dependsOn(macosMain)
            dependencies {
                implementation("org.jetbrains.compose.components:components-resources-macosX64:1.3.0-beta04-dev871")//workaround
            }
        }
        val macosArm64Main by getting {
            dependsOn(macosMain)
            dependencies {
                implementation("org.jetbrains.compose.components:components-resources-macosArm64:1.3.0-beta04-dev871")//workaround
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.components:components-resources:1.3.0-beta04-dev871")//workaround
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.components:components-resources:1.3.0-beta04-dev871")//workaround
            }
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        named("main") {
            resources.srcDir("src/commonMain/resources")
        }
    }
}

compose.experimental {
    web.application {}
}
