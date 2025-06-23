package com.yidigun.gradle.forge.utils

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path

//
// String Reader
//

fun String.openReader(): Reader = StringReader(this)

//
// File Reader
//

fun File.openReader(): Reader = this.openReader(Charsets.UTF_8)
fun File.openReader(charset: Charset): Reader = this.reader(charset)
fun File.openReader(charset: String): Reader = this.reader(Charset.forName(charset))

fun Path.openReader(): Reader = this.toFile().openReader(Charsets.UTF_8)
fun Path.openReader(charset: Charset): Reader = this.toFile().openReader(charset)
fun Path.openReader(charset: String): Reader = this.toFile().openReader(Charset.forName(charset))

//
// URL Reader
//

fun URL.openReader(): Reader {
    val conn = openConnection()
    val contentType = conn.contentType
    val charset = StringUtils.charsetFromContentType(contentType)?: Charsets.UTF_8
    return InputStreamReader(conn.getInputStream(), charset)
}
fun URL.openReader(charset: Charset): Reader {
    return InputStreamReader(openConnection().getInputStream(), charset)
}
fun URL.openReader(charset: String): Reader = this.openReader(Charset.forName(charset))

fun URI.openReader(): Reader = this.toURL().openReader()
fun URI.openReader(charset: Charset): Reader = this.toURL().openReader(charset)
fun URI.openReader(charset: String): Reader = this.toURL().openReader(Charset.forName(charset))

//
// BufferedReader
//
private val lineSeparator: String = System.lineSeparator() ?: "\n"

fun BufferedReader.readLineWithNewline(): String? {

    val lineBuilder = StringBuilder()
    var char: Int

    while (this.read().also { char = it } != -1) {
        val currentChar = char.toChar()

        // Check for newline characters
        if (currentChar == '\n')
            return lineBuilder.append(lineSeparator).toString()

        // if not '\n' after '\r',
        // treat '\r' as a newline and reset to after \r
        if (currentChar == '\r') {
            this.mark(1)
            val nextChar = this.read()

            // if EOF after '\r', treat as line ended with newline
            if (nextChar == -1)
                return lineBuilder.append(lineSeparator).toString()

            // if '\n' after '\r', line ended rightfully
            if (nextChar == '\n'.code)
                return lineBuilder.append(lineSeparator).toString()

            // else other character after '\r', it's start of a new line
            this.reset()
        }

        // otherwise, continue reading the line
        lineBuilder.append(currentChar)
    }

    return lineBuilder.takeIf { it.isNotEmpty() }?.toString()
}

//
// URL to File conversion
//

fun URL.toPhysicalFile(): File? {
    return when (this.protocol) {
        "file" -> {
            try {
                File(this.toURI())
            } catch (e: Exception) {
                File(this.path)
            }
        }
        "jar" -> {
            val conn = this.openConnection() as? java.net.JarURLConnection
            conn?.jarFileURL?.toURI()?.let {
                try {
                    File(it)
                } catch (e: Exception) {
                    File(it.path)
                }
            }
        }
        else -> null
    }
}
