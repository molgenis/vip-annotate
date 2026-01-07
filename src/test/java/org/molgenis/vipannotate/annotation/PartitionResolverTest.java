package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PartitionResolverTest {
  private PartitionResolver partitionResolver;

  @BeforeEach
  void setUp() {
    partitionResolver = new PartitionResolver();
  }

  @Test
  void resolvePartitionKey() {
    Contig contig = mock(Contig.class);
    assertEquals(new PartitionKey(contig, 0), partitionResolver.resolvePartitionKey(contig, 123));
  }

  @Test
  void resolvePartitionKeyFromInterval() {
    Contig contig = mock(Contig.class);
    Interval interval = new Interval(contig, 123, 456);
    assertEquals(new PartitionKey(contig, 0), partitionResolver.resolvePartitionKey(interval));
  }

  @Test
  void resolvePartitionKeyFromAnnotatedInterval() {
    Contig contig = mock(Contig.class);
    requireNonNull(contig);
    Interval interval = new Interval(contig, 123, 456);
    @SuppressWarnings("NullAway") // false positive?
    AnnotatedInterval<Interval, @Nullable Annotation> annotatedInterval =
        new AnnotatedInterval<>(interval, null);
    assertEquals(
        new PartitionKey(contig, 0), partitionResolver.resolvePartitionKey(annotatedInterval));
  }

  @Test
  void resolvePartitionKeySubsequentCalls() {
    Contig contig0 = mock(Contig.class);
    Contig contig1 = mock(Contig.class);

    // do not use assertAll, order matters
    assertEquals(new PartitionKey(contig0, 0), partitionResolver.resolvePartitionKey(contig0, 123));
    assertEquals(
        new PartitionKey(contig0, 1),
        partitionResolver.resolvePartitionKey(contig0, (1 << Partition.NR_POS_BITS) + 1));
    assertEquals(
        new PartitionKey(contig1, 1),
        partitionResolver.resolvePartitionKey(contig1, (1 << Partition.NR_POS_BITS) + 1));
  }

  @Test
  void createSame() {
    Contig contig = mock(Contig.class);
    int bin = 0;
    assertSame(
        partitionResolver.resolvePartitionKey(contig, bin),
        partitionResolver.resolvePartitionKey(contig, bin));
  }

  private static Stream<Arguments> posProvider() {
    return Stream.of(Arguments.of(1, 1), Arguments.of(1 << 18, 0), Arguments.of((1 << 18) + 2, 2));
  }

  @ParameterizedTest
  @MethodSource("posProvider")
  void getPartitionPos(int pos, int partitionPos) {
    assertEquals(partitionPos, partitionResolver.getPartitionPos(pos));
  }
}
