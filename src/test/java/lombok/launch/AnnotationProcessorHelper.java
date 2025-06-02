package lombok.launch;

import lombok.launch.AnnotationProcessorHider.AnnotationProcessor;
import lombok.launch.AnnotationProcessorHider.ClaimingProcessor;

import javax.annotation.processing.Processor;

public class AnnotationProcessorHelper {

    public static Processor getAnnotationProcessor() {
        return new AnnotationProcessor();
    }

    public static Processor getClaimingProcessor() {
        return new ClaimingProcessor();
    }
}
