package com.yidigun.gradle.forge.resources

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import java.io.File
import java.io.Reader
import java.net.URI
import java.nio.charset.Charset

internal abstract class FilesystemResourceSet(
    project: Project,
    protected val rootDir: File,
    defaultCharset: Charset = Charsets.UTF_8
): ReadableResourceSet(project, defaultCharset) {

    override val rootUri: URI by lazy {
        rootDir.toURI()
    }

    fun getFromFile(file: File): ReadableResource {
        val relativePath = file.relativeTo(rootDir).path
        return get(relativePath)
    }

    internal class FileResource(
        override val resourceSet: FilesystemResourceSet,
        override val relativePath: String,
    ): ReadableResource {

        val file: File by lazy {
            File(resourceSet.rootDir, relativePath)
        }

        override val absolutePath: String
            get() = file.absolutePath

        override val uri: URI
            get() = file.toURI()

        override fun openReader(): Reader {
            try {
                if (!file.exists())
                    throw ResourceNotFoundException("Resource not found: $absolutePath")
                return file.reader(charset)
            } catch (e: AccessDeniedException) {
                throw ResourceAccessDeniedException("Access denied to resources: $absolutePath", e)
            } catch (e: Exception) {
                throw ResourceAccessException("Failed to read resources: $absolutePath", e)
            }
        }
    }

    internal abstract class FilesystemIncrementalBuildSupport(
        override val resourceSet: FilesystemResourceSet
    ): IncrementalBuildSupport(resourceSet) {

        /**
         * Indicates whether changes to resources in this set are fully tracked.
         *
         * Always `true` for file system-based [ReadableResourceSet].
         */
        @get:Input
        override val fullyTrackable: Provider<Boolean> = resourceSet.project.provider { false }

        override fun changedIterator(changes: Iterable<FileChange>): Iterator<ReadableResource> {
            return changes
                .filter { it.changeType != ChangeType.REMOVED }
                .map { it.file }
                .map { resourceSet.getFromFile(it) }
                .iterator()
        }
    }
}
