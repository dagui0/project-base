package com.yidigun.gradle.forge.templates.freemarker

import com.yidigun.gradle.forge.templates.TemplateContext
import com.yidigun.gradle.forge.templates.TemplateLanguage
import com.yidigun.gradle.forge.templates.SupportedTemplateLanguage
import com.yidigun.gradle.forge.templates.TemplateProcessor
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.Reader
import java.io.Writer
import java.util.Locale
import java.util.TimeZone

internal class FreemarkerProcessor(
    val configuration: Configuration = DEFAULT_CONFIGURATION
): TemplateProcessor {

    companion object {
        val DEFAULT_CONFIGURATION =
            Configuration(Configuration.VERSION_2_3_34).apply {
                setClassForTemplateLoading(
                    FreemarkerProcessor::class.java, "/")
                defaultEncoding = "UTF-8"
                locale = Locale.getDefault()
                numberFormat = "0.##"
                templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
                logTemplateExceptions = false
                wrapUncheckedExceptions = true
                fallbackOnNullLoopVariable = false
                sqlDateAndTimeTimeZone = TimeZone.getDefault()
            }

        val DEFAULT_INSTANCE = FreemarkerProcessor(DEFAULT_CONFIGURATION)
    }

    override fun language(): TemplateLanguage {
        return SupportedTemplateLanguage.FREEMARKER
    }

    override fun process(
        context: TemplateContext,
        template: Reader,
        output: Writer
    ) {
        TODO("Not yet implemented")
    }
}
