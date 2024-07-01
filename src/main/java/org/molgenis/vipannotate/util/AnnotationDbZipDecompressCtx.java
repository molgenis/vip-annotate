package org.molgenis.vipannotate.util;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.ZstdDecompressCtx;
import com.github.luben.zstd.ZstdDictDecompress;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class AnnotationDbZipDecompressCtx implements AutoCloseable {
  private final ZstdDecompressCtx zstdDecompressCtx;
  private final FileChannel annotationsZipFileChannel;
  private final ZipFile zipFile;
  private ByteBuffer directByteBuffer;

  public AnnotationDbZipDecompressCtx(
      FileChannel annotationsZipFileChannel, ZipFile zipFile, int capacity) {
    this.annotationsZipFileChannel = requireNonNull(annotationsZipFileChannel);
    this.zipFile = requireNonNull(zipFile);
    this.zstdDecompressCtx = new ZstdDecompressCtx();
    this.directByteBuffer = ByteBuffer.allocateDirect(capacity);
  }

  public void loadDictionary(String entryName) throws IOException {
    ZipArchiveEntry zipArchiveEntry = zipFile.getEntry(entryName);
    if (zipArchiveEntry == null) throw new RuntimeException();

    ByteBuffer srcByteBuffer =
        this.annotationsZipFileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            zipArchiveEntry.getDataOffset(),
            zipArchiveEntry.getCompressedSize());

    zstdDecompressCtx.loadDict(new ZstdDictDecompress(srcByteBuffer));
  }

  public ByteBuffer decompress(String entryName) throws IOException {
    ZipArchiveEntry zipArchiveEntry = zipFile.getEntry(entryName);
    if (zipArchiveEntry == null) return null;

    int compressedSize = Math.toIntExact(zipArchiveEntry.getCompressedSize());
    int uncompressedSize = Math.toIntExact(zipArchiveEntry.getSize());

    directByteBuffer.clear();
    ByteBuffer srcByteBuffer =
        this.annotationsZipFileChannel.map(
            FileChannel.MapMode.READ_ONLY, zipArchiveEntry.getDataOffset(), compressedSize);
    zstdDecompressCtx.decompressDirectByteBuffer(
        directByteBuffer, 0, uncompressedSize, srcByteBuffer, 0, compressedSize);
    //noinspection UnusedAssignment
    srcByteBuffer = null; // mark for garbage collection
    directByteBuffer.position(0);
    directByteBuffer.limit(uncompressedSize);

    return directByteBuffer;
  }

  @Override
  public void close() {
    this.zstdDecompressCtx.close();
    this.directByteBuffer = null; // mark for garbage collection
  }
}
