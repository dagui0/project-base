package com.yidigun.gradle.forge.templates.jslt

import com.yidigun.gradle.forge.templates.TemplateContext
import com.yidigun.gradle.forge.templates.TemplateLanguage
import com.yidigun.gradle.forge.templates.TemplateProcessor
import com.yidigun.gradle.forge.templates.TemplateProcessorConfig
import java.io.Reader
import java.io.Writer


class JsltConfig: TemplateProcessorConfig {
    override fun getProcessor(): TemplateProcessor {
        return JsltProcessor()
    }
}

class JsltProcessor: TemplateProcessor {
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