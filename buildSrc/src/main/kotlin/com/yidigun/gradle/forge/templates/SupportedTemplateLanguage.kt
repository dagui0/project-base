package com.yidigun.gradle.forge.templates

import com.yidigun.gradle.forge.templates.freemarker.FreemarkerConfig
import com.yidigun.gradle.forge.templates.freemarker.FreemarkerProcessor
import com.yidigun.gradle.forge.templates.handlebars.HandlebarsConfig
import com.yidigun.gradle.forge.templates.jslt.JsltConfig
import com.yidigun.gradle.forge.templates.simple.SimpleTemplateConfig
import com.yidigun.gradle.forge.templates.simple.SimpleTemplateProcessor

internal enum class SupportedTemplateLanguage(
    val defaultSuffix: String
): TemplateLanguage {

    SIMPLE("tmpl"),
    FREEMARKER("ftl"),
    HANDLEBARS("handlebars"),
    JSTL("jslt");

    override fun defaultSuffix(): String {
        return defaultSuffix
    }

    override fun defaultConfig(): TemplateProcessorConfig {
        return when (this) {
            SIMPLE -> SimpleTemplateConfig(SimpleTemplateProcessor.DEFAULT_COMMENT_PREFIX)
            FREEMARKER -> FreemarkerConfig(FreemarkerProcessor.DEFAULT_CONFIGURATION)
            HANDLEBARS -> HandlebarsConfig()
            JSTL -> JsltConfig()
        }
    }

    override fun id(): String {
        return name.lowercase()
    }

    companion object {

        fun ofId(id: String): SupportedTemplateLanguage {
            return values().firstOrNull { it.id() == id.lowercase() }
                ?: throw IllegalArgumentException("Unsupported template language: $id")
        }

        fun ofSuffix(suffix: String): SupportedTemplateLanguage? {
            return values().firstOrNull { it.defaultSuffix() == suffix.lowercase() }
                ?: throw IllegalArgumentException("Unsupported template language: $suffix")
        }
    }
}
