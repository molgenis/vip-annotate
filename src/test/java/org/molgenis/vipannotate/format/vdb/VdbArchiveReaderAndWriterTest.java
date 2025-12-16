package org.molgenis.vipannotate.format.vdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@SuppressWarnings("DataFlowIssue")
class VdbArchiveReaderAndWriterTest {
  private static VdbArchiveWriterFactory archiveWriterFactory;
  private static VdbArchiveReaderFactory archiveReaderFactory;

  @BeforeAll
  static void beforeAll() {
    archiveWriterFactory = VdbArchiveWriterFactory.create();
    archiveReaderFactory = VdbArchiveReaderFactory.create();
  }

  @Test
  void testWriteAndRead() throws IOException, NoSuchAlgorithmException {
    Path vdbPath = Files.createTempFile("test", ".vdb");
    Files.delete(vdbPath); // we just need a temp path

    try {
      List<Integer> entityIds = new ArrayList<>();
      try (VdbArchiveWriter archiveWriter = archiveWriterFactory.create(vdbPath)) {
        MemoryBuffer memBuffer0 = MemoryBuffer.wrap(new byte[] {0, 1, 2, 3});
        memBuffer0.setPosition(4);
        entityIds.add(archiveWriter.createEntry(memBuffer0));

        MemoryBuffer memBuffer1 = MemoryBuffer.wrap(new byte[] {0, 1, 2, 3});
        memBuffer1.setPosition(4);
        entityIds.add(archiveWriter.createEntry(memBuffer1, Compression.ZSTD));

        MemoryBuffer memBuffer2 = MemoryBuffer.wrap(new byte[] {1, 2, 3, 4});
        memBuffer2.setPosition(4);
        entityIds.add(archiveWriter.createEntry(memBuffer2, Compression.ZSTD, IoMode.BUFFERED));

        MemoryBuffer memBuffer3 = MemoryBuffer.wrap(new byte[] {2, 3, 4, 5});
        memBuffer3.setPosition(4);
        entityIds.add(archiveWriter.createEntry(memBuffer3, Compression.PLAIN, IoMode.BUFFERED));

        MemoryBuffer memBuffer4 = MemoryBuffer.wrap(new byte[] {3, 4, 5, 6});
        memBuffer4.setPosition(4);
        entityIds.add(archiveWriter.createEntry(memBuffer4, Compression.PLAIN, IoMode.DIRECT));
      }

      // assert that database is byte-reproducible
      String sha256 =
          HexFormat.of()
              .formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(vdbPath)));
      assertEquals("5eef9bd090132c8640d4a3a22c2dc3552b56895c4f53bd117f6e48f7bf20aa13", sha256);

      try (VdbArchiveReader archiveReader = archiveReaderFactory.create(vdbPath)) {
        for (int i = 0; i < 2; ++i) {
          try (MemoryBuffer memBuffer = archiveReader.readEntry(entityIds.get(i))) {
            memBuffer.flip();

            assertEquals(0, memBuffer.getByte());
            assertEquals(1, memBuffer.getByte());
            assertEquals(2, memBuffer.getByte());
            assertEquals(3, memBuffer.getByte());
          }
        }

        try (MemoryBuffer memBuffer = archiveReader.readEntry(2)) {
          // 2
          memBuffer.flip();

          assertEquals(1, memBuffer.getByte());
          assertEquals(2, memBuffer.getByte());
          assertEquals(3, memBuffer.getByte());
          assertEquals(4, memBuffer.getByte());

          // 3
          archiveReader.readEntryInto(3, memBuffer);
          memBuffer.flip();

          assertEquals(2, memBuffer.getByte());
          assertEquals(3, memBuffer.getByte());
          assertEquals(4, memBuffer.getByte());
          assertEquals(5, memBuffer.getByte());

          // 4
          archiveReader.readLastEntryInto(memBuffer);
          memBuffer.flip();

          assertEquals(3, memBuffer.getByte());
          assertEquals(4, memBuffer.getByte());
          assertEquals(5, memBuffer.getByte());
          assertEquals(6, memBuffer.getByte());
        }
      }
    } finally {
      Files.delete(vdbPath);
    }
  }
}
