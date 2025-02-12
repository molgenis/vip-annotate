package org.molgenis.vcf.annotate.db2.exact;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.fury.Fury;
import org.molgenis.vcf.annotate.db2.exact.format.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationDbWriter {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationDbWriter.class);

  private final VariantAltAlleleEncoder variantAltAlleleEncoder;
  private final Fury fury;

  private String currentContig;
  private Integer currentPartitionId;

  public AnnotationDbWriter() {
    this(new VariantAltAlleleEncoder(), FuryFactory.createFury());
  }

  /** package-private constructor for unit testing */
  AnnotationDbWriter(VariantAltAlleleEncoder variantAltAlleleEncoder, Fury fury) {
    this.variantAltAlleleEncoder = requireNonNull(variantAltAlleleEncoder);
    this.fury = requireNonNull(fury);
  }

  public void create(
      Iterator<VariantAltAlleleAnnotation> variantAltAlleleAnnotationIterator,
      ZipOutputStream zipOutputStream) {
    reset();

    zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);

    List<EncodedSmallVariantAltAlleleAnnotation> encodedSmallVariantAltAlleleAnnotation =
        new ArrayList<>();
    List<EncodedBigVariantAltAlleleAnnotation> encodedBigVariantAltAlleleAnnotation =
        new ArrayList<>();

    variantAltAlleleAnnotationIterator.forEachRemaining(
        variantAltAlleleAnnotation -> {
          VariantAltAllele variantAltAllele = variantAltAlleleAnnotation.variantAltAllele();

          String contig = variantAltAllele.contig();
          if (currentContig == null) {
            currentContig = contig;
          }

          int partitionId = variantAltAlleleEncoder.getPartitionId(variantAltAllele);
          if (currentPartitionId == null) {
            currentPartitionId = partitionId;
          }

          // encode
          if (variantAltAlleleEncoder.isSmallVariant(variantAltAllele)) {
            int encodedVariantAltAllele = variantAltAlleleEncoder.encodeSmall(variantAltAllele);
            byte[] encodedAnnotations = variantAltAlleleAnnotation.encodedAnnotations();
            encodedSmallVariantAltAlleleAnnotation.add(
                new EncodedSmallVariantAltAlleleAnnotation(
                    encodedVariantAltAllele, encodedAnnotations));

          } else {
            BigInteger encodedVariantAltAllele =
                variantAltAlleleEncoder.encodeBig(variantAltAllele);
            byte[] encodedAnnotations = variantAltAlleleAnnotation.encodedAnnotations();
            encodedBigVariantAltAlleleAnnotation.add(
                new EncodedBigVariantAltAlleleAnnotation(
                    encodedVariantAltAllele, encodedAnnotations));
          }

          // persist
          if (partitionId != currentPartitionId || !contig.equals(currentContig)) {
            write(
                currentContig,
                currentPartitionId,
                encodedSmallVariantAltAlleleAnnotation,
                encodedBigVariantAltAlleleAnnotation,
                zipOutputStream);

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
          zipOutputStream);
    }
  }

  private void createThrows(
      Stream<VariantAltAlleleAnnotation> altAlleleAnnotationStream,
      ZipOutputStream zipOutputStream) {}

  private void write(
      String contig,
      int partitionId,
      List<EncodedSmallVariantAltAlleleAnnotation> encodedSmallVariantAltAlleleAnnotation,
      List<EncodedBigVariantAltAlleleAnnotation> encodedBigVariantAltAlleleAnnotation,
      ZipOutputStream zipOutputStream) {
    // create small item index
    encodedSmallVariantAltAlleleAnnotation.sort(
        Comparator.comparingInt(o -> o.encodedVariantAltAllele));

    int[] smallIndex =
        encodedSmallVariantAltAlleleAnnotation.stream()
            .map(EncodedSmallVariantAltAlleleAnnotation::encodedVariantAltAllele)
            .mapToInt(Integer::intValue)
            .toArray();

    // create small item data
    AnnotationData smallAnnotationData =
        serializeAnnotations(
            encodedSmallVariantAltAlleleAnnotation.stream()
                .map(EncodedSmallVariantAltAlleleAnnotation::encodedAnnotations)
                .toList());

    // create big item index
    encodedBigVariantAltAlleleAnnotation.sort(Comparator.comparing(o -> o.encodedVariantAltAllele));
    BigInteger[] bigIndex =
        encodedBigVariantAltAlleleAnnotation.stream()
            .map(EncodedBigVariantAltAlleleAnnotation::encodedVariantAltAllele)
            .toList()
            .toArray(new BigInteger[0]);

    // create big item data
    AnnotationData bigAnnotationData =
        serializeAnnotations(
            encodedBigVariantAltAlleleAnnotation.stream()
                .map(EncodedBigVariantAltAlleleAnnotation::encodedAnnotations)
                .toList());

    AnnotationDbPartition gnomAdAnnotationDb =
        new AnnotationDbPartition(
            new SmallVariantIndexLookupTable(new SortedIntArrayWrapper(smallIndex)),
            smallAnnotationData,
            new BigVariantIndexLookupTable(bigIndex),
            bigAnnotationData);

    // serialize and write to zip entry
    LOGGER.info("creating database partition {}", contig + "/" + partitionId + ".vdb");
    ZipEntry zipEntry = new ZipEntry(contig + "/" + partitionId + ".vdb");
    try {
      zipOutputStream.putNextEntry(zipEntry);

      try (ByteArrayOutputStream byteArrayOutputStream =
          new ByteArrayOutputStream(8388608)) { // 8 MB
        fury.serializeJavaObject(byteArrayOutputStream, gnomAdAnnotationDb);
        zipOutputStream.write(byteArrayOutputStream.toByteArray());
      }

      zipOutputStream.closeEntry();
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
