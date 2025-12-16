package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.util.BufferedLineReader;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class VcfRecordBatchIteratorTest {
  private VcfRecord vcfRecord0;
  private VcfRecord vcfRecord1;
  private VcfRecord vcfRecord2;
  private VcfRecordBatchIterator vcfRecordBatchIterator;

  @BeforeEach
  void setUp() throws IOException {
    String data =
        """
    1\t0\t.\tA\t.\t.\t.\t.\t.\t.
    1\t1\t.\tA\t.\t.\t.\t.\t.\t.
    1\t2\t.\tA\t.\t.\t.\t.\t.\t.
    1\t3\t.\tA\t.\t.\t.\t.\t.\t.
    1\t4\t.\tA\t.\t.\t.\t.\t.\t.
    """;
    vcfRecord0 = VcfRecordDummyFactory.INSTANCE.createDummy();
    vcfRecord1 = VcfRecordDummyFactory.INSTANCE.createDummy();
    vcfRecord2 = VcfRecordDummyFactory.INSTANCE.createDummy();
    vcfRecordBatchIterator =
        new VcfRecordBatchIterator(
            new BufferedLineReader(new StringReader(data)),
            List.of(vcfRecord0, vcfRecord1, vcfRecord2));
  }

  @AfterEach
  void tearDown() {
    vcfRecordBatchIterator.close();
  }

  @Test
  void hasNextAndNext() {
    assertTrue(vcfRecordBatchIterator.hasNext());
    List<VcfRecord> vcfRecordBatch0 = vcfRecordBatchIterator.next();
    assertEquals(3, vcfRecordBatch0.size());
    assertEquals(0, vcfRecord0.getPos().get());
    assertEquals(1, vcfRecord1.getPos().get());
    assertEquals(2, vcfRecord2.getPos().get());

    assertTrue(vcfRecordBatchIterator.hasNext());
    List<VcfRecord> vcfRecordBatch1 = vcfRecordBatchIterator.next();
    assertEquals(2, vcfRecordBatch1.size());
    assertEquals(3, vcfRecord0.getPos().get());
    assertEquals(4, vcfRecord1.getPos().get());

    assertFalse(vcfRecordBatchIterator.hasNext());
  }
}
