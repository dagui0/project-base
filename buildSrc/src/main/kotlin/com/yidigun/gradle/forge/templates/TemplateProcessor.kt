package com.yidigun.gradle.forge.templates

import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer

interface TemplateProcessor {

    fun process(context: TemplateContext, template: String): String {
        val s = StringWriter()
        process(context, StringReader(template), s)
        return s.toString()
    }

    fun process(context: TemplateContext, template: Reader): String {
        val s = StringWriter()
        process(context, template, s)
        return s.toString()
    }

    fun language(): TemplateLanguage

    fun process(context: TemplateContext, template: Reader, output: Writer)
}
