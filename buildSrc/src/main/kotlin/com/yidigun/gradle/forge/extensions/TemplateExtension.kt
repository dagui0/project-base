package com.yidigun.gradle.forge.extensions

import com.yidigun.gradle.forge.templates.TemplateLanguage
import com.yidigun.gradle.forge.templates.TemplateProcessorConfig
import com.yidigun.gradle.forge.utils.OutputCommentStyle
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface TemplateExtension {

    val outputDir: DirectoryProperty

    val templateLanguage: Property<TemplateLanguage>

    val templateProcessorConfig: Property<TemplateProcessorConfig>

    val templateFileSuffix: Property<String>

    val charset: Property<String>

    val model: MapProperty<String, Any>

    val headerText: Property<String?>

    val footerText: Property<String?>

    val suffixCommentStyleMap: MapProperty<String, OutputCommentStyle>
}
