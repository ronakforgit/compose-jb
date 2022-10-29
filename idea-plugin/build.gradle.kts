import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.10.0-SNAPSHOT"
    id("org.jetbrains.changelog") version "1.3.1"
}

val projectProperties = ProjectProperties(project)

group = "org.jetbrains.compose.desktop.ide"
version = projectProperties.deployVersion

sourceSets {
    main {
        java.srcDir("src/main/kotlin")
    }
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation("org.jetbrains.compose:preview-rpc")
    implementation("org.jetbrains.compose.compiler:compiler-hosted:1.3.2")
}

intellij {
    pluginName.set("Compose Multiplatform IDE Support")
    type.set(projectProperties.platformType)
    if (projectProperties.platformVersion == "local") {
        localPath.set(projectProperties.platformPath)   
    } else {
        version.set(projectProperties.platformVersion)
    }
    downloadSources.set(projectProperties.platformDownloadSources)

    plugins.set(
        listOf(
            "java",
            "com.intellij.gradle",
            "org.jetbrains.kotlin"
        )
    )
}

tasks.buildSearchableOptions {
    // temporary workaround
    enabled = false
}

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "17"
    }
    withType<KotlinJvmCompile> {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs += listOf("-Xopt-in=org.jetbrains.kotlin.utils.addToStdlib.UnsafeCastFunction")
    }

    publishPlugin {
        token.set(System.getenv("IDE_PLUGIN_PUBLISH_TOKEN"))
        channels.set(projectProperties.pluginChannels)
    }

    patchPluginXml {
        sinceBuild.set(projectProperties.pluginSinceBuild)
        untilBuild.set(projectProperties.pluginUntilBuild)
    }
    
    runIde {
        maxHeapSize = "2g"
    }

    runPluginVerifier {
        ideVersions.set(projectProperties.pluginVerifierIdeVersions)
    }
}

class ProjectProperties(private val project: Project) {
    val deployVersion get() = stringProperty("deploy.version")
    val platformType get() = stringProperty("platform.type")
    val platformVersion get() = stringProperty("platform.version")
    val platformPath get() = stringProperty("platform.path")
    val platformDownloadSources get() = stringProperty("platform.download.sources").toBoolean()
    val pluginChannels get() = listProperty("plugin.channels")
    val pluginSinceBuild get() = stringProperty("plugin.since.build")
    val pluginUntilBuild get() = stringProperty("plugin.until.build")
    val pluginVerifierIdeVersions get() = listProperty("plugin.verifier.ide.versions")

    private fun stringProperty(key: String): String =
        project.findProperty(key)!!.toString()
    private fun listProperty(key: String): List<String> =
        stringProperty(key).split(",").map { it.trim() }
}
