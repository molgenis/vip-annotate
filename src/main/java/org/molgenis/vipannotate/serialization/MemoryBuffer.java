package org.molgenis.vipannotate.serialization;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

public final class MemoryBuffer implements AutoCloseable {
  private static final ValueLayout.OfByte LAYOUT_BYTE;
  private static final VarHandle LAYOUT_BYTE_VAR_HANDLE;
  private static final ValueLayout.OfShort LAYOUT_SHORT;
  private static final VarHandle LAYOUT_SHORT_VAR_HANDLE;
  private static final ValueLayout.OfInt LAYOUT_INT;
  private static final VarHandle LAYOUT_INT_VAR_HANDLE;

  static {
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    //noinspection DataFlowIssue
    LAYOUT_BYTE = ValueLayout.JAVA_BYTE.withOrder(byteOrder);
    //noinspection DataFlowIssue
    LAYOUT_BYTE_VAR_HANDLE = LAYOUT_BYTE.varHandle();
    //noinspection DataFlowIssue
    LAYOUT_SHORT = ValueLayout.JAVA_SHORT_UNALIGNED.withOrder(byteOrder);
    //noinspection DataFlowIssue
    LAYOUT_SHORT_VAR_HANDLE = LAYOUT_SHORT.varHandle();
    //noinspection DataFlowIssue
    LAYOUT_INT = ValueLayout.JAVA_INT_UNALIGNED.withOrder(byteOrder);
    //noinspection DataFlowIssue
    LAYOUT_INT_VAR_HANDLE = LAYOUT_INT.varHandle();
  }

  public static final int VAR_INT_MAX_BYTE_SIZE = 5;

  private MemorySegment memorySegment;
  @Nullable private Arena arena;
  ///  the position in bytes (relative to the memory segment address)
  @Getter @Setter private long position = 0;

  private MemoryBuffer(MemorySegment memorySegment) {
    this(memorySegment, null);
  }

  private MemoryBuffer(MemorySegment memorySegment, @Nullable Arena arena) {
    this.memorySegment = memorySegment;
    this.arena = arena;
  }

  @SuppressWarnings("DataFlowIssue")
  public void ensureCapacity(long byteSize) {
    long currentByteSize = memorySegment.byteSize();
    if (currentByteSize < byteSize) {
      if (arena == null) {
        throw new UncheckedIOException(
            new IOException("cannot resize memory buffer with wrapped data"));
      }

      // find next power of two for requested byte size
      long newByteSize =
          (byteSize & (byteSize - 1)) == 0 ? byteSize : Long.highestOneBit(byteSize - 1) << 1;
      Arena newArena = Arena.ofConfined();
      MemorySegment newMemorySegment = newArena.allocate(newByteSize);
      newMemorySegment.copyFrom(memorySegment);
      close(); // deallocates current memory segment
      memorySegment = newMemorySegment;
      arena = newArena;
    }
  }

  public byte getByte() {
    byte value = memorySegment.get(LAYOUT_BYTE, position);
    position += LAYOUT_BYTE.byteSize();
    return value;
  }

  public byte getByteAtIndex(long index) {
    return memorySegment.getAtIndex(LAYOUT_BYTE, index);
  }

  public void putByte(byte value) {
    ensureCapacity(position + LAYOUT_BYTE.byteSize());
    putByteUnchecked(value);
  }

  public void putByteUnchecked(byte value) {
    memorySegment.set(LAYOUT_BYTE, position, value);
    position += LAYOUT_BYTE.byteSize();
  }

  public void setByteAtIndex(long index, byte value) {
    ensureCapacity(index + LAYOUT_BYTE.byteSize());
    setByteAtIndexUnchecked(index, value);
  }

  public void setByteAtIndexUnchecked(long index, byte value) {
    memorySegment.setAtIndex(LAYOUT_BYTE, index, value);
  }

  public byte[] getByteArray() {
    int arrayLength = getVarUnsignedInt();
    byte[] array = new byte[arrayLength];
    MemorySegment.copy(memorySegment, LAYOUT_BYTE, position, array, 0, arrayLength);
    position += arrayLength * LAYOUT_BYTE.byteSize();
    return array;
  }

  public void putByteArray(byte[] array) {
    ensureCapacity(position + VAR_INT_MAX_BYTE_SIZE + (array.length * LAYOUT_BYTE.byteSize()));
    putByteArrayUnchecked(array);
  }

  public void putByteArrayUnchecked(byte[] array) {
    putVarUnsignedIntUnchecked(array.length);
    MemorySegment.copy(array, 0, memorySegment, LAYOUT_BYTE, position, array.length);
    position += array.length * LAYOUT_BYTE.byteSize();
  }

  public short getShort() {
    short value = memorySegment.get(LAYOUT_SHORT, position);
    position += LAYOUT_SHORT.byteSize();
    return value;
  }

  public short getShortAtIndex(long index) {
    return memorySegment.getAtIndex(LAYOUT_SHORT, index);
  }

  public void putShort(short value) {
    ensureCapacity(position + LAYOUT_SHORT.byteSize());
    putShortUnchecked(value);
  }

  public void putShortUnchecked(short value) {
    memorySegment.set(LAYOUT_SHORT, position, value);
    position += LAYOUT_SHORT.byteSize();
  }

  public void setShortAtIndex(long index, short value) {
    ensureCapacity(index + LAYOUT_SHORT.byteSize());
    setShortAtIndexUnchecked(index, value);
  }

  public void setShortAtIndexUnchecked(long index, short value) {
    memorySegment.setAtIndex(LAYOUT_SHORT, index, value);
  }

  public int getInt() {
    int value = memorySegment.get(LAYOUT_INT, position);
    position += LAYOUT_INT.byteSize();
    return value;
  }

  public int getIntAtIndex(long index) {
    return memorySegment.getAtIndex(LAYOUT_INT, index);
  }

  public void putInt(int value) {
    ensureCapacity(position + LAYOUT_INT.byteSize());
    putIntUnchecked(value);
  }

  public void putIntUnchecked(int value) {
    memorySegment.set(LAYOUT_INT, position, value);
    position += LAYOUT_INT.byteSize();
  }

  public void setIntAtIndex(long index, int value) {
    ensureCapacity(index + LAYOUT_INT.byteSize());
    setIntAtIndexUnchecked(index, value);
  }

  public void setIntAtIndexUnchecked(long index, int value) {
    memorySegment.setAtIndex(LAYOUT_INT, index, value);
  }

  public int[] getIntArray() {
    int arrayLength = getVarUnsignedInt();
    int[] array = new int[arrayLength];
    MemorySegment.copy(memorySegment, LAYOUT_INT, position, array, 0, arrayLength);
    position += arrayLength * LAYOUT_INT.byteSize();
    return array;
  }

  public void putIntArray(int[] array) {
    ensureCapacity(position + VAR_INT_MAX_BYTE_SIZE + (array.length * LAYOUT_INT.byteSize()));
    putIntArrayUnchecked(array);
  }

  public void putIntArrayUnchecked(int[] array) {
    putVarUnsignedIntUnchecked(array.length);
    MemorySegment.copy(array, 0, memorySegment, LAYOUT_INT, position, array.length);
    position += array.length * LAYOUT_INT.byteSize();
  }

  /** Writes a 1-5 byte int. */
  public void putVarUnsignedInt(int value) {
    ensureCapacity(position + VAR_INT_MAX_BYTE_SIZE);
    putVarUnsignedIntUnchecked(value);
  }

  public void putVarUnsignedIntUnchecked(int value) {
    // derived from
    // https://github.com/apache/fory/blob/v0.12.3/java/fory-core/src/main/java/org/apache/fory/memory/MemoryBuffer.java#L772
    long index = position;

    // fast path: fits in 7 bits
    if ((value >>> 7) == 0) {
      LAYOUT_BYTE_VAR_HANDLE.set(memorySegment, index, (byte) value);
      position = index + 1;
      return;
    }

    // fits in 14 bits: write short
    long encoded = (value & 0x7F) | (((value & 0x3F80) << 1) | 0x80);
    if ((value >>> 14) == 0) {
      LAYOUT_SHORT_VAR_HANDLE.set(memorySegment, index, (short) encoded);
      position = index + 2;
      return;
    }

    // fits in 21 bits: write lower 2-bytes as short and upper 1-byte as byte
    encoded |= (((value & 0x1FC000) << 2) | 0x8000);
    if ((value >>> 21) == 0) {
      LAYOUT_SHORT_VAR_HANDLE.set(memorySegment, index, (short) (encoded & 0xFFFF));
      LAYOUT_BYTE_VAR_HANDLE.set(memorySegment, index + 2, (byte) ((encoded >>> 16) & 0xFF));
      position = index + 3;
      return;
    }

    // fits in 28 bits: write int
    encoded |= ((value & 0xFE00000L) << 3) | 0x800000L;
    if ((value >>> 28) == 0) {
      LAYOUT_INT_VAR_HANDLE.set(memorySegment, index, (int) encoded);
      position = index + 4;
      return;
    }

    // fits in 32 bits: write lower 4-bytes as int and upper 1-byte as byte
    encoded |= ((value & 0xFF0000000L) << 4) | 0x80000000L;
    LAYOUT_INT_VAR_HANDLE.set(memorySegment, index, (int) (encoded & 0xFFFFFFFFL));
    LAYOUT_BYTE_VAR_HANDLE.set(memorySegment, index + 4, (byte) ((encoded >>> 32) & 0xFF));
    position = index + 5;
  }

  /**
   * Reads a 1–5 byte unsigned integer written with {@link #putVarUnsignedIntUnchecked}
   *
   * @return decoded integer
   */
  @SuppressWarnings("DataFlowIssue")
  public int getVarUnsignedInt() {
    long index = position;
    int b0 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memorySegment, index++) & 0xFF;

    if ((b0 & 0x80) == 0) {
      position = index;
      return b0;
    }

    int b1 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memorySegment, index++) & 0xFF;
    int result = (b0 & 0x7F) | ((b1 & 0x7F) << 7);
    if ((b1 & 0x80) == 0) {
      position = index;
      return result;
    }

    int b2 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memorySegment, index++) & 0xFF;
    result |= (b2 & 0x7F) << 14;
    if ((b2 & 0x80) == 0) {
      position = index;
      return result;
    }

    int b3 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memorySegment, index++) & 0xFF;
    result |= (b3 & 0x7F) << 21;
    if ((b3 & 0x80) == 0) {
      position = index;
      return result;
    }

    int b4 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memorySegment, index++) & 0xFF;
    result |= (b4 & 0x0F) << 28;
    position = index;
    return result;
  }

  public void rewind() {
    position = 0;
  }

  public MemorySegment asMemorySegment() {
    //noinspection DataFlowIssue
    return memorySegment.asSlice(0L, position);
  }

  public MemorySegment asMemorySegment(long offset) {
    //noinspection DataFlowIssue
    return memorySegment.asSlice(offset);
  }

  public MemorySegment asMemorySegment(long offset, long length) {
    //noinspection DataFlowIssue
    return memorySegment.asSlice(offset, length);
  }

  @SuppressWarnings("DataFlowIssue")
  public static MemoryBuffer allocate(long byteSize) {
    Arena arena = Arena.ofConfined();
    return new MemoryBuffer(arena.allocate(byteSize), arena);
  }

  /**
   * Wraps an existing {@link MemorySegment} in a {@code MemoryBuffer}. The buffer does not take
   * ownership of the memory; closing it does not deallocate the underlying segment. The buffer
   * can't be resized.
   *
   * @param memorySegment the memory segment to wrap
   * @return a new {@code MemoryBuffer} view of the given segment
   */
  public static MemoryBuffer wrap(MemorySegment memorySegment) {
    return new MemoryBuffer(memorySegment);
  }

  /**
   * Wraps an existing {@code byte[]} array in a {@code MemoryBuffer}. The buffer provides a view of
   * the array's contents; modifications are reflected in both.
   *
   * @param array the array to wrap
   * @return a new {@code MemoryBuffer} view of the given array
   */
  public static MemoryBuffer wrap(byte[] array) {
    return new MemoryBuffer(MemorySegment.ofArray(array));
  }

  /** Same as above for {@code short[]} arrays. */
  public static MemoryBuffer wrap(short[] array) {
    return new MemoryBuffer(MemorySegment.ofArray(array));
  }

  /** Same as above for {@code int[]} arrays. */
  public static MemoryBuffer wrap(int[] array) {
    return new MemoryBuffer(MemorySegment.ofArray(array));
  }

  @Override
  public void close() {
    if (arena != null) {
      arena.close();
    }
  }
}
