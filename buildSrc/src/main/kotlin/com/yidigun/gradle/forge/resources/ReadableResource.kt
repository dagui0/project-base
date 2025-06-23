package com.yidigun.gradle.forge.resources

import java.io.Reader
import java.io.Serializable
import java.net.URI
import java.nio.charset.Charset

/**
 * Represents a readable resource within a resource set.
 *
 * This interface defines the basic properties and methods required to access and read a resource.
 * A resource is always associated with a [ReadableResourceSet], which provides its context.
 * Therefore, a resource cannot exist independently of its set.
 *
 * @see ReadableResourceSet
 */
interface ReadableResource: Serializable {

    /**
     * The absolute path or identifier of the resource.
     * (e.g., "/path/to/file.txt", "http://example.com/resource")
     */
    val absolutePath: String

    /**
     * The relative path of the resource from the root URI of its resource set.
     * @see ReadableResourceSet.rootUri
     */
    val relativePath: String

    /**
     * The absolute URI that uniquely identifies this resource.
     */
    val uri: URI

    /**
     * The [ReadableResourceSet] that this resource belongs to.
     */
    val resourceSet: ReadableResourceSet

    /**
     * The character set to use when reading this resource.
     * Defaults to the charset of the parent [resourceSet].
     */
    val charset: Charset
        get() = resourceSet.defaultCharset

    /**
     * Opens the resource and returns a [Reader] for its content.
     *
     * The caller is responsible for closing the returned reader, ideally using a `use` block.
     * This method should return a raw reader for the resource, not necessarily a [java.io.BufferedReader].
     *
     * @return A new [Reader] for the resource's content.
     * @throws ResourceNotFoundException if the resource cannot be found (e.g., 404 Not Found).
     * @throws ResourceAccessDeniedException if access to the resource is denied (e.g., 403 Forbidden).
     * @throws ResourceAccessException if the resource cannot be accessed or read (e.g., an I/O Error).
     */
    fun openReader(): Reader
}
