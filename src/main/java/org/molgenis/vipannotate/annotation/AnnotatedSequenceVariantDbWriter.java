package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.SortedIntArrayWrapper;

/**
 * Writes annotated sequence variants to a partitioned database
 *
 * @param <T> type of sequence variant annotation
 */
@RequiredArgsConstructor
public class AnnotatedSequenceVariantDbWriter<T extends Annotation>
    implements AnnotatedFeatureDbWriter<SequenceVariant, T, AnnotatedSequenceVariant<T>> {
  private final AnnotatedIntervalPartitionWriter<SequenceVariant, T, AnnotatedSequenceVariant<T>>
      annotatedIntervalPartitionWriter;
  private final AnnotationIndexWriter annotationIndexWriter;
  @Nullable private List<IntEncodedAnnotatedSequenceVariant<T>> intEncodedAnnotatedSequenceVariants;

  @Nullable
  private List<EncodedAnnotatedSequenceVariant<BigInteger, T>>
      bigIntegerEncodedAnnotatedSequenceVariants;

  public void write(Iterator<AnnotatedSequenceVariant<T>> annotatedFeatureIt) {
    List<AnnotatedSequenceVariant<T>> reusableAnnotatedVariants = new ArrayList<>();
    for (PartitionIterator<SequenceVariant, T, AnnotatedSequenceVariant<T>> partitionIt =
            new PartitionIterator<>(annotatedFeatureIt, reusableAnnotatedVariants);
        partitionIt.hasNext(); ) {
      write(partitionIt.next());
    }
  }

  private void write(Partition<SequenceVariant, T, AnnotatedSequenceVariant<T>> partition) {
    intEncodedAnnotatedSequenceVariants =
        intEncodedAnnotatedSequenceVariants != null
            ? new ArrayList<>(intEncodedAnnotatedSequenceVariants.size())
            : new ArrayList<>();

    bigIntegerEncodedAnnotatedSequenceVariants =
        bigIntegerEncodedAnnotatedSequenceVariants != null
            ? new ArrayList<>(bigIntegerEncodedAnnotatedSequenceVariants.size())
            : new ArrayList<>();

    for (AnnotatedSequenceVariant<T> annotatedFeature : partition.annotatedIntervals()) {
      SequenceVariant variant = annotatedFeature.getFeature();

      // encode
      if (VariantEncoder.isSmallVariant(variant)) {
        int encodedVariant = VariantEncoder.encodeSmall(variant);
        intEncodedAnnotatedSequenceVariants.add(
            new IntEncodedAnnotatedSequenceVariant<>(encodedVariant, annotatedFeature));
      } else {
        BigInteger encodedVariant = VariantEncoder.encodeBig(variant);
        bigIntegerEncodedAnnotatedSequenceVariants.add(
            new EncodedAnnotatedSequenceVariant<>(encodedVariant, annotatedFeature));
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

    // write index
    AnnotationIndex annotationIndex =
        new AnnotationIndexImpl(
            new VariantAnnotationIndexSmall(new SortedIntArrayWrapper(smallIndex)),
            new VariantAnnotationIndexBig(bigIndex));
    annotationIndexWriter.write(partition.key(), annotationIndex);

    // combine item data
    List<AnnotatedSequenceVariant<T>> allList =
        new ArrayList<>(
            intEncodedAnnotatedSequenceVariants.size()
                + bigIntegerEncodedAnnotatedSequenceVariants.size());
    intEncodedAnnotatedSequenceVariants.forEach(
        encodedVariantAnnotation ->
            allList.add(encodedVariantAnnotation.annotatedSequenceVariant()));
    bigIntegerEncodedAnnotatedSequenceVariants.forEach(
        encodedVariantAnnotation ->
            allList.add(encodedVariantAnnotation.annotatedSequenceVariant()));

    Partition<SequenceVariant, T, AnnotatedSequenceVariant<T>> allPartition =
        new Partition<>(partition.key(), allList);
    annotatedIntervalPartitionWriter.write(allPartition);
  }
}
