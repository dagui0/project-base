package com.yidigun.gradle.forge.templates.handlebars

import com.yidigun.gradle.forge.templates.TemplateContext
import com.yidigun.gradle.forge.templates.TemplateLanguage
import com.yidigun.gradle.forge.templates.TemplateProcessor
import com.yidigun.gradle.forge.templates.TemplateProcessorConfig
import java.io.Reader
import java.io.Writer

class HandlebarsConfig: TemplateProcessorConfig {
    override fun getProcessor(): TemplateProcessor {
        TODO("Not yet implemented")
    }
}

class HandlebarsProcessor: TemplateProcessor {

    override fun language(): TemplateLanguage {
        TODO("Not yet implemented")
    }

    override fun process(
        context: TemplateContext,
        template: Reader,
        output: Writer
    ) {
        TODO("Not yet implemented")
    }

}