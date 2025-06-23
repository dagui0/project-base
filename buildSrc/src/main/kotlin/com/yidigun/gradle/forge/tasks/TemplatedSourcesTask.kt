package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.extensions.MultipleFilesExtension
import com.yidigun.gradle.forge.extensions.SourceTemplateExtension
import com.yidigun.gradle.forge.extensions.TemplatedSourcesSet
import org.gradle.api.tasks.Internal
import java.io.File

/// Gradle custom task to generate templated sources to source files.
abstract class TemplatedSourcesTask : SourceTemplateTask(), MultipleFilesProperties {

    init {
        group = "generate"
        description = "Generates templated sources based on the provided templates and data model."
    }

    override fun prepareTemplateDir(): File {
        return templateDir.get().asFile
    }

    override fun prepareTemplateFiles(): List<File> {
        val templateFiles = templateFiles.asFileTree.toList()
        return templateFiles
    }

    fun configureFrom(config: TemplatedSourcesSet) {
        (this as SourceTemplateProperties).configureFrom(config as SourceTemplateExtension)
        (this as MultipleFilesProperties).configureFrom(config as MultipleFilesExtension)
    }
}
