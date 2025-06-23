package com.yidigun.gradle.forge.templates.simple

import com.yidigun.gradle.forge.templates.TemplateProcessor
import com.yidigun.gradle.forge.templates.TemplateProcessorConfig

data class SimpleTemplateConfig(
    val commentPrefix: String
): TemplateProcessorConfig {
    override fun getProcessor(): TemplateProcessor {
        if (commentPrefix == SimpleTemplateProcessor.DEFAULT_COMMENT_PREFIX) {
            return SimpleTemplateProcessor.DEFAULT_INSTANCE
        }
        return SimpleTemplateProcessor(commentPrefix)
    }
}
