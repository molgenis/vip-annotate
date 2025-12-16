package org.molgenis.streamvbyte;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.foreign.MemorySegment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class StreamVByteTest {
  private static StreamVByte streamVByte;

  @BeforeAll
  static void setUp() {
    streamVByte = StreamVByte.create();
  }

  @AfterAll
  static void tearDown() {
    streamVByte.close();
  }

  @Test
  void encodeAndDecode() {
    int[] srcIntArray = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2046, 4096, 8192, 16384};
    MemorySegment srcMemorySegment = MemorySegment.ofArray(srcIntArray);
    long maxNrEncodedBytes = streamVByte.maxCompressedBytes(srcIntArray.length);
    MemorySegment encodedMemorySegment =
        MemorySegment.ofArray(new byte[Math.toIntExact(maxNrEncodedBytes)]);
    long nrBytesWritten =
        streamVByte.encode(srcMemorySegment, encodedMemorySegment, srcIntArray.length);
    int[] decodedIntArray = new int[srcIntArray.length];
    MemorySegment decodedMemorySegment = MemorySegment.ofArray(decodedIntArray);
    long nrBytesRead =
        streamVByte.decode(encodedMemorySegment, decodedMemorySegment, srcIntArray.length);
    assertAll(
        () -> assertEquals(26, nrBytesWritten),
        () -> assertEquals(26, nrBytesRead),
        () -> assertArrayEquals(decodedIntArray, srcIntArray));
  }

  @Test
  void deltaEncodeAndDeltaDecode() {
    int[] srcIntArray = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2046, 4096, 8192, 16384};
    MemorySegment srcMemorySegment = MemorySegment.ofArray(srcIntArray);
    long maxNrEncodedBytes = streamVByte.maxCompressedBytes(srcIntArray.length);
    MemorySegment encodedMemorySegment =
        MemorySegment.ofArray(new byte[Math.toIntExact(maxNrEncodedBytes)]);
    long nrBytesWritten =
        streamVByte.deltaEncode(srcMemorySegment, encodedMemorySegment, srcIntArray.length);
    int[] decodedIntArray = new int[srcIntArray.length];
    MemorySegment decodedMemorySegment = MemorySegment.ofArray(decodedIntArray);
    long nrBytesRead =
        streamVByte.deltaDecode(encodedMemorySegment, decodedMemorySegment, srcIntArray.length);
    assertAll(
        () -> assertEquals(25, nrBytesWritten),
        () -> assertEquals(25, nrBytesRead),
        () -> assertArrayEquals(decodedIntArray, srcIntArray));
  }
}
