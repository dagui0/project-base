package com.yidigun.gradle.forge.resources

/**
 * Thrown when a requested resource is not found.
 *
 * This can occur when a file does not exist at the specified path,
 * or when a web resource is unavailable.
 * (e.g., [java.io.FileNotFoundException], `404 Not Found`).
 *
 * @param message The detail message of the exception.
 * @param cause The cause of the exception, if any.
 */
class ResourceNotFoundException(message: String, cause: Throwable? = null): ResourceException(message, cause)
