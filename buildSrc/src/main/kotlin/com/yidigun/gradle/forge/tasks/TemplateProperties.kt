package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.extensions.TemplateExtension
import com.yidigun.gradle.forge.templates.TemplateLanguage
import com.yidigun.gradle.forge.templates.TemplateProcessorConfig
import com.yidigun.gradle.forge.utils.OutputCommentStyle
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

interface TemplateProperties {

    companion object {
        const val DEFAULT_CHARSET      = "UTF-8"
    }

    @get:OutputDirectory
    @get:Optional
    val outputDir: DirectoryProperty

    @get:Input
    @get:Optional
    val templateLanguage: Property<TemplateLanguage>

    @get:Input
    @get:Optional
    val templateProcessorConfig: Property<TemplateProcessorConfig>

    @get:Input
    @get:Optional
    val templateFileSuffix: Property<String>

    @get:Input
    @get:Optional
    val charset: Property<String>

    @get:Input
    @get:Optional
    val model: MapProperty<String, Any>

    @get:Input
    @get:Optional
    val headerText: Property<String?>

    @get:Input
    @get:Optional
    val footerText: Property<String?>

    @get:Input
    @get:Optional
    val suffixCommentStyleMap: MapProperty<String, OutputCommentStyle>

    fun configureFrom(extension: TemplateExtension) {
        outputDir.convention(extension.outputDir)
        templateLanguage.convention(extension.templateLanguage)
        templateProcessorConfig.convention(extension.templateProcessorConfig)
        templateFileSuffix.convention(extension.templateFileSuffix)
        charset.convention(extension.charset)
        model.convention(extension.model)
        headerText.convention(extension.headerText)
        footerText.convention(extension.footerText)
        suffixCommentStyleMap.convention(extension.suffixCommentStyleMap)
    }
}
