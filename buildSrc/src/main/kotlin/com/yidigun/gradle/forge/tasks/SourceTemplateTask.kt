package com.yidigun.gradle.forge.tasks

/// Gradle custom task to generate templated sources to source files.
abstract class SourceTemplateTask : TemplateTask(), SourceTemplateProperties {

    override fun prepareModel(): Map<String, Any> {
        return mapOf(
            "project.name" to projectName.get(),
            "project.group" to projectGroup.get(),
            "project.version" to projectVersion.get(),
            "java.version" to javaVersion.get().toString()
        ) + model.get()
    }
}
