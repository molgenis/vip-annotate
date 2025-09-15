package org.molgenis.vipannotate.annotation.phylop;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.fory.memory.MemoryBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.IndexedAnnotation;
import org.molgenis.vipannotate.util.DoubleCodec;

@SuppressWarnings("DataFlowIssue")
class PhyloPAnnotationEncoderDecoderIT {
  private PhyloPAnnotationEncoder phyloPAnnotationEncoder;
  private PhyloPAnnotationDecoder phyloPAnnotationDecoder;

  @BeforeEach
  void setUp() {
    DoubleCodec doubleCodec = new DoubleCodec();
    phyloPAnnotationEncoder = new PhyloPAnnotationEncoder(doubleCodec);
    phyloPAnnotationDecoder = new PhyloPAnnotationDecoder(doubleCodec);
  }

  @Test
  void encodeDecode() {
    double scoreMin = -20.0d;
    double scoreMax = 10.003d;
    double maxQuantizationError =
        (scoreMax - scoreMin) / (2 * ((1 << Short.SIZE) - 2)); // 0.0002289

    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(1000 * Short.BYTES);
    for (int i = 0; i < 1000; ++i) {
      Double randomScore = scoreMin + (scoreMax - scoreMin) * Math.random();
      phyloPAnnotationEncoder.encode(
          new IndexedAnnotation<>(i, new DoubleValueAnnotation(randomScore)), memoryBuffer);
      DoubleValueAnnotation doubleValueAnnotation = phyloPAnnotationDecoder.decode(memoryBuffer, i);

      assertEquals(
          randomScore,
          doubleValueAnnotation.score(),
          maxQuantizationError,
          "score: %.10f error: %.10f max_error: %.10f"
              .formatted(
                  randomScore, randomScore - doubleValueAnnotation.score(), maxQuantizationError));
    }
  }
}
