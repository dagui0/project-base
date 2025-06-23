package com.yidigun.gradle.forge.templates.simple

import com.yidigun.gradle.forge.templates.TemplateContext
import com.yidigun.gradle.forge.templates.TemplateLanguage
import com.yidigun.gradle.forge.templates.SupportedTemplateLanguage
import com.yidigun.gradle.forge.templates.TemplateProcessor
import com.yidigun.gradle.forge.utils.formatDate
import com.yidigun.gradle.forge.utils.formatToString
import com.yidigun.gradle.forge.utils.readLineWithNewline
import java.io.Reader
import java.io.Writer

//
// SimpleTemplateProcessor
//

internal class SimpleTemplateProcessor(
    val commentPrefix: String = DEFAULT_COMMENT_PREFIX
): TemplateProcessor {

    companion object {
        const val DEFAULT_COMMENT_PREFIX: String = "#"
        val DEFAULT_INSTANCE: TemplateProcessor =
            SimpleTemplateProcessor(DEFAULT_COMMENT_PREFIX)
    }

    override fun language(): TemplateLanguage {
        return SupportedTemplateLanguage.SIMPLE
    }

    override fun process(
        context: TemplateContext,
        template: Reader,
        output: Writer
    ) {
        try {
            val reader = template.buffered()
            var line: String? = reader.readLineWithNewline()
            while (line != null) {
                if (line.trim().startsWith(commentPrefix)) {
                    line = reader.readLineWithNewline()
                    continue
                }
                output.write(replaceVariables(line, context.model))
                line = reader.readLineWithNewline()
            }
        }
        finally {
            // Ensure the output is flushed even if an exception occurs
            output.flush()
        }
    }

    /// 문자열에서 `${variable[:format]}` 형태의 변수를 찾아서 모델에 있는 값으로 대체한다.
    private fun replaceVariables(string: String, model: Map<String, Any>): String {

        if (model.isEmpty()) return string

        return buildString(string.length) {
            var lastIndex = 0
            var currentIndex = findNextVariable(string, lastIndex)

            while (currentIndex != -1) {
                append(string, lastIndex, currentIndex) // 이전 부분 추가

                val endIndex = string.indexOf('}', currentIndex)
                if (endIndex != -1) {
                    val key = string.substring(currentIndex + 2, endIndex)
                    val value = findReplacement(key, model)?.toString() ?: string.substring(currentIndex, endIndex + 1)
                    append(value)
                    lastIndex = endIndex + 1
                } else {
                    // 짝이 맞는 '}'가 없으면 원본 그대로 추가
                    append(string, currentIndex, currentIndex + 2)
                    lastIndex = currentIndex + 2
                }
                currentIndex = findNextVariable(string, lastIndex)
            }
            append(string, lastIndex, string.length) // 나머지 부분 추가
        }
    }

    /// `variable[:format]` 형태의 변수를 치환할 값을 생성한다.
    private fun findReplacement(variable: String, model: Map<String, Any>): String? {
        val variableName = variable.substringBefore(':')
        val format = variable.substringAfter(':', "")
        if (format.isNotEmpty()) {
            return model[variableName]?.let { value ->
                when (value) {
                    is Number -> value.formatToString(format)
                    is String -> value.formatToString(format)
                    is java.util.Date -> value.formatDate(format)
                    is java.time.Instant -> value.formatDate(format)
                    is java.time.LocalDate -> value.formatDate(format)
                    is java.time.LocalDateTime -> value.formatDate(format)
                    is java.time.ZonedDateTime -> value.formatDate(format)
                    is java.time.LocalTime -> value.formatDate(format)
                    else -> value.toString()
                }
            }
        }
        return model[variableName]?.toString()
    }

    /// `${variable}` 형태의 변수 위치를 찾는다.
    /// 만약 `\${not_a_variable}` 형태로 이스케이프되었다면, 다음 위치를 찾는다.
    private fun findNextVariable(string: String, startIndex: Int): Int {
        var index = string.indexOf("\${", startIndex)
        while (index != -1) {
            if (index > 0 && string[index - 1] == '\\') {
                // \${not_a_variable} escaping
                index = string.indexOf("\${", index + 2)
            } else {
                return index
            }
        }
        return -1
    }
}
