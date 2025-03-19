package org.molgenis.vcf.annotate.db.exact;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.Zstd;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.fury.Fury;
import org.molgenis.vcf.annotate.db.exact.format.*;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndex;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexBig;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexSmall;
import org.molgenis.vcf.annotate.util.Logger;

public class AnnotationDbWriter {
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
      ZipArchiveOutputStream zipOutputStream) {
    reset();

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
          if (VariantAltAlleleEncoder.isSmallVariant(variantAltAllele)) {
            int encodedVariantAltAllele = VariantAltAlleleEncoder.encodeSmall(variantAltAllele);
            byte[] encodedAnnotations = variantAltAlleleAnnotation.encodedAnnotations();
            encodedSmallVariantAltAlleleAnnotation.add(
                new EncodedSmallVariantAltAlleleAnnotation(
                    encodedVariantAltAllele, encodedAnnotations));

          } else {
            BigInteger encodedVariantAltAllele =
                VariantAltAlleleEncoder.encodeBig(variantAltAllele);
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

  private void write(
      String contig,
      int partitionId,
      List<EncodedSmallVariantAltAlleleAnnotation> encodedSmallVariantAltAlleleAnnotation,
      List<EncodedBigVariantAltAlleleAnnotation> encodedBigVariantAltAlleleAnnotation,
      ZipArchiveOutputStream zipOutputStream) {
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

    // serialize and write to zip entry
    {
      String zipArchiveEntryName = contig + "/var/" + partitionId + ".zst";
      Logger.info("creating database partition %s", zipArchiveEntryName);
      ZipArchiveEntry zipEntry = new ZipArchiveEntry(zipArchiveEntryName);
      zipEntry.setMethod(ZipMethod.ZSTD.getCode());

      writeZipArchiveEntryBytes(zipOutputStream, zipEntry, annotationData.variantsBytes());
    }
    {
      String zipArchiveEntryName = contig + "/var/" + partitionId + ".idx.zst";
      Logger.info("creating database partition %s index", zipArchiveEntryName);
      ZipArchiveEntry zipEntry = new ZipArchiveEntry(zipArchiveEntryName);
      zipEntry.setMethod(ZipMethod.ZSTD.getCode());

      writeZipArchiveEntry(zipOutputStream, zipEntry, variantAltAlleleAnnotationIndex);
    }
  }

  private void writeZipArchiveEntry(
      ZipArchiveOutputStream zipOutputStream, ZipArchiveEntry zipArchiveEntry, Object javaObject) {

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8388608)) { // 8 MB
      fury.serializeJavaObject(byteArrayOutputStream, javaObject);
      byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray();
      writeZipArchiveEntryBytes(zipOutputStream, zipArchiveEntry, uncompressedByteArray);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void writeZipArchiveEntryBytes(
      ZipArchiveOutputStream zipOutputStream,
      ZipArchiveEntry zipArchiveEntry,
      byte[] uncompressedByteArray) {
    zipArchiveEntry.setSize(uncompressedByteArray.length);

    // do not use ultra 20-22 levels because https://github.com/facebook/zstd/issues/435
    byte[] compressedByteArray = Zstd.compress(uncompressedByteArray, 19);
    try {
      zipOutputStream.putArchiveEntry(zipArchiveEntry);
      zipOutputStream.write(compressedByteArray);
      zipOutputStream.closeArchiveEntry();
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
