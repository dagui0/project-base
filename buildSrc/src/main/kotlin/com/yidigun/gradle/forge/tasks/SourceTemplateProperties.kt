package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.extensions.SourceTemplateExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

interface SourceTemplateProperties: TemplateProperties {

    companion object {
        const val DEFAULT_OUTPUT_DIR: String = "generated/sources/templates"
    }

    @get:Input
    @get:Optional
    val javaVersion: Property<Int>

    @get:Input
    @get:Optional
    val projectName: Property<String>

    @get:Input
    @get:Optional
    val projectGroup: Property<String>

    @get:Input
    @get:Optional
    val projectVersion: Property<String>

    fun configureFrom(extension: SourceTemplateExtension) {
        super.configureFrom(extension)
        javaVersion.convention(extension.javaVersion)
        projectName.convention(extension.projectName)
        projectGroup.convention(extension.projectGroup)
        projectVersion.convention(extension.projectVersion)
    }
}
