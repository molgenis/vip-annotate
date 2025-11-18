package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.util.Logger;

/**
 * Writes annotated sequence variants to a partitioned database
 *
 * @param <U> type of sequence variant annotation
 */
@RequiredArgsConstructor
public class AnnotatedSequenceVariantDbWriter<T extends SequenceVariant, U extends Annotation>
    implements AnnotatedFeatureDbWriter<SequenceVariant, U, AnnotatedSequenceVariant<U>> {
  private final AnnotatedIntervalPartitionWriter<SequenceVariant, U, AnnotatedSequenceVariant<U>>
      annotatedIntervalPartitionWriter;
  private final SequenceVariantAnnotationIndexWriter<T> annotationIndexWriter;
  private final SequenceVariantEncoderDispatcher encoderDispatcher;
  @Nullable private List<IntEncodedAnnotatedSequenceVariant<U>> intEncodedAnnotatedSequenceVariants;

  @Nullable
  private List<EncodedAnnotatedSequenceVariant<BigInteger, U>>
      bigIntegerEncodedAnnotatedSequenceVariants;

  @Override
  public void write(Iterator<AnnotatedSequenceVariant<U>> annotatedFeatureIt) {
    List<AnnotatedSequenceVariant<U>> reusableAnnotatedVariants = new ArrayList<>();
    for (PartitionIterator<SequenceVariant, U, AnnotatedSequenceVariant<U>> partitionIt =
            new PartitionIterator<>(annotatedFeatureIt, reusableAnnotatedVariants);
        partitionIt.hasNext(); ) {
      write(partitionIt.next());
    }
  }

  @SuppressWarnings("DataFlowIssue")
  private void write(Partition<SequenceVariant, U, AnnotatedSequenceVariant<U>> partition) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(
          "processing partition %s/%d", partition.key().contig().getName(), partition.key().bin());
    }

    intEncodedAnnotatedSequenceVariants =
        intEncodedAnnotatedSequenceVariants != null
            ? new ArrayList<>(intEncodedAnnotatedSequenceVariants.size())
            : new ArrayList<>();

    bigIntegerEncodedAnnotatedSequenceVariants =
        bigIntegerEncodedAnnotatedSequenceVariants != null
            ? new ArrayList<>(bigIntegerEncodedAnnotatedSequenceVariants.size())
            : new ArrayList<>();

    for (AnnotatedSequenceVariant<U> annotatedFeature : partition.annotatedIntervals()) {
      SequenceVariant variant = annotatedFeature.getFeature();

      // encode
      EncodedSequenceVariant encodedVariant = encoderDispatcher.encode(variant);
      switch (encodedVariant.getType()) {
        case SMALL ->
            intEncodedAnnotatedSequenceVariants.add(
                new IntEncodedAnnotatedSequenceVariant<>(
                    encodedVariant.getSmall(), annotatedFeature));
        case BIG ->
            bigIntegerEncodedAnnotatedSequenceVariants.add(
                new EncodedAnnotatedSequenceVariant<>(
                    new BigInteger(encodedVariant.getBigBytes()), annotatedFeature));
      }
    }

    // create small item index
    intEncodedAnnotatedSequenceVariants.sort(
        Comparator.comparingInt(IntEncodedAnnotatedSequenceVariant::encodedSequentVariant));

    int[] smallIndex =
        intEncodedAnnotatedSequenceVariants.stream()
            .map(IntEncodedAnnotatedSequenceVariant::encodedSequentVariant)
            .mapToInt(Integer::intValue)
            .toArray();

    // create big item index
    bigIntegerEncodedAnnotatedSequenceVariants.sort(
        Comparator.comparing(EncodedAnnotatedSequenceVariant::encodedSequentVariant));
    BigInteger[] bigIndex =
        bigIntegerEncodedAnnotatedSequenceVariants.stream()
            .map(EncodedAnnotatedSequenceVariant::encodedSequentVariant)
            .toList()
            .toArray(new BigInteger[0]);

    // prepare index writing
    SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher =
        new SequenceVariantAnnotationIndexDispatcher<>();
    indexDispatcher.register(
        Type.SMALL,
        new SequenceVariantAnnotationIndexSmall<T>(
            encoderDispatcher.getEncoder(Type.SMALL), smallIndex));
    indexDispatcher.register(
        Type.BIG,
        new SequenceVariantAnnotationIndexBig<>(encoderDispatcher.getEncoder(Type.BIG), bigIndex));

    // prepare data writing
    List<AnnotatedSequenceVariant<U>> allList =
        new ArrayList<>(
            intEncodedAnnotatedSequenceVariants.size()
                + bigIntegerEncodedAnnotatedSequenceVariants.size());
    intEncodedAnnotatedSequenceVariants.forEach(
        encodedVariantAnnotation ->
            allList.add(encodedVariantAnnotation.annotatedSequenceVariant()));
    bigIntegerEncodedAnnotatedSequenceVariants.forEach(
        encodedVariantAnnotation ->
            allList.add(encodedVariantAnnotation.annotatedSequenceVariant()));

    Partition<SequenceVariant, U, AnnotatedSequenceVariant<U>> allPartition =
        new Partition<>(partition.key(), allList);

    // write data and index
    annotatedIntervalPartitionWriter.write(allPartition);
    annotationIndexWriter.write(partition.key(), indexDispatcher);
  }
}
