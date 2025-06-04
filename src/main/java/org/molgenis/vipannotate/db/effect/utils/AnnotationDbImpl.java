package org.molgenis.vipannotate.db.effect.utils;

import com.github.luben.zstd.RecyclingBufferPool;
import com.github.luben.zstd.ZstdInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.fury.Fury;
import org.apache.fury.io.FuryInputStream;
import org.molgenis.vipannotate.db.effect.model.FuryFactory;

public class AnnotationDbImpl {
  private final ZipFile zipFile;
  private final Fury fury;

  private String currentContigId;
  private FuryFactory.AnnotationDb currentAnnotationDb;

  public AnnotationDbImpl(Path annotationsZip) {
    try {
      this.zipFile = ZipFile.builder().setPath(annotationsZip).get();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    this.fury = FuryFactory.createFury();
  }

  public FuryFactory.AnnotationDb get(FuryFactory.Chromosome chromosome) {
    String contig = chromosome.getId();
    if (!contig.equals(currentContigId)) {
      currentContigId = contig;

      int partitionId = 0;
      ZipArchiveEntry entry = zipFile.getEntry(contig + "/ref/" + partitionId + ".zst");
      if (entry == null) { // no annotations exist for this partition
        currentAnnotationDb = null;
      } else {
        // perf: zipFile.getInputStream creates a buffered stream, but FuryInputStream is already
        // buffered
        // perf: ZstdCompressorInputStream collects unnecessary InputStreamStatistics, use
        // ZstdInputStream instead
        try (InputStream inputStream =
            new ZstdInputStream(zipFile.getRawInputStream(entry), RecyclingBufferPool.INSTANCE)) {
          currentAnnotationDb = readDatabase(inputStream, entry.getSize());
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }
    return currentAnnotationDb;
  }

  private FuryFactory.AnnotationDb readDatabase(InputStream inputStream, long size)
      throws IOException {
    try (FuryInputStream furyInputStream =
        new FuryInputStream(inputStream, Math.toIntExact(size))) {
      return fury.deserializeJavaObject(furyInputStream, FuryFactory.AnnotationDb.class);
    }
  }
}
