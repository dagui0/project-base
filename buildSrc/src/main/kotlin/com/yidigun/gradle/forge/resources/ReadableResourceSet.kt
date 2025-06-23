package com.yidigun.gradle.forge.resources

import com.yidigun.gradle.forge.utils.toPhysicalFile
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import java.io.File
import java.io.Serializable
import java.net.URI
import java.nio.charset.Charset

/**
 * Represents a set of readable resources for a project.
 *
 * This abstract class provides methods for retrieving resources and is the base for all
 * resource set implementations.
 *
 * A resource set can be thought of as a collection of [ReadableResource] instances,
 * which internally share a common root and a collection of relative paths.
 *
 * A resource set is closely tied to a [Project] to provide integration with the
 * Gradle build system, such as creating lazy file collections.
 *
 * @see ReadableResource
 * @see Project
 */
abstract class ReadableResourceSet(
    val project: Project,
    open val defaultCharset: Charset = Charsets.UTF_8
): Iterable<ReadableResource>, Serializable {

    /**
     * The root URI for all resources in this set.
     * All resource paths are resolved relative to this URI.
     */
    abstract val rootUri: URI

    /**
     * Retrieves a [ReadableResource] for the given path within this set.
     *
     * @param path the relative path of the resource from the root URI.
     * @return the [ReadableResource] corresponding to the given path.
     * @throws ResourceNotFoundException if the resource is not found.
     */
    abstract fun get(path: String): ReadableResource

    /**
     * Retrieves a [ReadableResource] for the given path, or `null` if it is not found.
     *
     * @param path the relative path of the resource from the root URI.
     * @return a [ReadableResource] if found, or `null`.
     */
    fun find(path: String): ReadableResource? {
        return try {
            get(path)
        } catch (e: ResourceNotFoundException) {
            null
        }
    }

    /**
     * Provides properties and methods to support Gradle's incremental build system.
     * This object is intended to be used as a `@Nested` input on a Gradle task.
     */
    @get:Nested
    open val buildSupport: IncrementalBuildSupport = NonIncrementalBuildSupport(this)

    /**
     * A collection of task properties that support the Gradle incremental build system.
     *
     * This abstract class is designed to be set on a task's `@Nested` input property.
     * It provides Gradle with the physical source files to track for up-to-date checks.
     *
     * When overriding properties in a subclass, do not forget to re-apply the
     * appropriate Gradle input annotation (e.g., `@InputFiles`, `@Classpath`).
     */
    abstract class IncrementalBuildSupport(
        protected open val resourceSet: ReadableResourceSet
    ): Serializable {

        /**
         * Indicates whether the resources in this set are fully trackable for incremental builds.
         *
         * This should return `false` if any of the resources are fetched from a remote
         * location (e.g., `http:`).
         *
         * A task that uses [ReadableResourceSet] should check this property
         * and set `outputs.upToDateWhen { false }` if it returns false.
         *
         * ```kotlin
         * // In your plugin, when configuring a task:
         * val support = task.buildSupport.get()
         * if (!support.fullyTrackable.get()) {
         *     task.logger.warn("Task '{}' contains non-physical resources; incremental build is disabled.", task.name)
         *     task.outputs.upToDateWhen { false }
         * }
         * ```
         */
        @get:Input
        open val fullyTrackable: Provider<Boolean> = resourceSet.project.provider { false }

        /**
         * The collection of source files that the task operates on.
         * Gradle uses this property to determine if the task is up-to-date.
         * If the task has no file-based inputs, this can be an empty collection.
         */
        @get:Optional
        @get:InputFiles
        @get:PathSensitive(PathSensitivity.RELATIVE)
        open val sourceFiles: FileCollection = resourceSet.project.objects.fileCollection()

        /**
         * The collection of classpath entries (JAR files or class directories)
         * that the task operates on.
         * This is used by Gradle to determine if the task is up-to-date, with optimizations
         * specific to classpath resources.
         *
         * If the task does not operate on any classpath resources, this should be an empty collection.
         */
        @get:Optional
        @get:Classpath
        open val sourceClasspath: FileCollection = resourceSet.project.objects.fileCollection()

        /**
         * Returns an iterator over the resources that have changed since the last build execution.
         *
         * An implementation of this method should also handle untrackable resources.
         * For example, if a change to any trackable file occurs, all untrackable
         * resources should also be considered "changed" and included in the result.
         *
         * To support incremental builds, the task's `@TaskAction` method should use this
         * iterator when `InputChanges.isIncremental` is true.
         *
         * ```kotlin
         * @TaskAction
         * fun execute(inputChanges: InputChanges) {
         *     // ...
         *     if (inputChanges.isIncremental) {
         *         val changedSources = buildSupport.get().changedIterator(
         *             inputChanges.getFileChanges(buildSupport.get().sourceFiles)
         *         )
         *         changedSources.forEach { resource -> /* process changed resource */ }
         *     } else {
         *         // ... full rebuild logic ...
         *     }
         * }
         * ```
         * @param changes A collection of file changes that occurred since the last build.
         * @return An iterator over the changed resources.
         */
        open fun changedIterator(changes: Iterable<FileChange>): Iterator<ReadableResource> = resourceSet.iterator()
    }

    /**
     * A no-op implementation of [IncrementalBuildSupport] that does not support incremental builds.
     * This is useful when you do not need to track changes in resources.
     */
    class NonIncrementalBuildSupport(resourceSet: ReadableResourceSet): IncrementalBuildSupport(resourceSet)

    /**
     * An abstract class that provides incremental build support for
     * [URI]-based resources that can be mapped to physical files.
     *
     * If the resource's [URI] can be mapped to a physical file, it is used to track changes to the resource.
     * Otherwise, the resource is always included in the rebuild list.
     *
     * Only `file:` and `jar:file:` URIs are supported for physical file mapping.
     *
     * When overriding properties in a subclass, do not forget to re-apply the
     * appropriate Gradle input annotation (e.g., `@InputFiles`, `@Classpath`).
     */
    abstract class UriToFileIncrementalBuildSupport(
        resourceSet: ReadableResourceSet,
        private val physicalFileMapper: (ReadableResource) -> File? = { it.uri.toURL().toPhysicalFile() }
    ): IncrementalBuildSupport(resourceSet) {

        /**
         * A map that groups resources by their physical file mappings.
         * The keys are physical files, and the values are lists of resources that map to those files.
         * If a resource does not map to any physical file, its key is `null`.
         *
         * This map is used only internally when create the [#physicalFileMapProvider] and [#nonPhysicalFilesProvider].
         */
        private val physicalFileMapGrouper: Provider<Map<File?, List<ReadableResource>>> = resourceSet.project.provider {
            resourceSet.groupBy { physicalFileMapper(it) }
        }

        /**
         * A map of physical files to the list of resources that map to them.
         * The keys are physical files, and the values are lists of resources that map to those files.
         * If a resource does not map to any physical file, its key is `null`.
         *
         * This map is used to track changes to resources based on their physical files.
         */
        protected val physicalFileMapProvider: Provider<Map<File, List<ReadableResource>>> =
            physicalFileMapGrouper.map { groupedMap ->
                groupedMap
                    .filterKeys { it != null }
                    .mapKeys { it.key!! }
            }

        /**
         * A set of resources that do not map to any physical file.
         * This includes resources with `null` keys in the grouped map.
         * These resources are not tracked by Gradle's incremental build system.
         */
        protected val nonPhysicalFilesProvider: Provider<Set<ReadableResource>> =
            physicalFileMapGrouper.map { groupedMap ->
                groupedMap
                    .filterKeys { it == null }
                    .values
                    .flatten()
                    .toSet()
        }

        /**
         * Indicates whether changes to resources in this set are fully tracked.
         *
         * `false` if any of the URIs in the list are remote resources, `true` if they are all file-based.
         *
         * @see IncrementalBuildSupport.fullyTrackable
         */
        @get:Input
        override val fullyTrackable: Provider<Boolean> =
            physicalFileMapGrouper.map { !it.containsKey(null) }

        override fun changedIterator(changes: Iterable<FileChange>): Iterator<ReadableResource> {

            val physicalFileMap = physicalFileMapProvider.get()
            val changedFiles = changes
                .filter { it.changeType != ChangeType.REMOVED }
                .map { it.file }
                .toSet()

            val targetResources = changedFiles
                .flatMap { physicalFileMap[it] ?: emptyList() }
                .toSet() + nonPhysicalFilesProvider.get()

            return targetResources.iterator()
        }
    }
}

/*

```kotlin
interface ReadableResourceSet: Iterator<ReadableResource> {

    val processingTargets: ProcessingTarget

    abstract fun allIterator(): Iterator<ReadableResource>

    fun iterator(): Iterator<ReadableResource> {
        if (processingTargets.all) {
            return allIterator()
        }
        else {
            return allIterator().filter(processingTargets.predicate)
        }
    }
}

templatedSources {

    outputDir.set(project.layout.buildDirectory.dir("generated/generated/sources/api"))

    // set `sourceFiles` for incremental building
    resourceSet.set(ResourceSets.ofFiles(
        project = project,
        rootDir = project.layout.projectDirectory.file("src/main/freemarker/api"),
        includes = setOf("**\\/*.ftl")
    ))

    // 0. not set processingTargets - process all

    // 1. give fixed entries
    processingTargets {
        fixedEntries = setOf(
            "main.ftl",
            "sub1.ftl",
            "sub2.ftl"
        )
    }

    // 2. give rule
    processingTargets {
        predicate = { relativePath -> !relativePath.startsWith("include/") }
    }
}
```

 */