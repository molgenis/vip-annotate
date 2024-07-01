package org.molgenis.vipannotate.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
public class BgzipDecompressBenchmark {
  @Param({})
  public String bgzipPathString;

  @Param({"16384", "32768", "65536"})
  public int bufferedReaderBufferSize;

  @Param({"16384", "32768", "65536"})
  public int inputStreamReaderBufferSize;

  @Benchmark
  public void benchmarkDecompressJdk() throws IOException {
    Path annotationsZipPath = Paths.get(bgzipPathString);

    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(
                    Files.newInputStream(annotationsZipPath), inputStreamReaderBufferSize)),
            bufferedReaderBufferSize)) {
      while (br.readLine() != null) {
        /* consume lines */
      }
    }
  }

  @Benchmark
  public void benchmarkDecompressApacheCompress() throws IOException {
    Path annotationsZipPath = Paths.get(bgzipPathString);

    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                new GzipCompressorInputStream(Files.newInputStream(annotationsZipPath), true)),
            inputStreamReaderBufferSize)) {
      while (br.readLine() != null) {
        /* consume lines */
      }
    }
  }

  /**
   * Benchmark (bgzipPathString) (bufferedReaderBufferSize) (inputStreamReaderBufferSize) Mode Cnt
   * Score Error Units BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 16384 16384 avgt 2 468,057 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 16384 32768 avgt 2 490,195 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 16384 65536 avgt 2 407,553 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 32768 16384 avgt 2 333,996 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 32768 32768 avgt 2 335,183 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 32768 65536 avgt 2 337,385 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 65536 16384 avgt 2 332,267 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 65536 32768 avgt 2 352,316 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressApacheCompress
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 65536 65536 avgt 2 347,885 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressJdk HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz
   * 16384 16384 avgt 2 331,781 ms/op BgzipDecompressBenchmark.benchmarkDecompressJdk
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 16384 32768 avgt 2 322,282 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressJdk HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz
   * 16384 65536 avgt 2 321,927 ms/op BgzipDecompressBenchmark.benchmarkDecompressJdk
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 32768 16384 avgt 2 328,531 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressJdk HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz
   * 32768 32768 avgt 2 320,139 ms/op BgzipDecompressBenchmark.benchmarkDecompressJdk
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 32768 65536 avgt 2 320,695 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressJdk HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz
   * 65536 16384 avgt 2 322,303 ms/op BgzipDecompressBenchmark.benchmarkDecompressJdk
   * HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz 65536 32768 avgt 2 325,695 ms/op
   * BgzipDecompressBenchmark.benchmarkDecompressJdk HG002_GRCh38_1_22_v4.2.1_benchmark_chr1.vcf.gz
   * 65536 65536 avgt 2 324,805 ms/op
   */
  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder()
            .include(BgzipDecompressBenchmark.class.getSimpleName())
            .param("bgzipPathString", args[0])
            .build();
    new Runner(opt).run();
  }
}
