package org.molgenis.vipannotate;

import com.github.luben.zstd.ZstdDecompressCtx;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
public class ZstdDecompressBenchmark {
  @Param({})
  public String annotationsZipPathString;

  @Benchmark
  public void benchmarkDecompress() throws IOException {
    Path annotationsZipPath = Paths.get(annotationsZipPathString);

    List<ZipArchiveEntry> zipArchiveEntries;
    try (ZipFile zipFile = ZipFile.builder().setPath(annotationsZipPath).get()) {
      zipArchiveEntries = Collections.list(zipFile.getEntries());
    }

    long maxZipArchiveEntrySize = -1;
    for (ZipArchiveEntry zipArchiveEntry : zipArchiveEntries) {
      long zipArchiveEntrySize = zipArchiveEntry.getSize();
      if (zipArchiveEntrySize > maxZipArchiveEntrySize) {
        maxZipArchiveEntrySize = zipArchiveEntrySize;
      }
    }

    ByteBuffer dstByteBuffer = ByteBuffer.allocateDirect(Math.toIntExact(maxZipArchiveEntrySize));

    try (FileChannel fileChannel =
        FileChannel.open(annotationsZipPath, StandardOpenOption.READ)) {
      long start = System.currentTimeMillis();
      try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx()) {
        for (ZipArchiveEntry zipArchiveEntry : zipArchiveEntries) {
          // perf: use ExtendedMapMode.READ_ONLY_SYNC if available on machine?
          ByteBuffer srcByteBuffer =
              fileChannel.map(
                  FileChannel.MapMode.READ_ONLY,
                  zipArchiveEntry.getDataOffset(),
                  zipArchiveEntry.getCompressedSize());
          zstdDecompressCtx.decompress(dstByteBuffer, srcByteBuffer);
          dstByteBuffer.clear();
        }
      }
      long end = System.currentTimeMillis();

      long nrBytesAnnotationsZip = Files.size(annotationsZipPath);
      long durationMs = end - start;
      System.out.printf(
          "decompressed %d bytes in %dms = %dMB/sec\n",
          nrBytesAnnotationsZip,
          durationMs,
          Math.round((nrBytesAnnotationsZip / (1024d * 1024d)) / (durationMs / 1000d)));
    }
  }

  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder()
            .include(ZstdDecompressBenchmark.class.getSimpleName())
            .param("annotationsZipPathString", args[0])
            .build();
    new Runner(opt).run();
  }
}
