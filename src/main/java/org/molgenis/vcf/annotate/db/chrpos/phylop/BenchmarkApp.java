package org.molgenis.vcf.annotate.db.chrpos.phylop;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.Zstd;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import me.lemire.integercompression.IntCompressor;
import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.molgenis.vcf.annotate.db.chrpos.ContigPosAnnotation;

public class BenchmarkApp {
  public static void main(String[] args) {
    File phylopFile = new File(args[0]);

    List<Double> doubles = new ArrayList<>();
    List<Integer> integers = new ArrayList<>();
    String chr = null;
    Integer partitionId = null;
    try (BufferedReader bufferedReader = createReader(phylopFile)) {
      PhyloPVariantIterator phylopVariantIterator = new PhyloPVariantIterator(bufferedReader);
      while (phylopVariantIterator.hasNext()) {
        ContigPosAnnotation contigPosAnnotation = phylopVariantIterator.next();
        String newChr = contigPosAnnotation.contig();
        if (chr == null) chr = newChr;
        if (!chr.equals(newChr)) break;

        int newPartitionId = contigPosAnnotation.pos() >> 20;
        if (partitionId == null) partitionId = newPartitionId;
        if (partitionId != newPartitionId) break;

        String score = contigPosAnnotation.score();
        doubles.add(Double.parseDouble(score));
        int encodedScore = encodeScore(score);
        integers.add(encodedScore);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    int[] intArray = integers.stream().mapToInt(Integer::intValue).toArray();
    short[] shortArray = new short[intArray.length];
    for (int i = 0; i < intArray.length; i++) {
      shortArray[i] = (short) intArray[i];
    }

    Double min = Collections.min(doubles);
    System.out.println("min:" + min);
    System.out.println("max:" + Collections.max(doubles));
    Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
    byte[] serializedIntArrayBytes = fury.serialize(intArray);
    System.out.println("number of integers    : " + integers.size());
    System.out.println("expected number  bytes: " + integers.size() * 4);
    System.out.println("uncompressed      size: " + serializedIntArrayBytes.length);
    byte[] serializedIntArrayBytesZstd = Zstd.compress(serializedIntArrayBytes, 19);
    System.out.println("uncompressed zstd size: " + serializedIntArrayBytesZstd.length);

    int[] intArrayCompressed = new IntCompressor().compress(intArray);
    byte[] serializedIntArrayCompressedBytes = fury.serialize(intArrayCompressed);
    System.out.println("  compressed      size: " + serializedIntArrayCompressedBytes.length);
    byte[] serializedIntArrayCompressedBytesZstd =
        Zstd.compress(serializedIntArrayCompressedBytes, 19);
    System.out.println("  compressed zstd size: " + serializedIntArrayCompressedBytesZstd.length);

    int[] deltaIntArray = deltaEncode(intArray);
    byte[] serializedDeltaIntArrayBytes = fury.serialize(deltaIntArray);
    byte[] serializedDeltaIntArrayBytesZstd = Zstd.compress(serializedDeltaIntArrayBytes, 19);
    System.out.println("uncomp delta      size: " + serializedDeltaIntArrayBytes.length);
    System.out.println("uncomp delta zstd size: " + serializedDeltaIntArrayBytesZstd.length);
    {
      byte[] serializedShortArrayBytes = fury.serialize(shortArray);
      System.out.println("uncompressed shrt size: " + serializedShortArrayBytes.length);
      byte[] serializedShortArrayBytesZstd = Zstd.compress(serializedShortArrayBytes, 19);
      System.out.println("uncompressed shrt zstd size: " + serializedShortArrayBytesZstd.length);
    }
    short[] deltaShortArray = new short[deltaIntArray.length];
    for (int i = 0; i < deltaIntArray.length; i++) {
      deltaShortArray[i] = (short) deltaIntArray[i];
    }
    byte[] serializedDeltaShortArrayBytes = fury.serialize(deltaShortArray);
    System.out.println("uncompressed delta shrt size: " + serializedDeltaShortArrayBytes.length);
    byte[] serializedDeltaShortArrayBytesZstd = Zstd.compress(serializedDeltaShortArrayBytes, 19);
    System.out.println(
        "uncompressed delta shrt zstd size: " + serializedDeltaShortArrayBytesZstd.length);

    System.out.println("make all numbers unsigned");
    {
      short[] shortArraySigned = new short[shortArray.length];
      for (int i = 0; i < shortArray.length; i++) {
        shortArraySigned[i] = (short) (shortArray[i] - -12);
      }
      {
        byte[] serializedShortArrayBytes = fury.serialize(shortArraySigned);
        System.out.println("uncompressed unsigned shrt size: " + serializedShortArrayBytes.length);
        byte[] serializedShortArrayBytesZstd = Zstd.compress(serializedShortArrayBytes, 19);
        System.out.println(
            "uncompressed unsigned shrt zstd size: " + serializedShortArrayBytesZstd.length);
      }
    }

    System.out.println("make all numbers unsigned from double");
    {
      short[] shortArraySigned = new short[shortArray.length];
      for (int i = 0; i < doubles.size(); i++) {
        double val = doubles.get(i);
        double valUnsigned = val - min;
        short valShort =
            (short) (safeCastDoubleToShort(valUnsigned * 1000) + 1); // reserve 0 for missing values
        if (valShort <= 0) {
          throw new IllegalArgumentException();
        }
        shortArraySigned[i] = valShort;

        double valDec = ((valShort - 1) / 1000d) + min;
        if (Math.abs(val - valDec) > 1e-3) {
          throw new IllegalArgumentException();
        }
      }
      {
        byte[] serializedShortArrayBytes = fury.serialize(shortArraySigned);
        System.out.println("uncompressed unsigned shrt size: " + serializedShortArrayBytes.length);
        byte[] serializedShortArrayBytesZstd = Zstd.compress(serializedShortArrayBytes, 19);
        System.out.println(
            "uncompressed unsigned shrt zstd size: " + serializedShortArrayBytesZstd.length);
      }
    }
  }

  public static short safeCastDoubleToShort(double value) {
    if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
      throw new ArithmeticException("Value out of short range: " + value);
    }
    return (short) value;
  }

  public static int[] deltaEncode(int[] input) {
    if (input == null || input.length == 0) return new int[0];

    int[] delta = new int[input.length];
    delta[0] = input[0]; // first element stays as-is
    for (int i = 1; i < input.length; i++) {
      delta[i] = input[i] - input[i - 1];
    }
    return delta;
  }

  private static int encodeScore(String score) {
    return Integer.parseInt(score.substring(0, score.length() - 3).replaceAll("\\.", ""));
  }

  private static BufferedReader createReader(File phyloPFile) throws IOException {
    return new BufferedReader(
        new InputStreamReader(
            new GZIPInputStream(new FileInputStream(phyloPFile)), StandardCharsets.UTF_8),
        1048576);
  }

  private static class PhyloPVariantIterator implements Iterator<ContigPosAnnotation> {
    private final BufferedReader bufferedReader;

    public PhyloPVariantIterator(BufferedReader bufferedReader) {
      this.bufferedReader = requireNonNull(bufferedReader);
    }

    String line = null;

    @Override
    public boolean hasNext() {
      try {
        line = bufferedReader.readLine();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      return line != null;
    }

    @Override
    public ContigPosAnnotation next() {
      String[] tokens = line.split("\t", -1);
      String contig = tokens[0];
      int pos = Integer.parseInt(tokens[1]) + 1;
      if (Integer.parseInt(tokens[2]) != pos) throw new RuntimeException();
      String score = tokens[3];
      return new ContigPosAnnotation(contig, pos, score);
    }
  }
}
