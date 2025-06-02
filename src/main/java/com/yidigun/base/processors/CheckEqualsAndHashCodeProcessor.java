package com.yidigun.base.processors;

import com.yidigun.base.CheckEqualsAndHashCode;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.*;

/// [CheckEqualsAndHashCode] 어노테이션을 처리하는 프로세서.
///
/// 컴파일시 [Object#equals(Object)] 또는 [Object#hashCode()]를 재정의 했는지를 확인한다.
///
/// @see CheckEqualsAndHashCode
/// @see SuppressWarnings
@SupportedAnnotationTypes("com.yidigun.base.CheckEqualsAndHashCode")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class CheckEqualsAndHashCodeProcessor extends AbstractProcessor {

    /// [Object#equals(Object)]또는 [Object#hashCode()]를 구현하지 않은 경우
    /// 발생시킬 오류 레벨
    /// @see Diagnostic.Kind
    private static final Diagnostic.Kind DEFAULT_KIND = Diagnostic.Kind.WARNING;

    /// 경고 억제를 위한 키 문자열
    /// @see SuppressWarnings
    private static final String SUPPRESS_WARNING_KEY = "checkEqualsAndHashCode";

    /// 비교 대상 [Object#equals(Object)]와 [Object#hashCode()] 메소드
    private EqualsAndHashCode javaLangObject;

    /// 체크할 메소드들 정의
    enum MethodType {
        /// [Object#equals(Object)] 또는 재정의한 메소드
        EQUALS((m) ->
                m.getSimpleName().toString().equals("equals") &&
                        m.getParameters().size() == 1 &&
                        m.getParameters().getFirst().asType().toString().equals("java.lang.Object")
        ),
        /// [Object#hashCode()] 또는 재정의한 메소드
        HASH_CODE((m) ->
                m.getSimpleName().toString().equals("hashCode") &&
                        m.getParameters().isEmpty()
        );

        /// 메소드 시그니처 확인 조건
        public final Predicate<ExecutableElement> signatureMatched;
        MethodType(Predicate<ExecutableElement> signatureMatched) {
            this.signatureMatched = signatureMatched;
        }
    }

    /// 오버라이드 여부 확인 결과
    class EqualsAndHashCode {
        private final TypeElement element;
        private final ExecutableElement equals;
        private final boolean equalsOverridden;
        private final ExecutableElement hashCode;
        private final boolean hashCodeOverridden;

        public EqualsAndHashCode(TypeElement element, ExecutableElement equals, ExecutableElement hashCode) {
            this.element = element;
            this.equals = equals;
            this.equalsOverridden = (equals != null &&
                    javaLangObject != null &&
                    processingEnv.getElementUtils().overrides(
                            equals, javaLangObject.getEquals(), element));
            this.hashCode = hashCode;
            this.hashCodeOverridden = hashCode != null &&
                    javaLangObject != null &&
                    processingEnv.getElementUtils().overrides(
                            hashCode, javaLangObject.getHashCode(), element);
        }

        private ExecutableElement getEquals() { return equals; }
        private ExecutableElement getHashCode() { return hashCode; }
        public boolean equalsOverridden() { return equalsOverridden;  }
        public boolean hashCodeOverridden() { return hashCodeOverridden;  }

        public String message() {
            List<String> missing = new ArrayList<>();
            if (!equalsOverridden)
                missing.add("equals()");
            if (!hashCodeOverridden)
                missing.add("hashCode()");

            return missing.isEmpty()? "":
                    String.format("%s 클래스는 %s 메소드를 재정의해야 합니다.",
                        element.getQualifiedName(),
                        String.join(", ", missing));
        }
    }

    /// 프로세서 초기화.
    ///
    /// [Object#equals(Object)]와 [Object#hashCode()] 메소드를`미리 찾아놓는다.
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        TypeElement objectType = processingEnv.getElementUtils().getTypeElement("java.lang.Object");
        javaLangObject = getEqualsAndHashCode(objectType);
    }

    /// [CheckEqualsAndHashCode] 어노테이션이 붙은 클래스에 대해 equals()와 hashCode() 메소드를 재정의 했는지 확인.
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Elements elementUtils = processingEnv.getElementUtils();

        // 처리 시작
        roundEnv.getElementsAnnotatedWith(CheckEqualsAndHashCode.class)
                .stream()
                .filter(e ->
                        e.getKind() == ElementKind.CLASS && !e.getModifiers().contains(Modifier.ABSTRACT)
                )
                .filter(e -> !isWarningSuppressed(e)) // SuppressWarnings 확인
                .map(clazz -> getEqualsAndHashCode((TypeElement)clazz))
                .filter(result -> !result.equalsOverridden() || !result.hashCodeOverridden())
                .forEach((result) -> {
                    processingEnv.getMessager()
                            .printMessage(DEFAULT_KIND, result.message(), result.element);
                });
        return true;
    }

    private boolean isWarningSuppressed(Element element) {
        // SuppressWarnings 확인
        Element current = element;
        while (current != null) {
            SuppressWarnings suppressed = current.getAnnotation(SuppressWarnings.class);
            if (suppressed != null) {
                for (String value : suppressed.value()) {
                    if (SUPPRESS_WARNING_KEY.equalsIgnoreCase(value)) {
                        return true;
                    }
                }
            }
            // 패키지까지 올라가서 확인하려면 Element.getEnclosingElement()를 사용.
            // 최상위는 PackageElement이거나 null이 됨.
            if (current.getKind() == ElementKind.PACKAGE) { // 패키지 선언(package-info.java)까지 확인
                break;
            }
            current = current.getEnclosingElement();
        }
        return false;
    }

    /// 클래스에서 [Object#equals(Object)]와 [Object#hashCode()] 메소드를 찾아서 반환한다.
    private EqualsAndHashCode getEqualsAndHashCode(TypeElement type) {

        List<ExecutableElement> filtered = type.getEnclosedElements().stream()
                .filter(e ->
                        e.getKind() == ElementKind.METHOD &&
                        e.getModifiers().contains(Modifier.PUBLIC) &&
                        !e.getModifiers().contains(Modifier.ABSTRACT) &&
                        (e.getSimpleName().toString().equals("equals") || e.getSimpleName().toString().equals("hashCode"))
                )
                .map(e -> (ExecutableElement)e)
                .toList();

        return new EqualsAndHashCode(
                type,
                filtered.stream()
                        .filter(MethodType.EQUALS.signatureMatched)
                        .findFirst()
                        .orElse(null),
                filtered.stream()
                        .filter(MethodType.HASH_CODE.signatureMatched)
                        .findFirst()
                        .orElse(null));
    }
}
