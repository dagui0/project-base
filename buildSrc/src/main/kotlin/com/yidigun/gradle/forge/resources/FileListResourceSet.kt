package com.yidigun.gradle.forge.resources

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File
import java.nio.charset.Charset

internal class FileListResourceSet(
    project: Project,
    rootDir: File,
    private val relativePaths: Set<String>,
    defaultCharset: Charset = Charsets.UTF_8
): FilesystemResourceSet(project, rootDir, defaultCharset) {

    override fun get(path: String): ReadableResource {
        return if (relativePaths.contains(path)) {
            FileResource(this, path)
        } else {
            throw ResourceNotFoundException("Resource not found: $path")
        }
    }

    override fun iterator(): Iterator<ReadableResource> {
        return relativePaths.map { path ->
            FileResource(this, path)
        }.iterator()
    }

    @get:Nested
    override val buildSupport: IncrementalBuildSupport = FileListIncrementalBuildSupport(this)

    internal class FileListIncrementalBuildSupport(
        override val resourceSet: FileListResourceSet
    ): FilesystemIncrementalBuildSupport(resourceSet
    ) {

        @get:Optional
        @get:InputFiles
        @get:PathSensitive(PathSensitivity.RELATIVE)
        override val sourceFiles: FileCollection = resourceSet.project.files(
            resourceSet.project.provider {
                resourceSet.relativePaths.map { resourceSet.rootDir.resolve(it) }
            }
        )
    }
}
