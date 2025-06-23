package com.yidigun.gradle.forge.resources

import org.gradle.api.Project
import java.net.URI

internal class StringResourceSet(
    project: Project,
    private val templateMap: Map<String, String>,
): ReadableResourceSet(project) {

    override fun get(path: String): ReadableResource {
        if (templateMap.containsKey(path))
            return StringResource(this, path, templateMap[path]!!)
        else
            throw ResourceNotFoundException("Resource not found: $path in StringResourceSet")
    }

    override val rootUri: URI
        get() = URI.create("string:/")

    override fun iterator(): Iterator<ReadableResource> {
        return templateMap.map { (key, value) ->
            StringResource(this, key, value)
        }.iterator()
    }

    internal class StringResource(
        override val resourceSet: StringResourceSet,
        override val relativePath: String,
        private val data: String
    ): ReadableResource {

        override val absolutePath: String
            get() = resourceSet.rootUri.toString()

        override val uri: URI
            get() = resourceSet.rootUri.resolve(relativePath)

        override fun openReader(): java.io.Reader {
            return data.reader()
        }
    }
}
