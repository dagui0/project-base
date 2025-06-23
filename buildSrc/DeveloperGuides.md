# Developer's Guide

## Class Diagram

```
------------------------------------------------------------------          --------------------------------------------------------
<<abstract>>                                                                <<data>>
TemplateTask                                                                OutputConfig
------------------------------------------------------------------ ------>  --------------------------------------------------------
@OutputDirectory outputDir: DirectoryProperty                               @Input charset: String
@Nested outputConfig: Property<OutputConfig>                                @Input headerText: String
@Nested templateProcessorConfig: Property<TemplateProcessorConfig>          @Input footerText: String
@Nested resourceSet: Property<ReadableResourceSet>                          @Input suffixCommentMap: Map<String, OutputCommentStyle>
@Input model: MapProperty<String, Any>                                     ---------------------------------------------------------
------------------------------------------------------------------
@TaskAction generate()
------------------------------------------------------------------
 ^  |   |
 |  |   |   ---------------------------------                       ----------------------------------------------------------------
 |  |   |   <<data>>                                                <<interface>>
 |  |   |   TemplateProcessorConfig              1               1  TemplateLanguage
 |  |   +-> ---------------------------------  <>-----------------  ----------------------------------------------------------------
 |  |       @Input language: TemplateLanguage                       id: String
 |  |       @Input suffix: String                                   defaultSuffix: String
 |  |       @Input options: Map<String, Any>                        defaultConfig: TemplateProcessorConfig
 |  |       ---------------------------------                       ----------------------------------------------------------------
 |  |                                                               getProcessor(config: TemplateProcessorConfig): TemplateProcessor
 |  |                                                               ----------------------------------------------------------------
 |  |
 |  |       ---------------------------------------------           -------------------------------
 |  |       <<interface>>                                           <<interface>
 |  |       ReadableResourceSet                                     ReadableResource
 |  +-----> ---------------------------------------------  ----->   -------------------------------
 |          project: Project                                        resouceSet: ReadableResourceSet
 |          rooUri: URI                                    1  n     uri: URI
 |          @Input defaultCharset: String                  ----<>   absolutePath: String
 |          @Nested buildSupport: IncrementalBuildSupport           relativePath: String
 |          ---------------------------------------------           charset: String
 |          get(path: String): ReadableResource                     -------------------------------
 |          find(path: String): ReadableResource?                   openReader(): Reader
 |          iterator(): Itrator<ReadableResource>                   -------------------------------
 |          ---------------------------------------------
 |
 |              ^
 |              v
 |              | 1         -----------------------------------------------------------------
 |              |           <<interface>>
 |              |        1  IncrementalBuildSupport
 |              +---------  -----------------------------------------------------------------
 |                          @InputFiles sourceFiles: FileCollection
 |                          @Classpath sourceClasspath: FileCollection
 |                          -----------------------------------------------------------------
 |                          changedIterator(Iterable<FileChange>): Iterator<ReadableResource>
 |                          -----------------------------------------------------------------
 |
 |              ----------------------------------------------    ----------------------------------------------
 |              <<interface>>                                     <<interface>>                                 
 |              MultiFilesProperties                              SingleFileProperties                          
 |              ----------------------------------------------    ----------------------------------------------
 |              @InputDirectory templateDir: DirectoryProperty    @InputDirectory templateDir: DirectoryProperty
 |              @Input templateFiles: ListProperty<String>        @Input templateFile: Property<String>         
 |              @Input includes: ListProperty<String>             ----------------------------------------------
 |              @Input excludes: ListProperty<String>                          ^          ^
 |              ----------------------------------------------                 |          |
 |                       ^                                                     |          |
 |                       |                                                     |          |
 |                       |       ---------------------------------------       |          |
 |                       |       <<interface>>                                 |          |
 |                       |       SourceTemplateProperties                      |          |
 |                       |       ---------------------------------------       |          |
 |                       |       @Input javaVersion: Property<Int>             |          |
 |                       |       @Input projectName: Property<String>          |          |
 |                       |       @Input projectGroup: Property<String>         |          |
 |                       |       @Input projectVersion: Property<String>       |          |
 |                       |       ---------------------------------------       |          |
 |                       |                          ^          ^               |          |
 |                       |                          |          |               |          |
 |                       |                          |          |               |          |
 |                       |                          |          |               |          |
 |                       |                          |          |               |          |
 |                 --------------------             |          |               |          |
 +---------------  TemplatedSourcesTask  -----------+          |               |          |
 |                 --------------------                        |               |          |
 |                                                             |               |          |
 |                                                      ---------------        |          |
 +----------------------------------------------------  ProjectInfoTask -------+          |
 |                                                      ---------------                   |
 |                                                                                        |
 |                                                                                  -------------
 +--------------------------------------------------------------------------------  BuildInfoTask
                                                                                    -------------
```
