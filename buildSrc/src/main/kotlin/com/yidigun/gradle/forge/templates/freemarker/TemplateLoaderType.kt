package com.yidigun.gradle.forge.templates.freemarker

import com.yidigun.gradle.forge.utils.openReader
import freemarker.cache.TemplateLoader
import java.io.File
import java.io.Reader

enum class TemplateLoaderType {

    CLASSPATH, SINGLE_READER, FILESYSTEM, URI;

    fun createTemplateLoader(source: Any? = null): TemplateLoader {

        return when (this) {

            // for internal use only
            CLASSPATH -> ClasspathTemplateLoader

            SINGLE_READER -> {
                when (source) {
                    is Reader -> SingleReaderTemplateLoader(source)
                    is String -> SingleReaderTemplateLoader(source.openReader())
                    is File -> SingleReaderTemplateLoader(source.openReader())
                    is java.net.URI -> SingleReaderTemplateLoader(source.openReader())
                    else -> {
                        throw IllegalArgumentException(
                            "SINGLE_ONLY loader type requires a 'java.io.File', 'java.net.URI'," +
                                    " 'java.io.Reader' or 'String' instance for its parameter as template source. " +
                                    "Got: ${source?.javaClass?.name ?: "null"}"
                        )
                    }
                }
            }

            FILESYSTEM -> {
                if (source is File) {
                    FilesystemTemplateLoader(source)
                } else {
                    throw IllegalArgumentException(
                        "FILESYSTEM loader type requires a 'java.io.File' instance for its parameter for 'baseDir'. " +
                                "Got: ${source?.javaClass?.name ?: "null"}"
                    )
                }
            }

            URI -> {
                if (source is java.net.URI) {
                    URITemplateLoader(source)
                } else {
                    throw IllegalArgumentException(
                        "URI loader type requires a 'java.net.URI' instance for its parameter for 'baseURI'. " +
                                "Got: ${source?.javaClass?.name ?: "null"}"
                    )
                }
            }
        }
    }

    companion object {

        fun of(type: String): TemplateLoaderType {
            return when (type.lowercase()) {
                "single" -> SINGLE_READER
                "classpath" -> CLASSPATH
                "filesystem" -> FILESYSTEM
                "uri" -> URI
                else -> throw IllegalArgumentException("Unknown FreemarkerLoaderType: $type")
            }
        }

        fun createTemplateLoader(source: Any): TemplateLoader {
            return SINGLE_READER.createTemplateLoader(source)
        }

        fun createTemplateLoader(type: String, source: Any? = null): TemplateLoader {
            return of(type).createTemplateLoader(source)
        }
    }
}
