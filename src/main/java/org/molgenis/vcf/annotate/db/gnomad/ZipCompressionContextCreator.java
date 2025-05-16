package org.molgenis.vcf.annotate.db.gnomad;

import com.github.luben.zstd.Zstd;
import java.util.*;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleAnnotation;
import org.molgenis.vcf.annotate.util.Logger;

public class ZipCompressionContextCreator {
  private final int zstdDictionarySize;

  public ZipCompressionContextCreator() {
    this(1024 * 1024);
  }

  public ZipCompressionContextCreator(int zstdDictionarySize) {
    this.zstdDictionarySize = zstdDictionarySize;
  }

  public ZipCompressionContext create(
      Iterator<VariantAltAlleleAnnotation> variantAltAlleleAnnotationIterator) {
    // TODO only train with recommended total sample size (random contig variants, not first x)
    // It's recommended that total size of all samples be about ~x100 times the target size of
    // dictionary, see https://github.com/facebook/zstd/blob/v1.5.7/lib/zdict.h#L208

    Map<String, byte[]> contigDictionaryMap = new HashMap<>();

    List<byte[]> samples = new ArrayList<>();
    String currentContig = null;
    while (variantAltAlleleAnnotationIterator.hasNext()) {
      VariantAltAlleleAnnotation variantAltAlleleAnnotation =
          variantAltAlleleAnnotationIterator.next();
      String contig = variantAltAlleleAnnotation.variant().contig();
      if (currentContig == null) {
        currentContig = contig;
      }
      if (currentContig.equals(contig)) {
        if (samples.size() < 100000) { // trial and error
          samples.add(variantAltAlleleAnnotation.encodedAnnotations());
        }
      } else {
        if (!samples.isEmpty()) {
          Logger.info("creating compression dictionary for contig %s", currentContig);
          byte[] dictionary = createDictionary(samples);
          contigDictionaryMap.put(currentContig, dictionary);
        }

        samples.clear();
        currentContig = contig;
      }
    }

    if (!samples.isEmpty()) {
      Logger.info("creating compression dictionary for contig %s", currentContig);
      byte[] dictionary = createDictionary(samples);
      contigDictionaryMap.put(currentContig, dictionary);
    }

    return new ZipCompressionContext(contigDictionaryMap);
  }

  private byte[] createDictionary(List<byte[]> samples) {
    byte[] dictionary = new byte[this.zstdDictionarySize];
    long code = Zstd.trainFromBuffer(byteArrayListToByteArrayArray(samples), dictionary, false, 19);
    if (Zstd.isError(code)) throw new RuntimeException();
    return dictionary;
  }

  public static byte[][] byteArrayListToByteArrayArray(List<byte[]> byteArrayList) {
    int size = byteArrayList.size();
    byte[][] byteArrayArray = new byte[size][];

    for (int i = 0; i < size; i++) {
      byteArrayArray[i] = byteArrayList.get(i);
    }

    return byteArrayArray;
  }
}
