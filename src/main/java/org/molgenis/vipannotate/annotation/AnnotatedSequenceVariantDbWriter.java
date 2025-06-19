package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;
import org.molgenis.vipannotate.AnnotatedFeatureDbWriter;
import org.molgenis.vipannotate.serialization.SortedIntArrayWrapper;

/**
 * Writes annotated sequence variants to a partitioned database
 *
 * @param <T> type of sequence variant annotation
 */
public class AnnotatedSequenceVariantDbWriter<T extends Annotation>
    implements AnnotatedFeatureDbWriter<SequenceVariant, T, AnnotatedSequenceVariant<T>> {
  private final AnnotatedIntervalPartitionWriter<SequenceVariant, T, AnnotatedSequenceVariant<T>>
      annotatedIntervalPartitionWriter;
  private final AnnotationIndexWriter annotationIndexWriter;
  private final List<EncodedAnnotatedSequenceVariant<Integer, T>>
      intEncodedAnnotatedSequenceVariants;
  private final List<EncodedAnnotatedSequenceVariant<BigInteger, T>>
      bigIntegerEncodedAnnotatedSequenceVariants;

  public AnnotatedSequenceVariantDbWriter(
      @NonNull
          AnnotatedIntervalPartitionWriter<SequenceVariant, T, AnnotatedSequenceVariant<T>>
              annotatedFeaturePartitionWriter,
      @NonNull AnnotationIndexWriter annotationIndexWriter) {
    this.annotatedIntervalPartitionWriter = annotatedFeaturePartitionWriter;
    this.annotationIndexWriter = annotationIndexWriter;
    intEncodedAnnotatedSequenceVariants = new ArrayList<>();
    bigIntegerEncodedAnnotatedSequenceVariants = new ArrayList<>();
  }

  public void write(@NonNull Iterator<AnnotatedSequenceVariant<T>> annotatedFeatureIt) {
    for (PartitionIterator<SequenceVariant, T, AnnotatedSequenceVariant<T>> partitionIt =
            new PartitionIterator<>(annotatedFeatureIt);
        partitionIt.hasNext(); ) {
      write(partitionIt.next());
    }
  }

  private void write(Partition<SequenceVariant, T, AnnotatedSequenceVariant<T>> partition) {
    intEncodedAnnotatedSequenceVariants.clear();
    bigIntegerEncodedAnnotatedSequenceVariants.clear();

    for (AnnotatedSequenceVariant<T> annotatedFeature : partition.getAnnotatedIntervals()) {
      SequenceVariant variant = annotatedFeature.getFeature();

      // encode
      if (VariantEncoder.isSmallVariant(variant)) {
        int encodedVariant = VariantEncoder.encodeSmall(variant);
        intEncodedAnnotatedSequenceVariants.add(
            new EncodedAnnotatedSequenceVariant<>(encodedVariant, annotatedFeature));
      } else {
        BigInteger encodedVariant = VariantEncoder.encodeBig(variant);
        bigIntegerEncodedAnnotatedSequenceVariants.add(
            new EncodedAnnotatedSequenceVariant<>(encodedVariant, annotatedFeature));
      }
    }

    // create small item index
    intEncodedAnnotatedSequenceVariants.sort(
        Comparator.comparingInt(EncodedAnnotatedSequenceVariant::encodedSequentVariant));
    int[] smallIndex =
        intEncodedAnnotatedSequenceVariants.stream()
            .map(EncodedAnnotatedSequenceVariant::encodedSequentVariant)
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
    annotationIndexWriter.write(partition.getKey(), annotationIndex);

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

    partition.setAnnotatedIntervals(allList);
    annotatedIntervalPartitionWriter.write(partition);
  }
}
