package org.molgenis.vcf.annotate.db2.gnomad;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import org.molgenis.vcf.annotate.db2.exact.*;
import org.molgenis.vcf.annotate.db2.exact.AnnotationDbWriter;
import org.molgenis.vcf.annotate.util.Quantized16UnitIntervalDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnomAdAnnotationDbBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(GnomAdAnnotationDbBuilder.class);

  public GnomAdAnnotationDbBuilder() {}

  public void create(File gnomAdFile, File zipFile) {
    try (BufferedReader reader = createReader(gnomAdFile);
        ZipOutputStream zipOutputStream = createWriter(zipFile)) {
      new AnnotationDbWriter().create(new GnomAdShortVariantIterator(reader), zipOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static BufferedReader createReader(File gnomAdFile) throws IOException {
    return new BufferedReader(
        new InputStreamReader(
            new GZIPInputStream(new FileInputStream(gnomAdFile)), StandardCharsets.UTF_8),
        1048576);
  }

  private static ZipOutputStream createWriter(File zipFile) throws FileNotFoundException {
    return new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), 1048576));
  }

  private static short toQuantizedShort(String str) {
    Double doubleValue = str != null && !str.isEmpty() ? Double.parseDouble(str) : null;

    // FIXME remove workaround after bug in gnomad resource fixed
    if (doubleValue != null && doubleValue > 1) doubleValue = 1d;

    return Quantized16UnitIntervalDouble.toShort(doubleValue);
  }

  private static class GnomAdShortVariantIterator implements Iterator<VariantAltAlleleAnnotation> {
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

      byte[] blob = GnomAdShortVariantAnnotationCodec.encode(annotationBuilder.build());
      return new VariantAltAlleleAnnotation(
          new VariantAltAllele(
              chrom, start, start + length - 1, alt.getBytes(StandardCharsets.UTF_8)),
          blob);
    }
  }
}
