package org.molgenis.vipannotate.annotation.phylop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.IndexedAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

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

    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new short[1000]);
    for (int i = 0; i < 1000; ++i) {
      Double randomScore = scoreMin + (scoreMax - scoreMin) * Math.random();
      phyloPAnnotationEncoder.encodeInto(
          new IndexedAnnotation<>(i, new DoubleValueAnnotation(randomScore)), memoryBuffer);
      DoubleValueAnnotation doubleValueAnnotation = phyloPAnnotationDecoder.decode(memoryBuffer, i);

      Double score = doubleValueAnnotation.score();
      assertNotNull(score);
      assertEquals(
          randomScore,
          score,
          maxQuantizationError,
          "score: %.10f error: %.10f max_error: %.10f"
              .formatted(randomScore, randomScore - score, maxQuantizationError));
    }
  }
}
