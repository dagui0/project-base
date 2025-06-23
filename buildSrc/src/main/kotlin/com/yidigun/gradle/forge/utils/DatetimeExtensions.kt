package com.yidigun.gradle.forge.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

private val dateTimeFormatterCache = mutableMapOf<Pair<ZoneId, String>, DateTimeFormatter>()

private fun dateTimeFormatter(format: String, zone: ZoneId): DateTimeFormatter {
    return dateTimeFormatterCache.getOrPut(zone to format) {
        DateTimeFormatter.ofPattern(format).withZone(zone)
    }
}

private fun dateTimeFormatter(format: String): DateTimeFormatter {
    return dateTimeFormatter(format, ZoneId.systemDefault())
}

fun Date.formatDate(format: String): String {
    return this.toInstant().atZone(ZoneId.systemDefault()).formatDate(format)
}

fun Instant.formatDate(format: String): String {
    return this.atZone(ZoneId.of("UTC"))
        .format(dateTimeFormatter(format, ZoneId.of("UTC")))
}

fun LocalDateTime.formatDate(format: String): String {
    return this.format(dateTimeFormatter(format))
}

fun ZonedDateTime.formatDate(format: String): String {
    return this.format(dateTimeFormatter(format, this.zone))
}

fun LocalDate.formatDate(format: String): String {
    return this.format(dateTimeFormatter(format))
}

fun LocalTime.formatDate(format: String): String {
    return this.atDate(LocalDate.now()).formatDate(format)
}
