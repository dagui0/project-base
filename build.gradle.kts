import org.gradle.internal.serialize.codecs.stdlib.linkedListCodec

plugins {
    id("java-library")
}

ext {
    set("java.version", 23)
    set("lombok.version", "1.18.38")
    set("jetbrains.annotations.version", "26.0.2")
    set("guava.version", "33.4.8-jre")
    set("commons.lang3.version", "3.17.0")
    set("junit.version", "5.13.0")
    set("compile.testing.version", "0.21.0")
}

group = "com.yidigun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // dependencies
    implementation("org.jetbrains:annotations:${project.ext["jetbrains.annotations.version"]}")
    implementation("com.google.guava:guava:${project.ext["guava.version"]}")
    implementation("org.apache.commons:commons-lang3:${project.ext["commons.lang3.version"]}")
    //implementation("org.junit.jupiter:junit-jupiter:${project.ext["junit.version"]}")

    // Lombok
    compileOnly("org.projectlombok:lombok:${project.ext["lombok.version"]}")
    annotationProcessor("org.projectlombok:lombok:${project.ext["lombok.version"]}")
    testImplementation("org.projectlombok:lombok:${project.ext["lombok.version"]}")
    testAnnotationProcessor("org.projectlombok:lombok:${project.ext["lombok.version"]}")

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter:${project.ext["junit.version"]}")
    testImplementation("com.google.testing.compile:compile-testing:${project.ext["compile.testing.version"]}")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.release.set(project.ext["java.version"] as Int)
    options.compilerArgs.addAll(listOf("-Xlint:all"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Javadoc> {
    options {
        if (this is StandardJavadocDocletOptions) {
            encoding = "UTF-8"
            charSet = "UTF-8"
            title = "${project.name} ${project.version} API Documentation"
            version = true
            memberLevel = JavadocMemberLevel.PRIVATE
            links = listOf(
                "https://docs.oracle.com/en/java/javase/${project.ext["java.version"]}/docs/api/",
                "https://javadoc.io/doc/org.projectlombok/lombok/${project.ext["lombok.version"]}/",
                "https://javadoc.io/doc/org.jetbrains/annotations/${project.ext["jetbrains.annotations.version"]}/".toString(),
                "https://javadoc.io/doc/org.junit.jupiter/junit-jupiter/${project.ext["junit.version"]}/",
                "https://javadoc.io/doc/com.google.guava/guava/${project.ext["guava.version"]}/",
                "https://javadoc.io/doc/org.apache.commons/commons-lang3/${project.ext["commons.lang3.version"]}/"
            )
        }
    }
}
