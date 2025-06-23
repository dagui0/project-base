// :core-library/build.gradle.kts
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URI

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.lombok)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

val shadowBundle by configurations.creating
dependencies {

    // 통합 배포 대상 내부 모듈들
    shadowBundle(project(":core-api"))
    shadowBundle(project(":annotation-processors"))

    // 내부 모듈은 implementation을 쓰면 의존성이 외부로 노출되므로 주의
    compileOnly(project(":core-api"))
    testImplementation(project(":core-api"))
    annotationProcessor(project(":annotation-processors"))
    testAnnotationProcessor(project(":annotation-processors"))

    // 외부에 공시할 dependencies, 통합 배포될 내부 모듈의 dependencies 도 포함해야 한다.
    compileOnlyApi(libs.jetbrains.annotations)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)

    // Lombok
    lombok(libs.lombok)

    // junit
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    mergeServiceFiles()
    configurations = listOf(shadowBundle)
    dependencies {
        exclude { dep -> dep.moduleGroup.toString() != project.group.toString() }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()
            from(components["java"])

            artifacts.clear()
            artifact(tasks.shadowJar)

            pom {
                name.set(rootProject.ext["publish.name"].toString())
                description.set(rootProject.ext["publish.description"].toString())
                url.set(rootProject.ext["publish.url"].toString())

                licenses {
                    license {
                        name.set(rootProject.ext["publish.license"].toString())
                        url.set(rootProject.ext["publish.license.url"].toString())
                    }
                }
                developers {
                    developer {
                        id.set(rootProject.ext["publish.developer.id"].toString())
                        name.set(rootProject.ext["publish.developer.name"].toString())
                        email.set(rootProject.ext["publish.developer.email"].toString())
                    }
                }
                scm {
                    connection.set(rootProject.ext["publish.scm.connection"].toString())
                    url.set(rootProject.ext["publish.scm.url"].toString())
                }
            }
        }
    }
    repositories {
        maven {
            name = rootProject.ext["publish.repo.name"].toString()
            val releasesRepoUrl = rootProject.ext["publish.repo.release.url"].toString()
            val snapshotsRepoUrl = rootProject.ext["publish.repo.snapshot.url"].toString()
            url = URI(if (rootProject.version.toString()
                    .endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = rootProject.ext["private-repo.username"].toString()
                password = rootProject.ext["private-repo.password"].toString()
            }
        }
    }
}
