plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.freemarker:freemarker:2.3.34")

    // Test
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

val classpath = configurations.compileClasspath

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("forgePlugin") {
            id = "com.yidigun.gradle.forge"
            implementationClass = "com.yidigun.gradle.forge.GradleForgePlugin"
            displayName = "Gradle Forge Plugin"
            description = "A plugin to generate various source codes and resources."
        }
    }
}
