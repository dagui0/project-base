package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.extensions.BuildInfoExtension
import com.yidigun.gradle.forge.extensions.SingleFileExtension
import com.yidigun.gradle.forge.extensions.TemplateExtension
import org.gradle.api.tasks.Internal
import java.io.File

/// Generate META-INF/build-info.yaml for packaging
abstract class BuildInfoTask: TemplateTask(), SingleFileProperties {

    init {
        group = "build"
        description = "Generate build-info.yaml and build-info.properties for packaging"
    }

    override fun prepareTemplateDir(): File {
        return project.layout.projectDirectory.asFile;
    }

    override fun prepareTemplateFiles(): List<File> {
        return listOf(
            templateFile.get()
        )
    }

    fun configureFrom(extension: BuildInfoExtension) {
        (this as TemplateProperties).configureFrom(extension as TemplateExtension)
        (this as SingleFileProperties).configureFrom(extension as SingleFileExtension)
    }
}
