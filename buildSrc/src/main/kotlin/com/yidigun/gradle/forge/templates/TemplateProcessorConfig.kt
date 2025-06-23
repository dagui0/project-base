package com.yidigun.gradle.forge.templates

import java.io.Serializable

interface TemplateProcessorConfig: Serializable {
    fun getProcessor(): TemplateProcessor
}
