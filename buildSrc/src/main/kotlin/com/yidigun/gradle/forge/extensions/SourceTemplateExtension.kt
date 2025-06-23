package com.yidigun.gradle.forge.extensions

import org.gradle.api.provider.Property

interface SourceTemplateExtension: TemplateExtension {

    val javaVersion: Property<Int>

    val projectName: Property<String>

    val projectGroup: Property<String>

    val projectVersion: Property<String>
}
