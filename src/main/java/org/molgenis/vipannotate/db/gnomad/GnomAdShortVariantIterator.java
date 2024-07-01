package org.molgenis.vipannotate.db.gnomad;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.Quantized16UnitIntervalDouble;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.VariantAltAlleleAnnotation;

class GnomAdShortVariantIterator implements Iterator<VariantAltAlleleAnnotation> {
  private final BufferedReader bufferedReader;

  public GnomAdShortVariantIterator(BufferedReader bufferedReader) {
    this.bufferedReader = requireNonNull(bufferedReader);
  }

  String line = null;

  @Override
  public boolean hasNext() {
    try {
      do {
        line = bufferedReader.readLine();
      } while (line != null && line.startsWith("#"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return line != null;
  }

  @Override
  public VariantAltAlleleAnnotation next() {
    String[] tokens = line.split("\t", -1);

    String chrom = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    int length = tokens[2].length();
    String alt = tokens[3];

    GnomAdShortVariantAnnotation.GnomAdShortVariantAnnotationBuilder annotationBuilder =
        GnomAdShortVariantAnnotation.builder();

    boolean notCalledInExomes = tokens[18].equals("1");
    if (!notCalledInExomes) {
      annotationBuilder.exomes(
          GnomAdShortVariantAnnotation.VariantData.builder()
              .quantizedAf(toQuantizedShort(tokens[4]))
              .quantizedFaf95(toQuantizedShort(tokens[7]))
              .quantizedFaf99(toQuantizedShort(tokens[10]))
              .quantizedCov(toQuantizedShort(tokens[20]))
              .nHomAlt(!tokens[13].isEmpty() ? Integer.parseInt(tokens[13]) : -1)
              .filters(
                  !tokens[16].isEmpty()
                      ? GnomAdShortVariantAnnotation.FilterV2.from(tokens[16])
                      : null)
              .build());
    }

    boolean notCalledInGenomes = tokens[19].equals("1");
    if (!notCalledInGenomes) {
      annotationBuilder.genomes(
          GnomAdShortVariantAnnotation.VariantData.builder()
              .quantizedAf(toQuantizedShort(tokens[5]))
              .quantizedFaf95(toQuantizedShort(tokens[8]))
              .quantizedFaf99(toQuantizedShort(tokens[11]))
              .quantizedCov(toQuantizedShort(tokens[21]))
              .nHomAlt(!tokens[14].isEmpty() ? Integer.parseInt(tokens[14]) : -1)
              .filters(
                  !tokens[17].isEmpty()
                      ? GnomAdShortVariantAnnotation.FilterV2.from(tokens[17])
                      : null)
              .build());
    }

    if (!notCalledInExomes && !notCalledInGenomes) {
      annotationBuilder.joint(
          GnomAdShortVariantAnnotation.VariantData.builder()
              .quantizedAf(toQuantizedShort(tokens[6]))
              .quantizedFaf95(toQuantizedShort(tokens[9]))
              .quantizedFaf99(toQuantizedShort(tokens[12]))
              .quantizedCov(toQuantizedShort(tokens[22]))
              .nHomAlt(!tokens[13].isEmpty() ? Integer.parseInt(tokens[15]) : -1)
              .build());
    }

    MemoryBuffer memoryBuffer =
        new GnomAdShortVariantAnnotationCodec().encode(annotationBuilder.build());
    byte[] blob = Arrays.copyOfRange(memoryBuffer.getHeapMemory(), 0, memoryBuffer.writerIndex());
    return new VariantAltAlleleAnnotation(
        new Variant(chrom, start, start + length - 1, alt.getBytes(StandardCharsets.UTF_8)), blob);
  }

  private static short toQuantizedShort(String str) {
    Double doubleValue = str != null && !str.isEmpty() ? Double.parseDouble(str) : null;
    return Quantized16UnitIntervalDouble.toShort(doubleValue);
  }
}
