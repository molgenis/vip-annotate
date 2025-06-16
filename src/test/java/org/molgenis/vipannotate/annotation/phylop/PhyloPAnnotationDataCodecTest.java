package org.molgenis.vipannotate.annotation.phylop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PhyloPAnnotationDataCodecTest {
  private PhyloPAnnotationDataCodec phyloPAnnotationDataCodec;

  @BeforeEach
  void setUp() {
    phyloPAnnotationDataCodec = new PhyloPAnnotationDataCodec();
  }

  @Test
  void encodeNull() {
    assertEquals(0, phyloPAnnotationDataCodec.encode(null));
  }

  @Test
  void decodeNull() {
    assertNull(phyloPAnnotationDataCodec.decode((short) 0));
  }

  @Test
  void encodeDecode() {
    double scoreMin = -20.0d;
    double scoreMax = 10.003d;
    double maxQuantizationError = (scoreMax - scoreMin) / (2 * ((1 << Short.SIZE) - 2));

    for (int i = 0; i < 10000; ++i) {
      Double randomScore = scoreMin + (scoreMax - scoreMin) * Math.random();
      short encodedScore = phyloPAnnotationDataCodec.encode(randomScore);
      Double decodedScore = phyloPAnnotationDataCodec.decode(encodedScore);
      assertEquals(
          randomScore,
          decodedScore,
          maxQuantizationError,
          "score: %.10f error: %.10f max_error: %.10f"
              .formatted(randomScore, randomScore - decodedScore, maxQuantizationError));
    }
  }
}
