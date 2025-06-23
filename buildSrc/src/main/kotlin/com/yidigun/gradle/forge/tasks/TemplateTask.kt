package com.yidigun.gradle.forge.tasks

import com.yidigun.gradle.forge.templates.TemplateContext
import com.yidigun.gradle.forge.templates.TemplateProcessor
import com.yidigun.gradle.forge.utils.OutputCommentStyle
import com.yidigun.gradle.forge.utils.SequenceReader
import com.yidigun.gradle.forge.utils.openReader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.time.Instant
import java.time.ZoneId
import kotlin.collections.forEach

abstract class TemplateTask : DefaultTask(), TemplateProperties {

    protected abstract fun prepareTemplateDir(): File

    protected abstract fun prepareTemplateFiles(): List<File>

    protected open fun prepareTemplateURLs(): List<URL> {
        return emptyList()
    }

    protected open fun prepareProcessorOptions(): Map<String, Any> {
        return emptyMap()
    }

    protected open fun prepareModel(): Map<String, Any> {
        return model.get()
    }

    private val templateProcessor: TemplateProcessor by lazy {
        templateProcessorConfig.get().getProcessor()
    }

    @TaskAction
    fun generate() {

        val defaultContext = createDefaultContext()
        val outputDir = prepareOutputDir()
        val templateDir = prepareTemplateDir()

        val templateFiles = prepareTemplateFiles()
        templateFiles.forEach { templateFile ->
            processTemplateFile(defaultContext, templateDir, templateFile, outputDir)
        }

        val templateURLs = prepareTemplateURLs()
        templateURLs.forEach { templateURL ->
            processTemplateURL(defaultContext, templateURL, outputDir)
        }
    }

    protected fun processTemplateFile(defaultContext: TemplateContext,
                                      templateDir: File, templateFile: File, outputDir: File) {

        // prepare per template context
        val relativeFile = templateFile.relativeTo(templateDir)
        val outputFile = findOutputFile(outputDir, relativeFile)
        outputFile.parentFile.mkdirs()
        val now = Instant.now()
        val context = TemplateContext(
            model = mapOf(
                "templates.filename" to relativeFile.path,
                "templates.generate.time" to now,
                "templates.generate.localtime" to now.atZone(ZoneId.systemDefault()),
                "templates.task.name" to this.name,
                "templates.language" to templateProcessor.language().id(),
            ) + defaultContext.model,
            options = defaultContext.options
        )

        // header & footer pre-processing
        val header = headerText.get().let { templateProcessor.process(context, it) }
        val footer = footerText.get().let { templateProcessor.process(context, it) }

        // now process the template
        val outputCommentStyle = getOutputCommentStyle(outputFile)
        val charset = Charset.forName(charset.get())

        templateFile.openReader(charset).use { originReader ->

            val templateReader = SequenceReader.wrap(
                originReader,
                header.let { outputCommentStyle.createCommentBlock(header) },
                footer.let { outputCommentStyle.createCommentBlock(footer) }
            )

            outputFile.writer(charset).buffered().use { output ->
                templateProcessor.process(context, templateReader, output)
            }
        }
    }

    protected fun processTemplateURL(defaultContext: TemplateContext,
                                     templateURL: URL, outputDir: File) {
        // TODO for classpath resources url
    }

    protected fun getOutputCommentStyle(outputFile: File): OutputCommentStyle {
        val suffixMap: Map<String, OutputCommentStyle>? = suffixCommentStyleMap.getOrNull()
        val ext = outputFile.extension.lowercase()
        return if (suffixMap != null) OutputCommentStyle.ofSuffix(ext, suffixMap)
                else OutputCommentStyle.ofSuffix(ext)
    }

    protected fun findOutputFile(outputDir: File, relativeFile: File): File {
        templateFileSuffix.get().let { suffix ->
            return File(outputDir, relativeFile.path.removeSuffix(".$suffix"))
        }
    }

    protected fun prepareOutputDir(): File {
        return outputDir.get().asFile
    }

    protected open fun createDefaultContext(): TemplateContext {
        val options = mapOf<String, Any>(
            "charset" to charset.get(),
        ) + prepareProcessorOptions()

        return TemplateContext(
            model = prepareModel(),
            options = options.toMutableMap()
        )
    }
}
