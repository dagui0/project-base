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

internal class DirectoryResourceSet(
    project: Project,
    rootDir: File,
    private val includes: Set<String> = setOf("**/*"),
    private val excludes: Set<String> = emptySet(),
    defaultCharset: Charset = Charsets.UTF_8
): FilesystemResourceSet(project, rootDir, defaultCharset) {

    internal val fileTree: FileCollection by lazy {
        project.fileTree(rootDir) {
            include(includes)
            exclude(excludes)
        }
    }

    override fun get(path: String): ReadableResource {
        if (!matchPatterns(path)) {
            throw ResourceNotFoundException("Resource not matched to includes/excludes patterns: $path")
        }
        val file = rootDir.resolve(path)
        if (!file.exists())
            throw ResourceNotFoundException("Resource not found: $path")
        else if (!file.isFile)
            throw ResourceNotFoundException("Resource not a file: $path")

        return FileResource(this, path);
    }

    private fun matchPatterns(path: String): Boolean {
        if (excludes.any { path.matches(globToRegex(it)) })
            return false

        // If no includes are specified, all files are included
        if (includes.isEmpty() || includes.contains("**/*")) {
            return true
        }

        return includes.any { path.matches(globToRegex(it)) }
    }

    private val globPatternCache: MutableMap<String, Regex> = mutableMapOf()

    private fun globToRegex(glob: String): Regex {
        // Convert glob pattern to regex
        return globPatternCache.getOrPut(glob) {
            val regex = buildString {
                append('^')
                var i = 0
                while (i < glob.length) {
                    when (val c = glob[i]) {
                        '*' -> {
                            if (i + 1 < glob.length && glob[i + 1] == '*') {
                                append(".*")
                                i++ // consume the second '*'
                            } else {
                                append("[^/]*")
                            }
                        }
                        '?' -> append('.')
                        // Escape regex special characters
                        in ".+()[]{}|^$\\" -> append('\\').append(c)
                        else -> append(c)
                    }
                    i++
                }
                append('$')
            }
            Regex(regex, RegexOption.CANON_EQ)
        }
    }

    override fun iterator(): Iterator<ReadableResource> {

        return fileTree.files.asSequence()
            .map { file ->
                FileResource(this, file.relativeTo(rootDir).path)
            }.iterator()
    }

    @get:Nested
    override val buildSupport: IncrementalBuildSupport = DirectoryIncrementalBuildSupport(this)

    internal class DirectoryIncrementalBuildSupport(
        override val resourceSet: DirectoryResourceSet
    ): FilesystemIncrementalBuildSupport(resourceSet
    ) {

        @get:Optional
        @get:InputFiles
        @get:PathSensitive(PathSensitivity.RELATIVE)
        override val sourceFiles: FileCollection = resourceSet.fileTree
    }
}
