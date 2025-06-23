package com.yidigun.gradle.forge.extensions

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty

interface MultipleFilesExtension: TemplateExtension {

    val templateDir: DirectoryProperty

    val templateFiles: ConfigurableFileCollection

}
