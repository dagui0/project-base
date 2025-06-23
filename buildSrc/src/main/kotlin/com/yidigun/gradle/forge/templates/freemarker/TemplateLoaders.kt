package com.yidigun.gradle.forge.templates.freemarker

import freemarker.cache.TemplateLoader
import java.io.File
import java.io.Reader
import java.net.URI

internal object ClasspathTemplateLoader: AbstractTemplateLoader() {}
internal class SingleReaderTemplateLoader(val reader: Reader): AbstractTemplateLoader() {}
internal class FilesystemTemplateLoader(val baseDir: File): AbstractTemplateLoader() {}
internal class URITemplateLoader(val baseURI: URI): AbstractTemplateLoader() {}

internal abstract class AbstractTemplateLoader: TemplateLoader {

    override fun findTemplateSource(p0: String?): Any? {
        TODO("Not yet implemented")
    }

    override fun getLastModified(p0: Any?): Long {
        TODO("Not yet implemented")
    }

    override fun getReader(p0: Any?, p1: String?): Reader? {
        TODO("Not yet implemented")
    }

    override fun closeTemplateSource(p0: Any?) {
        TODO("Not yet implemented")
    }
}
