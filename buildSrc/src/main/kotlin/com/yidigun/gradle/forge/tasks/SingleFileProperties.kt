package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.extensions.SingleFileExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import java.io.File
import java.net.URI

interface SingleFileProperties: TemplateProperties {

    /// templates file
    @get:InputFile
    @get:Optional
    val templateFile: Property<File>

    /// templates file
    @get:Input
    @get:Optional
    val templateURL: Property<URI>

    fun configureFrom(extension: SingleFileExtension) {
        templateFile.convention(extension.templateFile)
        templateURL.convention(extension.templateURL)
    }
}
