package com.yidigun.base.processors;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import lombok.launch.AnnotationProcessorHelper;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Processor;

import java.util.regex.Pattern;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class CheckEqualsAndHashCodeProcessorTest {

    private Compiler getTestCompiler(Processor... processors) {

        Processor lombok1 = AnnotationProcessorHelper.getAnnotationProcessor();
        Processor lombok2 = AnnotationProcessorHelper.getClaimingProcessor();

        CheckEqualsAndHashCodeProcessor pr = new CheckEqualsAndHashCodeProcessor();

        return Compiler.javac()
                .withProcessors(lombok1, lombok2, pr)
                .withOptions("-Xlint:all");
    }

    @Test
    public void testManualCoded() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;

import java.util.Objects;

@CheckEqualsAndHashCode
public class ManualCoded {
    private final String name;
    public ManualCoded(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ManualCoded that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
""";
        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.ManualCoded", source));

        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void testNotOverrideBoth() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@CheckEqualsAndHashCode
public class NotOverrideBoth {
    private final String name;
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.NotOverrideBoth", source));

        String expectedClass = "com.yidigun.base.processors.NotOverrideBoth".replace(".", "\\.");
        String expectedMethods = "equals\\(\\), hashCode\\(\\)";
        Pattern warningPattern = Pattern.compile(
                String.format("^%s 클래스는 %s 메소드를 재정의해야 합니다\\.$", expectedClass, expectedMethods));

        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContainingMatch(warningPattern);
    }

    @Test
    public void testCheckSubclass() {

        String baseSource = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;

@CheckEqualsAndHashCode
public abstract class AbstractBaseClass {}
""";

        String source = """
package com.yidigun.base.processors.sub;

import com.yidigun.base.processors.AbstractBaseClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotOverrideSubclass extends AbstractBaseClass {
    private final String name;
}
""";
        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                                "com.yidigun.base.processors.AbstractBaseClass", baseSource),
                        JavaFileObjects.forSourceString(
                                "com.yidigun.base.processors.sub.NotOverrideSubclass", source));

        String expectedClass = "com.yidigun.base.processors.sub.NotOverrideSubclass".replace(".", "\\.");
        String expectedMethods = "equals\\(\\), hashCode\\(\\)";
        Pattern warningPattern = Pattern.compile(
                String.format("^%s 클래스는 %s 메소드를 재정의해야 합니다\\.$", expectedClass, expectedMethods));

        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContainingMatch(warningPattern);
    }

    @Test
    public void testNotOverrideEquals() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@CheckEqualsAndHashCode
public class NotOverrideEquals {
    private final String name;
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.NotOverrideEquals", source));

        String expectedClass = "com.yidigun.base.processors.NotOverrideEquals".replace(".", "\\.");
        String expectedMethods = "equals\\(\\)";
        Pattern warningPattern = Pattern.compile(
                String.format("^%s 클래스는 %s 메소드를 재정의해야 합니다\\.$", expectedClass, expectedMethods));

        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContainingMatch(warningPattern);
    }

    @Test
    public void testNotOverrideHashCode() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@CheckEqualsAndHashCode
public class NotOverrideHashCode {
    private final String name;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NotOverrideHashCode that)) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.NotOverrideHashCode", source));

        String expectedClass = "com.yidigun.base.processors.NotOverrideHashCode".replace(".", "\\.");
        String expectedMethods = "hashCode\\(\\)";
        Pattern warningPattern = Pattern.compile(
                String.format("^%s 클래스는 %s 메소드를 재정의해야 합니다\\.$", expectedClass, expectedMethods));

        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContainingMatch(warningPattern);
    }

    @Test
    public void testNotOverrideAndSuppress() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@CheckEqualsAndHashCode
@SuppressWarnings("checkEqualsAndHashCode")
public class NotOverrideAndSuppress {
    private final String name;
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.NotOverrideAndSuppress", source));

        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void testEnclosedClass() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OuterClass {

    @Getter
    @RequiredArgsConstructor
    @CheckEqualsAndHashCode
    class EnclosedNotOverrideBoth {
        private final String name;
    }
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.OuterClass", source));

        String expectedClass = "com.yidigun.base.processors.OuterClass.EnclosedNotOverrideBoth".replace(".", "\\.");
        String expectedMethods = "equals\\(\\), hashCode\\(\\)";
        Pattern warningPattern = Pattern.compile(
                String.format("^%s 클래스는 %s 메소드를 재정의해야 합니다\\.$", expectedClass, expectedMethods));

        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContainingMatch(warningPattern);
    }

    @Test
    public void testEnclosedClassSuppress() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("checkEqualsAndHashCode")
public class OuterClass {

    @Getter
    @RequiredArgsConstructor
    @CheckEqualsAndHashCode
    class EnclosedClassSuppress {
        private final String name;
    }
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.OuterClass", source));

        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void testSuppressAtPackage() {

        String packageInfo = """
@SuppressWarnings("checkEqualsAndHashCode")
package com.yidigun.base.processors;
""";

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OuterClass {

    @Getter
    @RequiredArgsConstructor
    @CheckEqualsAndHashCode
    class SuppressAtPackage {
        private final String name;
    }
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.package-info", packageInfo),
                        JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.OuterClass", source));

        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void testEnclosedStaticClass() {

        String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OuterClass {

    @Getter
    @RequiredArgsConstructor
    @CheckEqualsAndHashCode
    public static class StaticEnclosedNotOverrideBoth {
        private final String name;
    }
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.OuterClass", source));

        String expectedClass = "com.yidigun.base.processors.OuterClass.StaticEnclosedNotOverrideBoth".replace(".", "\\.");
        String expectedMethods = "equals\\(\\), hashCode\\(\\)";
        Pattern warningPattern = Pattern.compile(
                String.format("^%s 클래스는 %s 메소드를 재정의해야 합니다\\.$", expectedClass, expectedMethods));

        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContainingMatch(warningPattern);
    }

    @Test
    public void testLombokGenerated() {

        final String source = """
package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@CheckEqualsAndHashCode
public class LombokGenerated {
    private final String name;
}
""";

        Compilation compilation = getTestCompiler()
                .compile(JavaFileObjects.forSourceString(
                        "com.yidigun.base.processors.LombokGenerated", source));

        assertThat(compilation)
                .succeededWithoutWarnings();
    }

}
