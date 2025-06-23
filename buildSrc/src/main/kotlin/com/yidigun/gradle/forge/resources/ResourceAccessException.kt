package com.yidigun.gradle.forge.resources

/**
 * Thrown for general I/O errors that occur when accessing a resource.
 *
 * This exception indicates an issue during an input/output operation,
 * such as a disk error or a network connectivity problem. It serves as a catch-all
 * for I/O problems not covered by more specific exceptions like [ResourceNotFoundException].
 *
 * @param message The detail message of the exception.
 * @param cause The cause of the exception, if any.
 */
class ResourceAccessException(message: String, cause: Throwable? = null) : ResourceException(message, cause)
