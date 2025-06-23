package com.yidigun.gradle.forge.extensions

import org.gradle.api.provider.Property
import java.io.File
import java.net.URI

interface SingleFileExtension: TemplateExtension {

    val templateFile: Property<File>

    val templateURL: Property<URI>
}
