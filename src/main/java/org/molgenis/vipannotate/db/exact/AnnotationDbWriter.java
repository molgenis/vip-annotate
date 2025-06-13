package org.molgenis.vipannotate.db.exact;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.Fury;
import org.molgenis.vipannotate.db.exact.format.AnnotationData;
import org.molgenis.vipannotate.db.exact.format.FuryFactory;
import org.molgenis.vipannotate.db.exact.format.SortedIntArrayWrapper;
import org.molgenis.vipannotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexBig;
import org.molgenis.vipannotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexSmall;
import org.molgenis.vipannotate.db.gnomad.ZipCompressionContext;
import org.molgenis.vipannotate.db.v2.AnnotationIndex;
import org.molgenis.vipannotate.db.v2.AnnotationIndexImpl;
import org.molgenis.vipannotate.db.v2.GenomePartition;
import org.molgenis.vipannotate.db.v2.GenomePartitionKey;
import org.molgenis.vipannotate.util.PushbackIterator;

public class AnnotationDbWriter {
  private final VariantEncoder variantEncoder;
  private final Fury fury;

  private GenomePartitionKey activeGenomePartitionKey;

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

    for (PushbackIterator<VariantAltAlleleAnnotation> iterator =
            new PushbackIterator<>(variantAltAlleleAnnotationIterator);
        iterator.hasNext(); ) {
      VariantAltAlleleAnnotation variantAltAlleleAnnotation = iterator.next();

      // determine partition
      Variant variant = variantAltAlleleAnnotation.variant();
      GenomePartitionKey genomePartitionKey =
          new GenomePartitionKey(variant.contig(), GenomePartition.calcBin(variant.start()));

      // write partition
      if (!genomePartitionKey.equals(activeGenomePartitionKey)) {
        if (!encodedSmallVariantAltAlleleAnnotation.isEmpty()
            || !encodedBigVariantAltAlleleAnnotation.isEmpty()) {
          write(
              activeGenomePartitionKey,
              encodedSmallVariantAltAlleleAnnotation,
              encodedBigVariantAltAlleleAnnotation,
              zipCompressionContext,
              zipArchiveOutputStream);

          // reset
          encodedSmallVariantAltAlleleAnnotation.clear();
          encodedBigVariantAltAlleleAnnotation.clear();
        }

        activeGenomePartitionKey = genomePartitionKey;
      }

      // process variant
      byte[] encodedAnnotations = variantAltAlleleAnnotation.encodedAnnotations();
      if (VariantEncoder.isSmallVariant(variant)) {
        int encodedVariantAltAllele = VariantEncoder.encodeSmall(variant);

        encodedSmallVariantAltAlleleAnnotation.add(
            new EncodedSmallVariantAltAlleleAnnotation(
                encodedVariantAltAllele, encodedAnnotations));
      } else {
        BigInteger encodedVariantAltAllele = VariantEncoder.encodeBig(variant);
        encodedBigVariantAltAlleleAnnotation.add(
            new EncodedBigVariantAltAlleleAnnotation(encodedVariantAltAllele, encodedAnnotations));
      }
    }

    // write the remainder
    if (!encodedSmallVariantAltAlleleAnnotation.isEmpty()
        || !encodedBigVariantAltAlleleAnnotation.isEmpty()) {
      write(
          activeGenomePartitionKey,
          encodedSmallVariantAltAlleleAnnotation,
          encodedBigVariantAltAlleleAnnotation,
          zipCompressionContext,
          zipArchiveOutputStream);
    }
  }

  private void write(
      GenomePartitionKey genomePartitionKey,
      List<EncodedSmallVariantAltAlleleAnnotation> encodedSmallVariantAltAlleleAnnotation,
      List<EncodedBigVariantAltAlleleAnnotation> encodedBigVariantAltAlleleAnnotation,
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    String contig = genomePartitionKey.contig();
    int partitionId = genomePartitionKey.bin();

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

    AnnotationIndex variantAltAlleleAnnotationIndex =
        new AnnotationIndexImpl(
            new VariantAltAlleleAnnotationIndexSmall(new SortedIntArrayWrapper(smallIndex)),
            new VariantAltAlleleAnnotationIndexBig(bigIndex));

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
    activeGenomePartitionKey = null;
  }

  private record EncodedSmallVariantAltAlleleAnnotation(
      int encodedVariantAltAllele, byte[] encodedAnnotations) {}

  private record EncodedBigVariantAltAlleleAnnotation(
      BigInteger encodedVariantAltAllele, byte[] encodedAnnotations) {}
}
