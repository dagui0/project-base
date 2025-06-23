package com.yidigun.gradle.forge.utils

import java.nio.charset.Charset
import java.nio.charset.IllegalCharsetNameException
import java.nio.charset.UnsupportedCharsetException
import javax.lang.model.SourceVersion

object StringUtils {

    private val KOTLIN_HARD_KEYWORDS = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
        "if", "in", "interface", "is", "null", "object", "package", "return",
        "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
        "var", "when", "while"
    )
    private val multipleDotsRegex = Regex("\\.{2,}")
    private val invalidCharactersRegex = Regex("[^A-Za-z0-9_]+")

    fun defaultPackageName(projectGroup: String, projectName: String): String {
        return sanitizePackageName("$projectGroup.${projectName.replace('.', '_')}")
    }

    fun sanitizeJavaClassName(name: String): String {
        return sanitizeClassName(name) { SourceVersion.isKeyword(it) }
    }

    fun sanitizeKotlinClassName(name: String): String {
        return sanitizeClassName(name) { KOTLIN_HARD_KEYWORDS.contains(it) }
    }

    fun sanitizePackageName(
        name: String,
        defaultName: String = "project",
        isKeyword: (String) -> Boolean = { SourceVersion.isKeyword(it) || KOTLIN_HARD_KEYWORDS.contains(it) }
    ): String {
        return if (name.isEmpty())
            defaultName
        else
            name
                .replace(multipleDotsRegex, ".")
                .trim('.')
                .split('.')
                .filterNot { part -> isKeyword(part) }
                .map { part ->
                    sanitizeIdentifier(
                        name = part,
                        forceLowercase = true,
                        defaultName = "",
                        isKeyword = isKeyword,
                        // if package part is keyword, remove it
                        keywordEscaper = { "" }
                    )
                }
                .filterNot { it.isEmpty() }
                .joinToString(".")
                .ifEmpty { defaultName }
    }

    fun sanitizeClassName(
        name: String,
        defaultName: String = "ProjectInfo",
        isKeyword: (String) -> Boolean = { SourceVersion.isKeyword(it) || KOTLIN_HARD_KEYWORDS.contains(it) }
    ): String {
        return if (name.isEmpty())
            defaultName
        else
            sanitizeIdentifier(
                name = name.substringAfterLast('.'),
                forceLowercase = false,
                defaultName = defaultName,
                isKeyword = isKeyword,
                // if class name is keyword, prepend '_'
                keywordEscaper = { "_$it" }
            )
    }

    private fun sanitizeIdentifier(
        name: String,
        forceLowercase: Boolean,
        defaultName: String,
        isKeyword: (String) -> Boolean,
        keywordEscaper: (String) -> String
    ): String {
        val sanitized = name
            .let { if (forceLowercase) it.lowercase() else it }
            .replace('-', '_')
            .replace(invalidCharactersRegex, "_") // replace to single underscore
            .trimEnd('_')   // preserve only heading underscore

        return if (sanitized.isEmpty())
            defaultName
        else if (isKeyword(sanitized))
            keywordEscaper(sanitized)
        else
            // escape first digit
            if (sanitized.first().isDigit()) "_$sanitized" else sanitized
    }

    private val contentTypeCharsetRegex = Regex(
        "charset\\s*=\\s*([\"'])?([^;\"']*?)",
        RegexOption.IGNORE_CASE)

    fun charsetFromContentType(contentType: String?): Charset? {
        if (contentType.isNullOrBlank())
            return null

        val matches = contentTypeCharsetRegex.find(contentType)
        return try {
            matches?.groups?.get(2)?.value?.trim()?.let { Charset.forName(it) }
        } catch (e: IllegalCharsetNameException) {
            null
        } catch (e: UnsupportedCharsetException) {
            null
        }
    }
}

//
// Extensions
//

fun String.chomp(): String {
    return this.trimEnd('\n', '\r')
}

fun String.formatToString(format: String): String {
    return try {
        String.format(format, this)
    } catch (e: NumberFormatException) {
        this
    }
}

fun Number.formatToString(format: String): String {
    return try {
        String.format(format, this)
    } catch (e: IllegalArgumentException) {
        this.toString()
    }
}

fun String.removeAfter(ch: Char): String {
    val index = indexOf(ch)
    if (index != -1) {
        return substring(0, index)
    }
    return this
}
