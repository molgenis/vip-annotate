package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;
import org.molgenis.vipannotate.serialization.SortedIntArrayWrapper;
import org.molgenis.vipannotate.util.SizedIterable;
import org.molgenis.vipannotate.util.TransformingIterable;

public class VariantAnnotationDbWriter<T extends VariantAnnotation<U>, U> {
  private final AnnotationIndexWriter annotationIndexWriter;
  private final AnnotationDatasetWriter<U> annotationDatasetWriter;
  private final List<EncodedSmallVariantAnnotation<U>> reusableEncodedSmallVariantAnnotations;
  private final List<EncodedBigVariantAnnotation<U>> reusableEncodedBigVariantAnnotations;

  public VariantAnnotationDbWriter(
      @NonNull AnnotationIndexWriter annotationIndexWriter,
      @NonNull AnnotationDatasetWriter<U> annotationDatasetWriter) {
    this.annotationIndexWriter = annotationIndexWriter;
    this.annotationDatasetWriter = annotationDatasetWriter;
    reusableEncodedSmallVariantAnnotations = new ArrayList<>();
    reusableEncodedBigVariantAnnotations = new ArrayList<>();
  }

  public void create(Iterator<T> variantAnnotationIterator) {
    for (ReusableGenomePartitionIterator<T, U> reusableGenomePartitionIterator =
            new ReusableGenomePartitionIterator<>(variantAnnotationIterator);
        reusableGenomePartitionIterator.hasNext(); ) {
      process(reusableGenomePartitionIterator.next());
    }
  }

  private void process(GenomePartition<T, U> genomePartition) {
    reusableEncodedSmallVariantAnnotations.clear();
    reusableEncodedBigVariantAnnotations.clear();

    for (T variantAnnotation : genomePartition.getIntervalAnnotationList()) {
      Variant variant = variantAnnotation.variant();

      // encode
      if (VariantEncoder.isSmallVariant(variant)) {
        int encodedVariant = VariantEncoder.encodeSmall(variant);
        reusableEncodedSmallVariantAnnotations.add(
            new EncodedSmallVariantAnnotation<>(encodedVariant, variantAnnotation));
      } else {
        BigInteger encodedVariant = VariantEncoder.encodeBig(variant);
        reusableEncodedBigVariantAnnotations.add(
            new EncodedBigVariantAnnotation<>(encodedVariant, variantAnnotation));
      }
    }

    write(
        genomePartition.getGenomePartitionKey(),
        reusableEncodedSmallVariantAnnotations,
        reusableEncodedBigVariantAnnotations);
  }

  private void write(
      GenomePartitionKey genomePartitionKey,
      List<EncodedSmallVariantAnnotation<U>> encodedSmallVariantAnnotations,
      List<EncodedBigVariantAnnotation<U>> encodedBigVariantAnnotations) {
    // create small item index
    encodedSmallVariantAnnotations.sort(Comparator.comparingInt(o -> o.encodedVariant));
    int[] smallIndex =
        encodedSmallVariantAnnotations.stream()
            .map(EncodedSmallVariantAnnotation::encodedVariant)
            .mapToInt(Integer::intValue)
            .toArray();

    // create big item index
    encodedBigVariantAnnotations.sort(Comparator.comparing(o -> o.encodedVariant));
    BigInteger[] bigIndex =
        encodedBigVariantAnnotations.stream()
            .map(EncodedBigVariantAnnotation::encodedVariant)
            .toList()
            .toArray(new BigInteger[0]);

    // write index
    AnnotationIndex annotationIndex =
        new AnnotationIndexImpl(
            new VariantAnnotationIndexSmall(new SortedIntArrayWrapper(smallIndex)),
            new VariantAnnotationIndexBig(bigIndex));
    annotationIndexWriter.write(genomePartitionKey, annotationIndex);

    // combine item data
    List<VariantAnnotation<U>> allList =
        new ArrayList<>(
            encodedSmallVariantAnnotations.size() + encodedBigVariantAnnotations.size());
    encodedSmallVariantAnnotations.forEach(
        encodedVariantAnnotation -> allList.add(encodedVariantAnnotation.variantAnnotation()));
    encodedBigVariantAnnotations.forEach(
        encodedVariantAnnotation -> allList.add(encodedVariantAnnotation.variantAnnotation()));

    annotationDatasetWriter.write(
        genomePartitionKey,
        new SizedIterable<>(
            new TransformingIterable<>(allList, IntervalAnnotation::annotation), allList.size()));
  }

  private record EncodedSmallVariantAnnotation<T>(
      int encodedVariant, VariantAnnotation<T> variantAnnotation) {}

  private record EncodedBigVariantAnnotation<T>(
      BigInteger encodedVariant, VariantAnnotation<T> variantAnnotation) {}
}
