package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.extensions.MultipleFilesExtension
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

interface MultipleFilesProperties: TemplateProperties {

    companion object {
        const val DEFAULT_TEMPLATE_DIR      = "src/main/javaTemplate"
    }

    /// templates file directory
    @get:InputDirectory
    @get:Optional
    val templateDir: DirectoryProperty

    /// templates files
    @get:InputFiles
    @get:Optional
    val templateFiles: ConfigurableFileCollection

    fun configureFrom(extension: MultipleFilesExtension) {
        templateDir.convention(extension.templateDir)
        templateFiles.from(extension.templateFiles)
    }
}
