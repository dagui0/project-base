package com.yidigun.gradle.forge.resources

/**
 * Thrown when access to a resource is denied.
 *
 * This can occur when the user does not have permission to read the resource,
 * or when the resource is protected by security settings.
 * (e.g., [java.nio.file.AccessDeniedException], `403 Forbidden`)
 *
 * @param message The detail message of the exception.
 * @param cause The cause of the exception, if any.
 */
class ResourceAccessDeniedException(message: String, cause: Throwable? = null): ResourceException(message, cause)
