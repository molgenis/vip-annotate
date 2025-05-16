package org.molgenis.vcf.annotate.util;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class AnnotationDbZipArchive implements AutoCloseable {
  private final FileChannel annotationsZipFileChannel;
  private final ZipFile zipFile;

  public AnnotationDbZipArchive(Path annotationsZip) throws IOException {
    this.annotationsZipFileChannel =
        FileChannel.open(annotationsZip, StandardOpenOption.READ, ExtendedOpenOption.DIRECT);
    this.zipFile = ZipFile.builder().setSeekableByteChannel(annotationsZipFileChannel).get();
  }

  public AnnotationDbZipDecompressCtx createDecompressCtx(int capacity) {
    return new AnnotationDbZipDecompressCtx(annotationsZipFileChannel, zipFile, capacity);
  }

  @Override
  public void close() throws Exception {
    this.zipFile.close();
  }
}
