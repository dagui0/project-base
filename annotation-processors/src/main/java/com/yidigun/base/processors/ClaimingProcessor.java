package com.yidigun.base.processors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/// 이 프로세서는 어떠한 작업도 하지 않고, 단순히 "어노테이션 프로세서가 없다"는 경고 억제용으로 사용된다.
///
/// Java 컴파일러는 어떤 어노테이션을 처리하는 어노테이션 프로세서가 없는 경우 경고를 발생시키는데,
/// 런타임에 활용하기 위한 어노테이션을 적용시킨 경우 이러한 경고 메시지를 보게 될 수 있다.
///
/// 이 프로세서는 모든 어노테이션에 대해서 처리한다고 선언함으로써 경고를 억제하게 된다.
/// `lombok.launch.AnnotationProcessorHider$ClaimingProcessor`과 같은 방식이다.
/// 다만 선언만 할 뿐 실제로 아무런 처리를 하지 않는다.
///
/// 하지만 이 프로세서나 복의 `ClaimingProcessor`가 사용 되더라도 경고가 발생할 수 있는데(`-Xlint:all` 같이 모든 경고를 활성화 한 경우 등),
/// 어노테이션 프로세서 관련 경고를 완전히 제거하기 위해서는 컴파일 옵션에서 `-Xlint:-processing` 을 추가할 수 있다.
///
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ClaimingProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return true; // 아무 작업도 하지 않음
    }
}
