package com.yidigun.gradle.forge.resources

import org.gradle.api.Project
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path

/**
 * Factory for creating various types of [ReadableResourceSet]s.
 * Provides methods to create resource sets from strings, directories, files, URIs, and classpath resources.
 */
object ResourceSets {

    //
    // StringResourceSet
    //

    /**
     * Creates a [ReadableResourceSet] from a single string data.
     * The data will be stored under the 'index' key.
     * @param project the Gradle project
     * @param data the string data to be stored
     * @return a [ReadableResourceSet] containing the string data
     */
    fun ofString(project: Project, data: String): ReadableResourceSet = ofString(project, mapOf("index" to data))
    fun ofString(project: Project, data: String, relativePath: String): ReadableResourceSet {
        return ofString(project, mapOf(relativePath to data))
    }

    /**
     * Creates a [ReadableResourceSet] from a map of string data.
     * Each entry in the map will be stored as a separate resource.
     * @param project the Gradle project
     * @param templateMap a map where keys are relative paths and values are string data
     * @return a [ReadableResourceSet] containing the string data
     */
    fun ofString(project: Project, templateMap: Map<String, String>): ReadableResourceSet {
        return StringResourceSet(project, templateMap)
    }

    //
    // DirectoryResourceSet
    //

    /**
     * Creates a [ReadableResourceSet] from a directory.
     * The directory will be scanned for files matching the specified includes and excludes patterns.
     * @param project the Gradle project
     * @param rootDir the root directory to scan
     * @param includes a set of include patterns (default is all files)
     * @param excludes a set of exclude patterns (default is empty)
     * @param charset the character set to use when reading files (default is UTF-8)
     * @return a [ReadableResourceSet] containing the files from the directory
     */
    fun ofDir(
        project: Project, rootDir: File,
        includes: Set<String> = setOf("**/*"), excludes: Set<String> = emptySet(),
        charset: Charset = Charsets.UTF_8
    ): ReadableResourceSet {
        return DirectoryResourceSet(project, rootDir, includes, excludes, charset)
    }

    /**
     * Creates a [ReadableResourceSet] from a directory specified by its path.
     * The directory will be scanned for files matching the specified includes and excludes patterns.
     * @param project the Gradle project
     * @param rootDirPath the path to the root directory
     * @param includes a set of include patterns (default is all files)
     * @param excludes a set of exclude patterns (default is empty)
     * @param charset the character set to use when reading files (default is UTF-8)
     * @return a [ReadableResourceSet] containing the files from the directory
     */
    fun ofDir(
        project: Project, rootDirPath: String,
        includes: Set<String> = setOf("**/*"), excludes: Set<String> = emptySet(),
        charset: Charset = Charsets.UTF_8
    ): ReadableResourceSet {
        return DirectoryResourceSet(
            project, project.layout.projectDirectory.file(rootDirPath).asFile,
            includes, excludes, charset
        )
    }

    //
    // FileListResourceSet
    //

    /**
     * Creates a [ReadableResourceSet] from a list of files.
     * All the files must be relative to the specified root directory.
     * @param project the Gradle project
     * @param rootPath the root directory containing the files
     * @param relativePaths a list of relative paths to the files
     * @return a [ReadableResourceSet] containing the specified files
     * @throws IllegalArgumentException if any file specification is not valid or not relative to the root directory
     */
    fun ofFile(project: Project, rootPath: String, vararg relativePaths: String): ReadableResourceSet {
        return ofFile(project, File(rootPath), relativePaths.toList())
    }

    /**
     * Creates a [ReadableResourceSet] from a list of files.
     * All the files must be relative to the specified root directory.
     * @param project the Gradle project
     * @param rootPath the root directory containing the files
     * @param files a list of files to include in the resource set
     * @return a [ReadableResourceSet] containing the specified files
     * @throws IllegalArgumentException if any file specification is not valid or not relative to the root directory
     */
    fun ofFile(project: Project, rootPath: String, vararg files: File): ReadableResourceSet {
        return ofFile(project, File(rootPath), files.toList())
    }

    /**
     * Creates a [ReadableResourceSet] from a list of files.
     * All the files must be relative to the specified root directory.
     * @param project the Gradle project
     * @param rootPath the root directory containing the files
     * @param fileSpecs a collection of file specifications (File, String, or Path)
     * @return a [ReadableResourceSet] containing the specified files
     * @throws IllegalArgumentException if any file specification is not valid or not relative to the root directory
     */
    fun ofFile(project: Project, rootPath: String, fileSpecs: Collection<*>): ReadableResourceSet {
        return ofFile(project, File(rootPath), fileSpecs)
    }

    /**
     * Creates a [ReadableResourceSet] from a list of files.
     * All the files must be relative to the specified root directory.
     * @param project the Gradle project
     * @param rootDir the root directory containing the files
     * @param relativePaths a list of relative paths to the files
     * @return a [ReadableResourceSet] containing the specified files
     * @throws IllegalArgumentException if any file specification is not valid or not relative to the root directory
     */
    fun ofFile(project: Project, rootDir: File, vararg relativePaths: String): ReadableResourceSet {
        return ofFile(project, rootDir, relativePaths.toList())
    }

    /**
     * Creates a [ReadableResourceSet] from a list of files.
     * All the files must be relative to the specified root directory.
     * @param project the Gradle project
     * @param rootDir the root directory containing the files
     * @param files a list of files to include in the resource set
     * @return a [ReadableResourceSet] containing the specified files
     * @throws IllegalArgumentException if any file specification is not valid or not relative to the root directory
     */
    fun ofFile(project: Project, rootDir: File, vararg files: File): ReadableResourceSet {
        return ofFile(project, rootDir, files.toList())
    }

    /**
     * Creates a [ReadableResourceSet] from a list of files.
     * All the files must be relative to the specified root directory.
     * @param project the Gradle project
     * @param rootDir the root directory containing the files
     * @param fileSpecs a collection of file specifications (File, String, or Path)
     * @param charset the character set to use when reading files (default is UTF-8)
     * @return a [ReadableResourceSet] containing the specified files
     * @throws IllegalArgumentException if any file specification is not valid or not relative to the root directory
     */
    fun ofFile(project: Project, rootDir: File, fileSpecs: Collection<*>, charset: Charset = Charsets.UTF_8): ReadableResourceSet {
        val relativePaths = fileSpecs.map { spec ->
            if (spec == null)
                throw IllegalArgumentException("FileSpec cannot be null")
            val file = when (spec) {
                is File -> spec
                is String -> if (!spec.isEmpty())
                    rootDir.resolve(spec)
                else
                    throw IllegalArgumentException("File path cannot be empty")
                is Path -> spec.toFile()
                else -> throw IllegalArgumentException("Unsupported type: ${spec.javaClass.name}")
            }

            require(file.absolutePath.startsWith(rootDir.absolutePath)) {
                "File ${file.absolutePath} is not relative to root directory ${rootDir.absolutePath}"
            }
            file.relativeTo(rootDir).path
        }.toSet()
        return FileListResourceSet(project, rootDir, relativePaths, charset)
    }

    //
    // UriResourceSet
    //

    /**
     * Creates a [ReadableResourceSet] from a single URI.
     * The URI will be resolved to its root and relative paths will be extracted.
     * @param project the Gradle project
     * @param uri the URI to create the resource set from
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, uri: String): ReadableResourceSet = ofUri(project, URI.create(uri))

    /**
     * Creates a [ReadableResourceSet] from a single URI.
     * The URI will be resolved to its root and relative paths will be extracted.
     * @param project the Gradle project
     * @param uri the URI to create the resource set from
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, uri: URL): ReadableResourceSet = ofUri(project, uri.toURI())

    /**
     * Creates a [ReadableResourceSet] from a single URI.
     * The URI will be resolved to its root and relative paths will be extracted.
     * @param project the Gradle project
     * @param uri the URI to create the resource set from
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, uri: URI): ReadableResourceSet {
        val rootUri = uri.resolve("/")
        val relativePath = rootUri.relativize(uri).path
        return ofUri(project, rootUri, setOf(relativePath))
    }

    /**
     * Creates a [ReadableResourceSet] from a URI and a list of relative paths.
     * All the relative paths must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param relativePaths a list of relative paths to the resources
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: String, vararg relativePaths: String): ReadableResourceSet {
        return ofUri(project, URI.create(rootUri), relativePaths.toList())
    }

    /**
     * Creates a [ReadableResourceSet] from a URI and a list of URIs.
     * All the URIs must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param uris a list of URIs to the resources
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: String, uriSpecs: Collection<*>): ReadableResourceSet {
        return ofUri(project, URI.create(rootUri), uriSpecs)
    }

    /**
     * Creates a [ReadableResourceSet] from a URI and a list of relative paths.
     * All the relative paths must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param relativePaths a list of relative paths to the resources
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: URL, vararg relativePaths: String): ReadableResourceSet {
        return ofUri(project, rootUri.toURI(), relativePaths.toList())
    }

    /**
     * Creates a [ReadableResourceSet] from a URI and a list of URIs.
     * All the URIs must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param uris a list of URIs to the resources
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: URL, vararg uris: URL): ReadableResourceSet {
        return ofUri(project, rootUri.toURI(), uris.toList())
    }

    /**
     * Creates a [ReadableResourceSet] from a URI and a collection of relative paths or URIs.
     * All the relative paths or URIs must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param uriSpecs a collection of relative paths or URIs to the resources
     * @return a [ReadableResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: URL, uriSpecs: Collection<*>): ReadableResourceSet {
        return ofUri(project, rootUri.toURI(), uriSpecs)
    }

    /**
     * Creates a [UriResourceSet] from a root URI and a list of relative paths.
     * All the relative paths must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param relativePaths a list of relative paths to the resources
     * @return a [UriResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: URI, vararg relativePaths: String): ReadableResourceSet {
        return ofUri(project, rootUri, relativePaths.toList())
    }

    /**
     * Creates a [UriResourceSet] from a root URI and a list of URIs.
     * All the URIs must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param uriSpecs a list of URIs to the resources
     * @return a [UriResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: URI, vararg uriSpecs: URI): ReadableResourceSet {
        return ofUri(project, rootUri, uriSpecs.toList())
    }

    /**
     * Creates a [UriResourceSet] from a root URI and a collection of relative paths.
     * All the relative paths must be relative to the root URI.
     * @param project the Gradle project
     * @param rootUri the root URI to resolve relative paths against
     * @param uriSpecs a collection of relative paths or URIs to the resources
     * @return a [UriResourceSet] containing the resources from the URI
     * @throws IllegalArgumentException if any URI specification is not valid or not relative to the root URI
     */
    fun ofUri(project: Project, rootUri: URI, uriSpecs: Collection<*>): ReadableResourceSet {
        val rootPath = rootUri.toString().let { if (it.endsWith('/')) it else "$it/" }
        val finalRootUri = URI.create(rootPath)
        val relativePaths = uriSpecs.map { spec ->
            if (spec == null)
                throw IllegalArgumentException("URI cannot be null")
            val uri = when (spec) {
                is URI -> spec
                is URL -> spec.toURI()
                is String -> if (!spec.isEmpty())
                    rootUri.resolve(spec)
                else
                    throw IllegalArgumentException("URI cannot be empty")
                else -> throw IllegalArgumentException("Unsupported type: ${spec.javaClass.name}")
            }
            uri.let {
                val absolutePath = it.toString()
                require(absolutePath.startsWith(rootPath)) {
                    "URI $absolutePath is not relative to root uri $rootPath"
                }
                absolutePath.removePrefix(rootPath)
            }
        }.toSet()
        return UriResourceSet(project, finalRootUri, relativePaths)
    }

    //
    // ClasspathResourceSet
    //

    /**
     * Creates a [ReadableResourceSet] from the classpath of the given project.
     * The resources will be loaded from the classpath using the specified relative paths.
     * @param project the Gradle project
     * @param relativePaths a list of relative paths to the resources in the classpath
     * @return a [ReadableResourceSet] containing the resources from the classpath
     */
    fun ofClasspath(project: Project, vararg relativePaths: String): ReadableResourceSet
        = ofClasspath(project, relativePaths.toList(), Charsets.UTF_8)

    /**
     * Creates a [ReadableResourceSet] from the classpath of the given project.
     * The resources will be loaded from the classpath using the specified relative paths.
     * @param project the Gradle project
     * @param relativePaths a collection of relative paths to the resources in the classpath
     * @param charset the character set to use when reading files (default is UTF-8)
     * @return a [ReadableResourceSet] containing the resources from the classpath
     */
    fun ofClasspath(
        project: Project,
        relativePaths: Collection<String>,
        charset: Charset = Charsets.UTF_8
    ): ReadableResourceSet {
        return ClasspathResourceSet(project,relativePaths.toSet(), charset)
    }
}
