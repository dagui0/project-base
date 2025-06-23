import com.yidigun.gradle.forge.applyDefaults
import com.yidigun.gradle.forge.tasks.TemplatedSourcesTask
import com.yidigun.gradle.forge.utils.OutputCommentStyle

// :core-api/build.gradle.kts

plugins {
    id("java-library")
}

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

val generateTemplatedSources = tasks.register<TemplatedSourcesTask>("generateTemplatedSources") {
    applyDefaults(project)
    templateDir.set(project.layout.projectDirectory.dir("src/main/java"))
    suffixCommentStyleMap.set(mapOf(
        "java" to OutputCommentStyle.C_LANG
    ))
    model.set(mapOf(
        "project.name" to rootProject.name,
        "project.info.package" to "com.yidigun.base",
        "project.info.class" to "ProjectInfo",
    ))
}
sourceSets.named("main") {
    java.srcDir(generateTemplatedSources.flatMap { it.outputDir })
}
tasks.named<JavaCompile>("compileJava") {
    dependsOn(generateTemplatedSources)
    classpath += files(generateTemplatedSources.flatMap { it.outputDir })
}

//plugins {
//    id("com.yidigun.templated-sources") // 1. 만든 플러그인을 적용하고
//}
//
//// 2. 플러그인이 제공하는 'templatedSources' 확장(extension)으로 설정만 한다
//templatedSources {
//    templateDir.set(project.layout.projectDirectory.dir("src/main/java"))
//    model.set(mapOf("project.name" to rootProject.name))
//}
