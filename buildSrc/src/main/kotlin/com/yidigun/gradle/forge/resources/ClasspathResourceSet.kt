package com.yidigun.gradle.forge.resources

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import java.io.Reader
import java.net.URI
import java.net.URL
import java.nio.charset.Charset

internal class ClasspathResourceSet(
    project: Project,
    private val relativePaths: Set<String>,
    override val defaultCharset: Charset = Charsets.UTF_8
): ReadableResourceSet(project) {

    private val classLoader: ClassLoader
        get() = Thread.currentThread().contextClassLoader

    private fun findResource(path: String): URL? {
        return classLoader.getResource(path)
            ?: classLoader.getResource(path.replace('\\', '/'))
    }

    override fun get(path: String): ReadableResource {
        return if (findResource(path) != null)
            ClasspathResource(this, path)
        else
            throw ResourceNotFoundException("Resource not found: $path")
    }

    override val rootUri: URI
        get() = URI.create("classpath://")

    override fun iterator(): Iterator<ReadableResource> {
        return relativePaths.map { get(it) }.iterator()
    }

    internal class ClasspathResource(
        override val resourceSet: ClasspathResourceSet,
        override val relativePath: String
    ): ReadableResource {

        override val uri: URI by lazy {
            resourceSet.rootUri.resolve(relativePath)
        }

        override val absolutePath: String
            get() = uri.toString()

        override fun openReader(): Reader {
            val inputStream = resourceSet.classLoader.getResourceAsStream(relativePath)
                ?: throw ResourceNotFoundException("Resource not found: $relativePath")
            try {
                return inputStream.reader(charset)
            } catch (e: Exception) {
                throw ResourceAccessException("Failed to read resources: $absolutePath", e)
            }
        }
    }

    @get:Nested
    override val buildSupport: IncrementalBuildSupport = object: UriToFileIncrementalBuildSupport(this) {

        /**
         * Indicates whether changes to resources in this set are fully tracked.
         *
         * For a [ClasspathResourceSet], this is always `true` because Gradle resolves all
         * classpath dependencies to local, physical files (JARs or class directories)
         * that can be reliably tracked.
         *
         * A theoretical edge case like a `jar:http://...` resource, which cannot be
         * tracked, is not a standard use case in a Gradle build environment.
         */
        @get:Input
        override val fullyTrackable: Provider<Boolean> = resourceSet.project.provider { true }

        @get:Optional
        @get:Classpath
        override val sourceClasspath: FileCollection =
            project.objects.fileCollection().from(physicalFileMapProvider.map { it.keys })
    }
}
