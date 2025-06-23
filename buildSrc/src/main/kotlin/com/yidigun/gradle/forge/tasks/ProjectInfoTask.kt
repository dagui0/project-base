package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.extensions.ProjectInfoExtension
import com.yidigun.gradle.forge.extensions.SingleFileExtension
import com.yidigun.gradle.forge.extensions.SourceTemplateExtension
import java.io.File

/// Generate ProjectInfo.java at project root packaage.
abstract class ProjectInfoTask: SourceTemplateTask(), SingleFileProperties {

    init {
        group = "generate"
        description = "Generate ProjectInfo class at project root package, using gradle configurations"
    }

    override fun prepareTemplateDir(): File {
        return project.layout.projectDirectory.asFile;
    }

    override fun prepareTemplateFiles(): List<File> {
        return listOf(
            templateFile.get()
        )
    }

    fun configureFrom(extension: ProjectInfoExtension) {
        (this as SourceTemplateProperties).configureFrom(extension as SourceTemplateExtension)
        (this as SingleFileProperties).configureFrom(extension as SingleFileExtension)
    }
}
