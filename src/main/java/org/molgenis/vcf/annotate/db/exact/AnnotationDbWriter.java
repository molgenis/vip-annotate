package org.molgenis.vcf.annotate.db.exact;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.Fury;
import org.molgenis.vcf.annotate.db.exact.format.*;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndex;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexBig;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexSmall;
import org.molgenis.vcf.annotate.db.gnomad.ZipCompressionContext;

public class AnnotationDbWriter {
  private final VariantEncoder variantEncoder;
  private final Fury fury;

  private String currentContig;
  private Integer currentPartitionId;

  public AnnotationDbWriter() {
    this(new VariantEncoder(), FuryFactory.createFury());
  }

  /** package-private constructor for unit testing */
  AnnotationDbWriter(VariantEncoder variantEncoder, Fury fury) {
    this.variantEncoder = requireNonNull(variantEncoder);
    this.fury = requireNonNull(fury);
  }

  public void create(
      Iterator<VariantAltAlleleAnnotation> variantAltAlleleAnnotationIterator,
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    reset();

    List<EncodedSmallVariantAltAlleleAnnotation> encodedSmallVariantAltAlleleAnnotation =
        new ArrayList<>();
    List<EncodedBigVariantAltAlleleAnnotation> encodedBigVariantAltAlleleAnnotation =
        new ArrayList<>();

    variantAltAlleleAnnotationIterator.forEachRemaining(
        variantAltAlleleAnnotation -> {
          Variant variant = variantAltAlleleAnnotation.variant();
          byte[] encodedAnnotations = variantAltAlleleAnnotation.encodedAnnotations();

          // encode
          if (VariantEncoder.isSmallVariant(variant)) {
            int encodedVariantAltAllele = VariantEncoder.encodeSmall(variant);

            encodedSmallVariantAltAlleleAnnotation.add(
                new EncodedSmallVariantAltAlleleAnnotation(
                    encodedVariantAltAllele,
                    encodedAnnotations)); // FIXME added to list if contig/partition differs
          } else {
            BigInteger encodedVariantAltAllele = VariantEncoder.encodeBig(variant);
            encodedBigVariantAltAlleleAnnotation.add(
                new EncodedBigVariantAltAlleleAnnotation(
                    encodedVariantAltAllele,
                    encodedAnnotations)); // FIXME added to list if contig/partition differs
          }

          String contig = variant.contig();
          if (currentContig == null) {
            currentContig = contig;
          }

          int partitionId = variantEncoder.getPartitionId(variant);
          if (currentPartitionId == null) {
            currentPartitionId = partitionId;
          }

          if (partitionId != currentPartitionId || !contig.equals(currentContig)) {
            write(
                currentContig,
                currentPartitionId,
                encodedSmallVariantAltAlleleAnnotation,
                encodedBigVariantAltAlleleAnnotation,
                zipCompressionContext,
                zipArchiveOutputStream);

            // reset
            encodedSmallVariantAltAlleleAnnotation.clear();
            encodedBigVariantAltAlleleAnnotation.clear();
            currentContig = contig;
            currentPartitionId = partitionId;
          }
        });

    // write remainder
    if (!encodedSmallVariantAltAlleleAnnotation.isEmpty()
        || !encodedBigVariantAltAlleleAnnotation.isEmpty()) {
      write(
          currentContig,
          currentPartitionId,
          encodedSmallVariantAltAlleleAnnotation,
          encodedBigVariantAltAlleleAnnotation,
          zipCompressionContext,
          zipArchiveOutputStream);
    }
  }

  private void write(
      String contig,
      int partitionId,
      List<EncodedSmallVariantAltAlleleAnnotation> encodedSmallVariantAltAlleleAnnotation,
      List<EncodedBigVariantAltAlleleAnnotation> encodedBigVariantAltAlleleAnnotation,
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    // create small item index
    encodedSmallVariantAltAlleleAnnotation.sort(
        Comparator.comparingInt(o -> o.encodedVariantAltAllele));

    int[] smallIndex =
        encodedSmallVariantAltAlleleAnnotation.stream()
            .map(EncodedSmallVariantAltAlleleAnnotation::encodedVariantAltAllele)
            .mapToInt(Integer::intValue)
            .toArray();

    // create big item index
    encodedBigVariantAltAlleleAnnotation.sort(Comparator.comparing(o -> o.encodedVariantAltAllele));
    BigInteger[] bigIndex =
        encodedBigVariantAltAlleleAnnotation.stream()
            .map(EncodedBigVariantAltAlleleAnnotation::encodedVariantAltAllele)
            .toList()
            .toArray(new BigInteger[0]);

    // create small item data
    List<byte[]> smallList =
        encodedSmallVariantAltAlleleAnnotation.stream()
            .map(EncodedSmallVariantAltAlleleAnnotation::encodedAnnotations)
            .toList();

    // create big item data
    List<byte[]> bigList =
        encodedBigVariantAltAlleleAnnotation.stream()
            .map(EncodedBigVariantAltAlleleAnnotation::encodedAnnotations)
            .toList();

    // combine item data
    List<byte[]> allList = new ArrayList<>(smallList.size() + bigList.size());
    allList.addAll(smallList);
    allList.addAll(bigList);

    AnnotationData annotationData = serializeAnnotations(allList);

    VariantAltAlleleAnnotationIndex variantAltAlleleAnnotationIndex =
        new VariantAltAlleleAnnotationIndex(
            new VariantAltAlleleAnnotationIndexSmall(new SortedIntArrayWrapper(smallIndex)),
            new VariantAltAlleleAnnotationIndexBig(bigIndex),
            annotationData.variantOffsets());

    //    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8388608)) {
    // // 8 MB
    //      fury.serializeJavaObject(byteArrayOutputStream, annotationData.variantsBytes());
    {
      byte[] uncompressedByteArray = annotationData.variantsBytes();
      zipCompressionContext.writeData(
          contig, partitionId, uncompressedByteArray, zipArchiveOutputStream);
    }
    //    } catch (IOException e) {
    //      throw new UncheckedIOException(e);
    //    }

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8388608)) { // 8 MB
      fury.serializeJavaObject(byteArrayOutputStream, variantAltAlleleAnnotationIndex);
      byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray();
      zipCompressionContext.writeDataIndex(
          contig, partitionId, uncompressedByteArray, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private AnnotationData serializeAnnotations(List<byte[]> annotations) {
    byte[][] bytes = new byte[annotations.size()][];
    for (int i = 0; i < annotations.size(); i++) {
      bytes[i] = annotations.get(i);
    }

    int[] offsets = new int[annotations.size()];
    int offset = 0;
    for (int i = 0; i < bytes.length; i++) {
      offsets[i] = offset;
      offset += bytes[i].length;
    }

    byte[] blob = new byte[offset];
    for (int i = 0; i < bytes.length; i++) {
      byte[] b = bytes[i];
      System.arraycopy(b, 0, blob, offsets[i], b.length);
    }

    return new AnnotationData(new SortedIntArrayWrapper(offsets), blob);
  }

  private void reset() {
    currentContig = null;
    currentPartitionId = null;
  }

  private record EncodedSmallVariantAltAlleleAnnotation(
      int encodedVariantAltAllele, byte[] encodedAnnotations) {}

  private record EncodedBigVariantAltAlleleAnnotation(
      BigInteger encodedVariantAltAllele, byte[] encodedAnnotations) {}
}
