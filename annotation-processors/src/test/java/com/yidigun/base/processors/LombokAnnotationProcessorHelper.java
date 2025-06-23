package com.yidigun.base.processors;

import javax.annotation.processing.Processor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/// 테스트 클래스에서 lombok package-private으로 설정된
/// 프로세서들의 참조를 얻기 위한 헬퍼 클래스.
///
public class LombokAnnotationProcessorHelper {

    private static final Constructor<?> annotationProcessorConstructor;
    private static final Constructor<?> claimingProcessorConstructor;

    static {
        try {
            Class<?> annotationProcessorClass = Class.forName("lombok.launch.AnnotationProcessorHider$AnnotationProcessor");
            annotationProcessorConstructor = annotationProcessorClass.getDeclaredConstructor();
            annotationProcessorConstructor.setAccessible(true);

            Class<?> claimingProcessorClass = Class.forName("lombok.launch.AnnotationProcessorHider$ClaimingProcessor");
            claimingProcessorConstructor = claimingProcessorClass.getDeclaredConstructor();
            claimingProcessorConstructor.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize LombokAnnotationProcessorHelper", e);
        }
    }

    public static Processor getAnnotationProcessor() {
        try {
            return (Processor) annotationProcessorConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Processor getClaimingProcessor() {
        try {
            return (Processor) claimingProcessorConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
