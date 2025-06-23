package com.yidigun.gradle.forge

import com.yidigun.gradle.forge.extensions.BuildInfoExtension
import com.yidigun.gradle.forge.extensions.ProjectInfoExtension
import com.yidigun.gradle.forge.extensions.TemplatedSourcesSet
import com.yidigun.gradle.forge.tasks.BuildInfoTask
import com.yidigun.gradle.forge.tasks.ProjectInfoTask
import com.yidigun.gradle.forge.tasks.TemplateTask
import com.yidigun.gradle.forge.tasks.TemplatedSourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

class GradleForgePlugin : Plugin<Project> {

    val taskMap: Map<String, TaskProvider<out TemplateTask>> by lazy {
        taskMapBuilding.toMap()
    }

    private val taskMapBuilding: MutableMap<String, TaskProvider<out TemplateTask>> = mutableMapOf()

    override fun apply(project: Project) {

        //
        // Singleton Tasks
        //

        // ProjectInfoTask
        val projectInfoExtension: ProjectInfoExtension = project.extensions
            .create<ProjectInfoExtension>("projectInfo")
        projectInfoExtension.applyDefaults(project)
        val generateProjectInfoTask = project.tasks
            .register<ProjectInfoTask>("generateProjectInfo") {
                onlyIf("projectInfo extension is enabled") {
                    projectInfoExtension.enabled.getOrElse(false)
                }
                configureFrom(projectInfoExtension)
            }
        addToSourceSet(project, generateProjectInfoTask.flatMap { it.outputDir })
        taskMapBuilding.put("generateProjectInfo", generateProjectInfoTask)

        // BuildInfoTask
        val buildInfoExtension = project.extensions
            .create<BuildInfoExtension>("buildInfo")
        buildInfoExtension.applyDefaults(project)
        val generateBuildInfoTask = project.tasks
            .register<BuildInfoTask>("generateBuildInfo") {
                onlyIf("buildInfo extension is enabled") {
                    buildInfoExtension.enabled.getOrElse(false)
                }
                configureFrom(buildInfoExtension)
            }
        project.tasks.named("processResources") {
            dependsOn(generateBuildInfoTask)
        }
        taskMapBuilding.put("generateBuildInfo", generateProjectInfoTask)

        //
        // Factory Tasks
        //

        // TemplatedSourcesTask
        val templatedSourcesContainer = project.container(TemplatedSourcesSet::class.java)
            .all {
                applyDefaults(project)

                val nameCapitalized = name.replaceFirstChar { it.uppercase() }
                val taskName = "generate${nameCapitalized}Sources"
                val config = this
                val task = project.tasks.register<TemplatedSourcesTask>(taskName) {
                    configureFrom(config)
                }
                taskMapBuilding.put(taskName, task)
            }
        project.extensions.add("templatedSources", templatedSourcesContainer)
    }

    /// 지정한 출력 디렉토리를 자바 컴파일 소스셋으로 등록
    private fun addToSourceSet(
        project: Project,
        outputDirProvider: Provider<Directory>,
        sourceSetName: String = "main"
    ) {
        project.plugins.withType(JavaPlugin::class.java) {
            val javaExtension = project.extensions.getByType<JavaPluginExtension>()
            javaExtension.sourceSets.named(sourceSetName) {
                java.srcDir(outputDirProvider)
            }
        }
    }
}

/*

fun registerForgeTask(project: Project, name: String, config: ForgeTaskConfig) {
    project.tasks.register<ForgeTask>(name) {
        // 1. 사용자 설정으로부터 resourceSet을 설정합니다.
        val resourceSetProvider = project.provider { ResourceSets.create(project, config.source) }
        this.resourceSet.set(resourceSetProvider)

        // 2. ✨ 핵심: 설정이 완료된 후, 증분 빌드 가능 여부를 확인하고 태스크 옵션을 동적으로 변경합니다.
        this.doFirst("Configure incremental execution") {
            val support = this.resourceSet.get().buildSupport
            if (!support.isFullyTrackable.get()) {
                logger.warn("Task '{}' contains non-physical resources, incremental build is disabled.", this.name)
                // 이 태스크는 절대 UP-TO-DATE가 될 수 없다고 선언합니다.
                this.outputs.upToDateWhen { false }
            }
        }
    }
}

 */