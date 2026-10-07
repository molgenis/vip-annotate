package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.annotation.spec.PositionBinSpec;

class PartitionResolverTest {
  private PartitionResolver partitionResolver;

  @BeforeEach
  void setUp() {
    partitionResolver =
        new PartitionResolver(Map.of("chr1", new PositionBinSpec(0L, 1024L, (byte) 18)));
  }

  @Test
  void resolvePartitionKey() {
    Contig contig = when(mock(Contig.class).getName()).thenReturn("chr1").getMock();
    assertEquals(new PartitionKey(contig, 0), partitionResolver.resolvePartitionKey(contig, 123));
  }

  @Test
  void resolvePartitionKeyFromInterval() {
    Contig contig = when(mock(Contig.class).getName()).thenReturn("chr1").getMock();
    Interval interval = new Interval(contig, 123, 456);
    assertEquals(new PartitionKey(contig, 0), partitionResolver.resolvePartitionKey(interval));
  }

  @Test
  void resolvePartitionKeyFromAnnotatedInterval() {
    Contig contig = when(mock(Contig.class).getName()).thenReturn("chr1").getMock();
    requireNonNull(contig);
    Interval interval = new Interval(contig, 123, 456);
    @SuppressWarnings("NullAway") // false positive?
    AnnotatedInterval<Interval, @Nullable Annotation> annotatedInterval =
        new AnnotatedInterval<>(interval, null);
    assertEquals(
        new PartitionKey(contig, 0), partitionResolver.resolvePartitionKey(annotatedInterval));
  }

  @Test
  void createSame() {
    Contig contig = when(mock(Contig.class).getName()).thenReturn("chr1").getMock();
    int bin = 0;
    assertSame(
        partitionResolver.resolvePartitionKey(contig, bin),
        partitionResolver.resolvePartitionKey(contig, bin));
  }
}
