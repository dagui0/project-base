package com.yidigun.gradle.forge.templates

data class TemplateContext(
    val model: Map<String, Any> = emptyMap(),
    val options: Map<String, Any> = emptyMap()
)
