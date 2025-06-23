package com.yidigun.gradle.forge.extensions

import org.gradle.api.provider.Property

interface BuildInfoExtension : TemplateExtension, SingleFileExtension {

    val enabled: Property<Boolean>
}
