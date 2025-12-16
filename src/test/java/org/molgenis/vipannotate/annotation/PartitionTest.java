package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PartitionTest {
  @Test
  void calcMaxPos() {
    Contig contig = new Contig("chrZ", (1 << Partition.NR_POS_BITS) + 10);
    Partition<?, ?, ?> partition = new Partition<>(new PartitionKey(contig, 0), List.of());
    assertEquals(1 << Partition.NR_POS_BITS, partition.calcMaxPos());
  }

  @Test
  void calcMaxPosLastBin() {
    Contig contig = new Contig("chrZ", 10);
    Partition<?, ?, ?> partition = new Partition<>(new PartitionKey(contig, 0), List.of());
    assertEquals(10, partition.calcMaxPos());
  }

  @Test
  void calcMaxPosNoLength() {
    Contig contig = new Contig("chrZ");
    Partition<?, ?, ?> partition = new Partition<>(new PartitionKey(contig, 3), List.of());
    assertThrows(IllegalArgumentException.class, partition::calcMaxPos);
  }

  @Test
  void getPartitionStart() {
    PartitionKey key = new PartitionKey(new Contig("chrZ"), 1);
    assertEquals(5, Partition.getPartitionStart(key, 5 + (1 << Partition.NR_POS_BITS)));
  }

  @Test
  void getPartitionStartFirstBin() {
    PartitionKey key = new PartitionKey(new Contig("chrZ"), 0);
    assertEquals(5, Partition.getPartitionStart(key, 5));
  }
}
