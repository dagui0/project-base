package com.yidigun.gradle.forge.templates.freemarker

import com.yidigun.gradle.forge.templates.TemplateProcessor
import com.yidigun.gradle.forge.templates.TemplateProcessorConfig
import freemarker.cache.TemplateLoader
import freemarker.template.Configuration
import java.io.File
import java.net.URL

data class FreemarkerConfig(
    val configuration: Configuration,
    val loaderType: TemplateLoaderType = TemplateLoaderType.SINGLE_READER,
    val templateString: String? = null,
    val baseDir: File? = null,
    val baseURL: URL? = null
): TemplateProcessorConfig {
    override fun getProcessor(): TemplateProcessor {
        return FreemarkerProcessor(configuration)
    }

    fun createTemplateLoader(): TemplateLoader {
        return when (loaderType) {
            TemplateLoaderType.CLASSPATH -> ClasspathTemplateLoader
            TemplateLoaderType.SINGLE_READER -> loaderType.createTemplateLoader(templateString ?: "")
            TemplateLoaderType.FILESYSTEM -> {
                if (baseDir != null) {
                    FilesystemTemplateLoader(baseDir)
                } else {
                    throw IllegalArgumentException("FILESYSTEM loader type requires a 'baseDir' parameter.")
                }
            }
            TemplateLoaderType.URI -> {
                if (baseURL != null) {
                    URITemplateLoader(baseURL.toURI())
                } else {
                    throw IllegalArgumentException("URI loader type requires a 'baseURL' parameter.")
                }
            }
        }
    }
}
