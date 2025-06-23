package com.yidigun.gradle.forge.resources

import com.yidigun.gradle.forge.utils.StringUtils
import com.yidigun.gradle.forge.utils.toPhysicalFile
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.charset.Charset

internal class UriResourceSet(
    project: Project,
    override val rootUri: URI,
    private val relativePaths: Set<String>,
): ReadableResourceSet(project) {

    override fun get(path: String): ReadableResource {
        // skip validation for network resources
        return UriResource(this, path)
    }

    override fun iterator(): Iterator<ReadableResource> {
        return relativePaths.map { path ->
            UriResource(this, path)
        }.iterator()
    }

    internal class UriResource(
        override val resourceSet: UriResourceSet,
        override val relativePath: String
    ): ReadableResource {

        private var detectedCharset: Charset? = null

        override val charset: Charset
            get() = detectedCharset ?: resourceSet.defaultCharset

        override val uri: URI by lazy {
            resourceSet.rootUri.resolve(relativePath)
        }

        private val url: URL by lazy {
            uri.toURL()
        }

        override val absolutePath: String
            get() = uri.toString()

        override fun openReader(): Reader {
            try {
                val conn = url.openConnection()

                // now connect to URL
                if (conn is HttpURLConnection) { // check http status
                    if (conn.responseCode == 404)
                        throw ResourceNotFoundException("Resource not found: $absolutePath")
                    else if (conn.responseCode == 401 || conn.responseCode == 403)
                        throw ResourceAccessDeniedException("Access denied to resources: $absolutePath")
                    else if (conn.responseCode >= 400)
                        throw ResourceAccessException("Failed to access resources: $absolutePath, HTTP status: ${conn.responseCode}")

                    val contentType = conn.contentType
                    if (contentType.isNotBlank())
                        detectedCharset = StringUtils.charsetFromContentType(contentType)
                }

                return url.openStream().reader(charset)
            } catch (e: Exception) {
                throw ResourceAccessException("Failed to read resources: $absolutePath", e)
            }
        }
    }

    @get:Nested
    override val buildSupport: IncrementalBuildSupport = object: UriToFileIncrementalBuildSupport(this) {

        @get:Optional
        @get:InputFiles
        @get:PathSensitive(PathSensitivity.RELATIVE)
        override val sourceFiles: FileCollection =
            project.objects.fileCollection().from(physicalFileMapProvider.map { it.keys })
    }
}
