# Template Processor Custom Tasks

## TODO

- [x] Choose plugin name: Gradle Forge Plugin
- Custom Tasks
  - [x] Abstract tasks and interfaces hierarchy
  - [x] `GenerateTemplatedSourceTask`
  - [ ] `GenerateProjectInfoTask`
    - [x] Default template: `ProjectInfo.java.tmpl`
    - [ ] Verify handling single input/output pair safe for incremental build
    - [ ] Implement template source from URL(for `classpath:`)
  - [ ] `GenerateBuildInfoTask`
    - Requires Freemarker support
    - [ ] Check "Spring Boot Actuator" `/info` endpoint
    - [ ] Default template: `build-info.yaml.ftl`
    - [ ] Generate YAML and transform to `*.properties` file at the same time
    - [ ] Add `env`, `gradle` and `git` map to `model` automatically
    - [ ] Study how to integrate with gradle `package` phase
    - [ ] Build environment: timestamp, host, user, os, jdk and gradle version
    - [ ] Gradle properties and parameters: `-Ptarget=...`
    - [ ] CI/CD: build number, CI/CD tools, pipeline/job name
      - Supported environments: jenkins, GitHub actions
    - [ ] Git support: commit id, branch, tags and commit date
      - Study `grgit` plugin
  - [ ] `TransformResourcesTask`
    - Requires JSTL support
    - Requires `org.json` support
    - How to configure multiple input/output pairs manually
  - [ ] `StreamTransformResourceTask`
    - Study streaming JSON processing from remote data source
    - [ ] add `streamSoruce` property
    - [ ] XML stream to JSON stream converter
      - Requires Jackson Streaming API, XML module support
- Template Processors
  - [x] Implement SimpleTemplateProcessor
  - [ ] Implement Freemarker support
  - [ ] Implement Handlebars support
  - [ ] Implement JSTL support
- Plugin Functions
  - [x] Architecture for injecting default values to both extension and manual task
  - [ ] Register plugin from `buildSrc`
  - [ ] Register plugin from specific project in file system
  - [ ] Study architecture for multiple task types
  - [ ] Design helper classes for manual task creation
    - [ ] Simulate how to create custom tasks
  - [ ] Implement plugin
- Documentation 
  - [ ] Study kotlin project's best documentation practices
    - Study Dokka 
  - [ ] Reference manual: plugin, extension and tasks
  - [ ] User guide
    - `projectInfo` and `buildInfo` tasks
    - How to generate Java and js source files with the same interface
    - How to transform and apply my project resources from Open API
  - [ ] Licence comments to all source files
- Publishing
  - Publish to gradle plugin portal
  - Make website and hosting



```kotlin
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * XML 스트림을 JSON 스트림으로 변환하여 처리하는 파이프라인을 실행합니다.
 *
 * @param xmlInputStream 원본 XML 데이터 스트림.
 * @param jsonStreamProcessor JSON 스트림을 실제로 소비하는 로직 (예: 사용자의 기존 스트림 처리 함수).
 */
fun processXmlAsJsonStream(xmlInputStream: InputStream, jsonStreamProcessor: (JsonParser) -> Unit) {
    // 1. 파이프 생성: 생산자가 데이터를 쓸 PipedOutputStream과 소비자가 읽을 PipedInputStream을 만듭니다.
    val pipedOut = PipedOutputStream()
    val pipedIn = PipedInputStream(pipedOut)

    val executor = Executors.newFixedThreadPool(2)

    // 2. 생산자 스레드 설정 (XML -> JSON 변환)
    executor.submit {
        // PipedOutputStream을 JsonGenerator로 감쌉니다.
        // 이 generator에 쓰는 모든 것은 pipedIn으로 흘러 들어갑니다.
        val jsonGenerator = JsonFactory().createGenerator(pipedOut)
        
        try {
            // XmlMapper를 사용하여 XML을 읽고, 그 내용을 JsonGenerator로 보냅니다.
            // 이 한 줄이 내부적으로 SAX와 유사한 스트림 처리를 수행합니다.
            val xmlMapper = XmlMapper()
            xmlMapper.writeValue(jsonGenerator, xmlMapper.readTree(xmlInputStream))
        } catch (e: Exception) {
            // 에러 처리
            System.err.println("XML to JSON producer failed: ${e.message}")
        } finally {
            // 생산이 끝나면 반드시 스트림을 닫아 소비자에게 "끝"을 알려야 합니다.
            pipedOut.close()
        }
    }

    // 3. 소비자 스레드 설정 (JSON 스트림 처리)
    executor.submit {
        try {
            // PipedInputStream을 표준 JsonParser로 감쌉니다.
            val jsonParser = JsonFactory().createParser(pipedIn)
            // 기존의 JSON 스트림 처리 로직을 그대로 호출합니다.
            jsonStreamProcessor(jsonParser)
        } catch (e: Exception) {
            System.err.println("JSON stream consumer failed: ${e.message}")
        } finally {
            pipedIn.close()
        }
    }

    // 두 스레드가 모두 끝날 때까지 대기
    executor.shutdown()
    // executor.awaitTermination(1, TimeUnit.MINUTES) // 필요 시 타임아웃 설정
}

// --- 사용 예시 ---
fun main() {
    val sampleXml = """
        <users>
            <user active="true"><name>Alex</name></user>
            <user active="false"><name>Maria</name></user>
            <user active="true"><name>John</name></user>
        </users>
    """.trimIndent().byteInputStream()

    // 소비할 로직: 'json-streaming-libs' 아티팩트의 예시와 유사한 로직
    val myJsonProcessor = { parser: JsonParser ->
        println("Consumer started processing...")
        if (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.START_OBJECT) {
            if (parser.nextFieldName() == "user") {
                 parser.nextToken() // START_ARRAY
                 while(parser.nextToken() != com.fasterxml.jackson.core.JsonToken.END_ARRAY) {
                     // 여기에 개별 객체를 처리하는 로직...
                     val userNode = ObjectMapper().readTree<com.fasterxml.jackson.databind.JsonNode>(parser)
                     if (userNode.get("active").asBoolean()) {
                         println("Found active user from XML: ${userNode.get("name").asText()}")
                     }
                 }
            }
        }
        println("Consumer finished.")
    }

    processXmlAsJsonStream(sampleXml, myJsonProcessor)
}
```
