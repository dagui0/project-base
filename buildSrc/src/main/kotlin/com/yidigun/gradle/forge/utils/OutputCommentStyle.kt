package com.yidigun.gradle.forge.utils

enum class OutputCommentStyle(
    val suffixes: Set<String>
) {

    NONE(emptySet()) {
        override fun createCommentBlock(content: String): String {
            return content
        }
    },

    JAVA(setOf("java", "js", "kt", "kts", "cs", "cpp", "cxx", "c++", "gradle",
                        "php", "go", "rs", "scala", "swift", "ts", "tsx")) {
        override fun createCommentBlock(content: String): String {
            return buildString {
                prefixedLines(this, "// ", content)
            }
        }
    },

    SHELL(setOf("txt", "text", "sh", "bash", "rb", "csh", "pl", "pm", "perl", "zsh", "fish", "ksh",
                         "tcsh", "toml", "yaml", "yml")) {
        override fun createCommentBlock(content: String): String {
            return buildString {
                prefixedLines(this, "# ", content)
            }
        }
    },

    C_LANG(setOf("c", "h")) {
        override fun createCommentBlock(content: String): String {
            return createBlockComment(
                content = content,
                singleLineFormatter = { "/* $content */$LINE_SEPARATOR" },
                multiLineFormatter = {
                    buildString {
                        append("/*").append(LINE_SEPARATOR)
                        prefixedLines(this, " * ", content)
                        append(" */").append(LINE_SEPARATOR)
                    }
                }
            )
        }
    },

    SQL(setOf("sql", "plsql", "pgsql", "mysql", "sqlite", "hive", "db2", "mssql")) {
        override fun createCommentBlock(content: String): String {
            return buildString {
                prefixedLines(this, "-- ", content)
            }
        }
    },

    INI(setOf("ini", "properties")) {
        override fun createCommentBlock(content: String): String {
            return buildString {
                prefixedLines(this, "; ", content)
            }
        }
    },

    XML(setOf("xml", "html", "xhtml", "htm", "svg", "xsl", "xslt", "md", "markdown")) {
        override fun createCommentBlock(content: String): String {
            return createBlockComment(
                content = content,
                singleLineFormatter = { "<!-- $content -->$LINE_SEPARATOR" },
                multiLineFormatter = {
                    buildString {
                        append("<!--").append(LINE_SEPARATOR)
                        prefixedLines(this, "  -- ", content)
                        append("  -->").append(LINE_SEPARATOR)
                    }
                }
            )
        }
    },

    PYTHON(setOf("py", "pyi", "pyx", "pyo", "pyd")) {
        override fun createCommentBlock(content: String): String {
            return createBlockComment(
                content = content,
                singleLineFormatter = { "# $content$LINE_SEPARATOR" },
                multiLineFormatter = {
                    buildString {
                        append("\"\"\"").append(LINE_SEPARATOR)
                        append(content).append(LINE_SEPARATOR)
                        append("\"\"\"").append(LINE_SEPARATOR)
                    }
                }
            )
        }
    },

    BASIC(setOf("bas", "vb", "vba", "vbs")) {
        override fun createCommentBlock(content: String): String {
            return buildString {
                prefixedLines(this, "' ", content)
            }
        }
    },

    MATLAB(setOf("matlab", "m", "mlx")) {
        override fun createCommentBlock(content: String): String {
            return buildString {
                prefixedLines(this, "% ", content)
            }
        }
    },

    LUA(setOf("lua", "luac", "luau")) {
        override fun createCommentBlock(content: String): String {
            return createBlockComment(
                content = content,
                singleLineFormatter = { "-- $content$LINE_SEPARATOR" },
                multiLineFormatter = {
                    buildString {
                        append("--[[").append(LINE_SEPARATOR)
                        append(content).append(LINE_SEPARATOR)
                        append("]]").append(LINE_SEPARATOR)
                    }
                }
            )
        }
    },

    POWERSHELL(setOf("ps1", "psm1", "psd1")) {
        override fun createCommentBlock(content: String): String {
            return createBlockComment(
                content = content,
                singleLineFormatter = { "# $content$LINE_SEPARATOR" },
                multiLineFormatter = {
                    buildString {
                        append("<#").append(LINE_SEPARATOR)
                        prefixedLines(this, " # ", content)
                        append(" #>").append(LINE_SEPARATOR)
                    }
                }
            )
        }
    },

    BATCH(setOf("bat", "cmd")) {
        override fun createCommentBlock(content: String): String {
            return buildString {
                prefixedLines(this, "REM ", content)
            }
        }
    },

    JSP(setOf("jsp", "jspx", "jspf", "tag", "tagx", "aspx", "ascx")) {
        override fun createCommentBlock(content: String): String {
            return createBlockComment(
                content = content,
                singleLineFormatter = { "<%-- $content --%>$LINE_SEPARATOR" },
                multiLineFormatter = {
                    buildString {
                        append("<%--").append(LINE_SEPARATOR)
                        prefixedLines(this, "  -- ", content)
                        append("  --%>").append(LINE_SEPARATOR)
                    }
                }
            )
        }
    },
    ;

    companion object {
        val LINE_SEPARATOR: String = System.lineSeparator()?: "\n"

        val defaultSuffixMap: Map<String, OutputCommentStyle> = values().flatMap { style ->
            style.suffixes.map { suffix -> suffix.lowercase() to style }
        }.toMap()

        fun ofSuffix(suffix: String, suffixMap: Map<String, OutputCommentStyle>): OutputCommentStyle {
            val lowerSuffix = suffix.lowercase()
            return suffixMap[lowerSuffix] ?:
                defaultSuffixMap[lowerSuffix] ?:
                throw IllegalArgumentException("Unsupported suffix $suffix, try suffixCommentStyleMap to add suffix")
        }

        fun ofSuffix(suffix: String): OutputCommentStyle {
            return ofSuffix(suffix, defaultSuffixMap)
        }

        private fun createBlockComment(
            content: String,
            singleLineFormatter: (String) -> String,
            multiLineFormatter: (String) -> String
        ): String {
            val contentChomped = content.trimEnd()
            return if (contentChomped.contains('\n')) {
                multiLineFormatter(contentChomped)
            } else {
                singleLineFormatter(contentChomped)
            }
        }
    }

    abstract fun createCommentBlock(content: String): String

    internal fun prefixedLines(builder: StringBuilder, prefix: String, content: String) {
        content.lines().forEach {
            if (it.isNotBlank())
                builder.append(prefix).append(it).append(LINE_SEPARATOR)
        }
    }
}
