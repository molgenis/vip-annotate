package org.molgenis.vipannotate.format.zip;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.jspecify.annotations.Nullable;

/** {@link ZipFile} that can map a {@link ZipArchiveEntry} directly into memory. */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MappableZipFile implements AutoCloseable {
  private final FileChannel fileChannel;
  private final ZipFile zipFile;

  public @Nullable ZipArchiveEntry getEntry(String entryName) {
    return this.zipFile.getEntry(entryName);
  }

  public Iterator<ZipArchiveEntry> getEntries() {
    return this.zipFile.getEntries().asIterator();
  }

  public MappedByteBuffer map(ZipArchiveEntry zipArchiveEntry) {
    long dataOffset = zipArchiveEntry.getDataOffset();
    long compressedSize = zipArchiveEntry.getCompressedSize();
    try {
      return this.fileChannel.map(FileChannel.MapMode.READ_ONLY, dataOffset, compressedSize);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static MappableZipFile fromFile(Path path) {
    FileChannel fileChannel;
    ZipFile zipFile;
    try {
      // adding ExtendedOpenOption.DIRECT throws exception:
      // Channel position (164368795) is not a multiple of the block size (512)
      fileChannel = FileChannel.open(path, StandardOpenOption.READ);
      zipFile = ZipFile.builder().setSeekableByteChannel(fileChannel).get();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return new MappableZipFile(fileChannel, zipFile);
  }

  @Override
  public void close() {
    try {
      this.zipFile.close(); // closes fileChannel as well
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
