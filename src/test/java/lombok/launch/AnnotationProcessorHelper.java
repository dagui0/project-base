package lombok.launch;

import javax.annotation.processing.Processor;

/// 테스트 클래스에서 lombok package-private으로 설정된
/// 프로세서들의 참조를 얻기 위한 헬퍼 클래스.
///
/// @see AnnotationProcessorHider
public class AnnotationProcessorHelper {

    @SuppressWarnings("all")
    public static Processor getAnnotationProcessor() {
        return new AnnotationProcessorHider.AnnotationProcessor();
    }

    @SuppressWarnings("all")
    public static Processor getClaimingProcessor() {
        return new AnnotationProcessorHider.ClaimingProcessor();
    }
}
