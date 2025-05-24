plugins {
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.somnio_nocte.portal"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.05.01"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.SomnioNocte"
                artifactId = "portal"
                version = "0.1.0"

                artifact("$buildDir/outputs/aar/${project.name}-release.aar") {
                    extension = "aar"
                }

                val classesJarFile = file("${buildDir}/intermediates/aar_main_jar/release/classes.jar")
                if (classesJarFile.exists()) {
                    artifact(classesJarFile) {

                    }
                } else {
                    logger.warn("Classes JAR not found at ${classesJarFile.absolutePath}")
                }

                artifact(tasks.named("releaseSourcesJar")) {
                    classifier = "sources"
                }
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/${project.group.toString().substringAfter("com.github.")}/${project.name}")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}