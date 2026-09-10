package org.molgenis.vipannotate.serialization;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryReaderTest {
  @Mock private MemoryBuffer memBuffer;
  private BinaryReader binaryReader;

  @BeforeEach
  void setUp() {
    binaryReader = new BinaryReader(memBuffer);
  }

  @Test
  void readArray() {
    when(memBuffer.getVarUnsignedInt()).thenReturn(3);
    when(memBuffer.getInt()).thenReturn(0).thenReturn(1).thenReturn(2);
    assertArrayEquals(
        new Integer[] {0, 1, 2}, binaryReader.readArray(Integer[]::new, BinaryReader::readInteger));
  }

  @Test
  void close() {
    binaryReader.close();
    verify(memBuffer).close();
  }
}
