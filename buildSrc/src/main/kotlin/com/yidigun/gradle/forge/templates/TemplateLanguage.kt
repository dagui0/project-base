package com.yidigun.gradle.forge.templates

interface TemplateLanguage {

    fun id(): String

    fun defaultSuffix(): String

    fun defaultConfig(): TemplateProcessorConfig
}
