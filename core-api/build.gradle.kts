// core-api/build.gradle.kts
plugins {
    id("java-library")
}

group = rootProject.group
version = rootProject.version

// generateProjectInfo task
val generateProjectInfoDir = project.layout.buildDirectory.dir(rootProject.ext["project.info.dir"] as String)
sourceSets.main.get().java.srcDir(generateProjectInfoDir)
sourceSets.test.get().java.srcDir(generateProjectInfoDir)
apply(from = "project-info.gradle.kts")

repositories {
    mavenCentral()
}

dependencies {

    // dependencies
    compileOnlyApi(libs.jetbrains.annotations)
    implementation(libs.commons.lang3)

    // Lombok
    compileOnly(libs.lombok)
}

tasks.withType<JavaCompile> {
    options.release.set(libs.versions.java.get().toInt())
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.test {
    enabled = false
}
