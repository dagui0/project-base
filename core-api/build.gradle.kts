// core-api/build.gradle.kts
plugins {
    id("java-library")
}

// generateProjectInfo task
val generateProjectInfoDir = project.layout.buildDirectory.dir(rootProject.ext["project.info.dir"] as String)
sourceSets.main.get().java.srcDir(generateProjectInfoDir)
sourceSets.test.get().java.srcDir(generateProjectInfoDir)
apply(from = "project-info.gradle.kts")

dependencies {

    // dependencies
    compileOnlyApi(libs.jetbrains.annotations)
    implementation(libs.commons.lang3)

    // Lombok
    compileOnly(libs.lombok)

    // junit
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
