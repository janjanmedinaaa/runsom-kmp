import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.medina.juanantonio.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Runsom"
            packageVersion = "1.0.0"

            windows {
                iconFile.set(project.file("icons/runsom_logo.ico"))
            }

            macOS {
                iconFile.set(project.file("icons/runsom_logo.icns"))
            }

            linux {
                iconFile.set(project.file("icons/runsom_logo.png"))
            }
        }
    }
}