package org.molgenis.vcf.annotate.spliceai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UtilsTest {

  @Test
  void mean() {
    List<float[][][]> arrays = new ArrayList<>();
    arrays.add(new float[][][] {{{1, 2, 3}, {4, 5, 6}}});
    arrays.add(new float[][][] {{{2, 3, 4}, {5, 6, 7}}});

    float[][][] expectedMean = new float[][][] {{{1.5f, 2.5f, 3.5f}, {4.5f, 5.5f, 6.5f}}};
    assertArrayEquals(expectedMean, Utils.mean(arrays));
  }
}
