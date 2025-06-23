# User Guides

## `projectInfo`

```kotlin
projectInfo {
    enabled.set(true)                           // default: false
    packageName.set("com.example.myproject")    // default: ${project.group}.${project.name}
    className.set("MyProjectInfo")              // default: ProjectInfo
    model.set(mapOf(
        "project.tribute.to" to "Richard Stallman",
        "project.maintainer" to "Jane Doe",
    ))
}
```

To use your own template, you can configure like this:

```kotlin
import com.yidigun.gradle.forge.templates.TemplateLanguage
// ...
projectInfo {
    // ...
    templateLanguage.set(TemplateLanguage.FREEMARKER)
    templateDir.set("src/main/template/project-info")   // default: none
    templateFile.set("ProjectInfo.java.ftl")           // default: none
}
```

```kotlin
import com.yidigun.gradle.forge.templates.HandlebarsProcessorConfig
// ...
projectInfo {
    // ...
    templateLanguage.set(TemplateLanguage.HANDLEBARS)
    templateProcessorConfig.set(HandlebarsProcessorConfig(defaultSuffix = "bhs"))
    templateURLBase.set("https://dev.example.com/templates/project-info")
    templateFile.set("com/example/myproject/MyProjectInfo.java.hbs")
}
```



## `buildInfo`

```kotlin
buildInfo {
    enabled.set(true)                           // default: false
}
```

## `templatedSources`

```kotlin
templatedSources {
    
    create("api") {
        templateLanguage.set(TemplateLanguage.FREEMARKER)
        templateDir.set("src/main/template/api")
    }
    
    create("javascript") {
        templateLanguage.set(TemplateLanguage.HANDLEBARS)
        templateDir.set("src/main/template/js")
    }
}
```
