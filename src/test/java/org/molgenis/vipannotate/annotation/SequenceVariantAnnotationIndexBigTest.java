package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexBigTest {
  @Mock private SequenceVariantEncoder<SequenceVariant> encoder;

  @Test
  void isEmptyFalse() {
    SequenceVariantAnnotationIndexBig<SequenceVariant> sequenceVariantAnnotationIndexBig =
        new SequenceVariantAnnotationIndexBig<>(encoder, new BigInteger[] {new BigInteger("1")}, 1);
    assertFalse(sequenceVariantAnnotationIndexBig.isEmpty());
  }

  @Test
  void isEmpty() {
    SequenceVariantAnnotationIndexBig<SequenceVariant> sequenceVariantAnnotationIndexBig =
        new SequenceVariantAnnotationIndexBig<>(encoder, new BigInteger[0], 0);
    assertTrue(sequenceVariantAnnotationIndexBig.isEmpty());
  }

  @Test
  void isEmptyNrEncodedVariantsDiffersFromArrayLength() {
    SequenceVariantAnnotationIndexBig<SequenceVariant> sequenceVariantAnnotationIndexBig =
        new SequenceVariantAnnotationIndexBig<>(encoder, new BigInteger[] {new BigInteger("1")}, 0);
    assertTrue(sequenceVariantAnnotationIndexBig.isEmpty());
  }

  @Test
  void reset() {
    SequenceVariantAnnotationIndexBig<SequenceVariant> sequenceVariantAnnotationIndexBig =
        new SequenceVariantAnnotationIndexBig<>(encoder, new BigInteger[] {new BigInteger("1")}, 1);
    sequenceVariantAnnotationIndexBig.reset(new BigInteger[] {new BigInteger("1")}, 0);
    assertTrue(sequenceVariantAnnotationIndexBig.isEmpty());
  }
}
