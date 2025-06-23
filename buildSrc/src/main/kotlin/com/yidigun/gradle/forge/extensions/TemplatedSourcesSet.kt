package com.yidigun.gradle.forge.extensions

import javax.inject.Inject

abstract class TemplatedSourcesSet
    @Inject constructor(val name: String)
    : TemplateExtension, SourceTemplateExtension, MultipleFilesExtension {

}
