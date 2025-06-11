plugins {
    id("java-library")
    alias(libs.plugins.lombok)
}

ext {
    set("project.info.dir", "generated/sources/project-info/java/main")
    set("project.info.package", "com.yidigun.base")
}

group = rootProject.group
version = rootProject.version

// ProjectInfo generation task
val generateProjectInfoDir = project.layout.buildDirectory.dir(project.ext["project.info.dir"] as String)
sourceSets.main.get().java.srcDir(generateProjectInfoDir)
sourceSets.test.get().java.srcDir(generateProjectInfoDir)
apply(from = "../gradle/project-info.gradle.kts")

repositories {
    mavenCentral()
}

dependencies {

    // dependencies
    implementation(libs.jetbrains.annotations)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)

    // Lombok
    lombok(libs.lombok)

    // junit
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<JavaCompile> {
    options.release.set(libs.versions.java.get().toInt())
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.test {
    useJUnitPlatform()
}
