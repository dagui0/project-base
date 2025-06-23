package com.yidigun.gradle.forge.resources

import com.yidigun.gradle.forge.ForgeException

/**
 * A base exception for resource-related errors.
 *
 * This exception is thrown when an issue occurs related to a resource,
 * such as access denial, resource not found, or other I/O-related issues.
 * It serves as a base class for more specific resource exceptions.
 *
 * ```
 * -----------------------
 * <<abstract, exception>>
 * ResourceException
 * -----------------------
 *    ^
 *    |
 *    |     -------------------------
 *    |     <<exception>>
 *    +---  ResourceNotFoundException
 *    |     -------------------------
 *    |
 *    |     -----------------------------
 *    |     <<exception>>
 *    +---  ResourceAccessDeniedException
 *    |     -----------------------------
 *    |
 *    |     -----------------------
 *    |     <<exception>>
 *    +---  ResourceAccessException
 *          -----------------------
 * ```
 *
 * @param message The detail message of the exception.
 * @param cause The cause of the exception, if any.
 */
abstract class ResourceException(message: String, cause: Throwable? = null): ForgeException(message, cause)
