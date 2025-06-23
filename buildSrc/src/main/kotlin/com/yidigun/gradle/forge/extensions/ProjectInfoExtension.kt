package com.yidigun.gradle.forge.extensions

import org.gradle.api.provider.Property

interface ProjectInfoExtension : TemplateExtension, SourceTemplateExtension, SingleFileExtension {

    val enabled: Property<Boolean>
}
