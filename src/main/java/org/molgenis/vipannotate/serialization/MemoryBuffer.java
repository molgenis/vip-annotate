package org.molgenis.vipannotate.serialization;

import static org.molgenis.vipannotate.util.Numbers.nextPowerOf2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.Numbers;

/** Memory buffer with little endian byte order. */
public final class MemoryBuffer implements AutoCloseable {
  private static final ValueLayout.OfByte LAYOUT_BYTE;
  private static final VarHandle LAYOUT_BYTE_VAR_HANDLE;
  private static final ValueLayout.OfShort LAYOUT_SHORT;
  private static final VarHandle LAYOUT_SHORT_VAR_HANDLE;
  private static final ValueLayout.OfInt LAYOUT_INT;
  private static final VarHandle LAYOUT_INT_VAR_HANDLE;
  private static final ValueLayout.OfLong LAYOUT_LONG;
  public static final int VAR_INT_MAX_BYTE_SIZE = Integer.BYTES + 1;

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
    //noinspection DataFlowIssue
    LAYOUT_LONG = ValueLayout.JAVA_LONG_UNALIGNED.withOrder(byteOrder);
  }

  /** Underlying native or heap-based memory segment */
  private MemorySegment memSegment;

  /** {@link #memSegment} wrapped in a {@link ByteBuffer} */
  @Nullable private ByteBuffer byteBufferWrapper;

  /**
   * {@link Arena} that was used to allocate {@link #memSegment}.
   *
   * <p>{@code null} for heap-based segments
   */
  @Nullable private Arena arena;

  /**
   * alignment constraint (in bytes) of the native memory segment.
   *
   * <p>always one for heap-based segments
   */
  @Getter private final long alignment;

  /**
   * index of the next element to read or write. automatically updated on get/put operations.
   *
   * <p>0 <= position <= limit <= capacity
   */
  @Getter private long position;

  /**
   * the index (exclusive) that marks the end of the accessible data in the buffer.
   *
   * <p>0 <= position <= limit <= capacity
   */
  @Getter private long limit;

  private MemoryBuffer(MemorySegment memSegment) {
    this(memSegment, 1L, null);
  }

  private MemoryBuffer(MemorySegment memSegment, long alignment, @Nullable Arena arena) {
    this.memSegment = memSegment;
    this.alignment = alignment;
    this.arena = arena;

    this.position = 0;
    this.limit = getCapacity();
  }

  public void setPosition(long position) {
    Numbers.requireNonNegative(position);
    if (position > limit) {
      throw new IllegalArgumentException("invalid position: %d".formatted(position));
    }
    this.position = position;
  }

  public void setLimit(long limit) {
    Numbers.requireNonNegative(limit);
    if (limit < position || limit > getCapacity()) {
      throw new IllegalArgumentException("invalid limit: %d".formatted(limit));
    }
    this.limit = limit;
  }

  /** The total number of bytes the buffer can hold */
  public long getCapacity() {
    return memSegment.byteSize();
  }

  /**
   * ensures the native memory buffer has at least the given capacity.
   *
   * @throws UncheckedIOException if attempting to resize a heap-based memory buffer
   */
  public void ensureCapacity(long minCapacity) {
    if (minCapacity <= getCapacity()) {
      return;
    }

    if (arena == null) {
      throw new UncheckedIOException(new IOException("cannot resize heap-based memory buffer"));
    }

    // create new memory segment
    Arena newArena = Arena.ofConfined();
    long newCapacity = Math.max(alignment, nextPowerOf2(minCapacity));
    MemorySegment newMemorySegment = newArena.allocate(newCapacity, alignment);
    newMemorySegment.copyFrom(memSegment);

    // replace old memory segment
    close();
    memSegment = newMemorySegment;
    byteBufferWrapper = null;
    arena = newArena;
  }

  /** Rewinds this buffer: sets the position to zero. */
  public void rewind() {
    this.position = 0;
  }

  /** Clears this buffer: sets the position to zero and limit to capacity. */
  public void clear() {
    this.position = 0;
    this.limit = getCapacity();
  }

  /** Flips this buffer: sets the limit to the current position and the position to zero. */
  public void flip() {
    this.limit = position;
    this.position = 0;
  }

  /** Returns the byte at the current position and increments the position */
  public byte getByte() {
    byte value = memSegment.get(LAYOUT_BYTE, position);
    position += LAYOUT_BYTE.byteSize();
    return value;
  }

  /** Returns the byte at the given position */
  public int getByte(long pos) {
    return memSegment.get(LAYOUT_BYTE, pos);
  }

  /** Returns the byte at the given index */
  public byte getByteAtIndex(long index) {
    return memSegment.getAtIndex(LAYOUT_BYTE, index);
  }

  /**
   * Writes a byte at the current position.
   *
   * <p>grows capacity and extends limit if required.
   */
  public void putByte(byte value) {
    ensureCapacity(position + LAYOUT_BYTE.byteSize());
    putByteUnchecked(value);
    if (position > limit) {
      limit = position;
    }
  }

  /**
   * Writes a byte at the current position. Very fast. Use with care.
   *
   * <p>does not check whether it is possible to write the byte which could result in writing after
   * the limit.
   */
  public void putByteUnchecked(byte value) {
    memSegment.set(LAYOUT_BYTE, position, value);
    position += LAYOUT_BYTE.byteSize();
  }

  /**
   * Writes a byte at the given index.
   *
   * <p>grows capacity and extends limit if required.
   */
  public void setByteAtIndex(long index, byte value) {
    long minCapacity = (index * LAYOUT_BYTE.byteSize()) + LAYOUT_BYTE.byteSize();
    ensureCapacity(minCapacity);
    setByteAtIndexUnchecked(index, value);
    if (minCapacity > limit) {
      limit = minCapacity;
    }
  }

  /**
   * Writes a byte at the given index. Very fast. Use with care.
   *
   * <p>does not check whether it is possible to write the byte which could result in writing after
   * * the limit.
   */
  public void setByteAtIndexUnchecked(long index, byte value) {
    memSegment.setAtIndex(LAYOUT_BYTE, index, value);
  }

  /**
   * Writes the same byte x times starting from the current position.
   *
   * <p>grows capacity and extends limit if required.
   */
  public void putByte(byte value, long count) {
    ensureCapacity(position + (LAYOUT_BYTE.byteSize() * count));
    putByteUnchecked(value, count);
    if (position > limit) {
      limit = position;
    }
  }

  /**
   * same as {@link #putByteUnchecked(byte)} but writes a <code>byte</code> a given number of times.
   */
  public void putByteUnchecked(byte value, long count) {
    memSegment.asSlice(position, count).fill(value);
    position += LAYOUT_BYTE.byteSize() * count;
  }

  /** same as {@link #getByte()} for <code>byte[]</code>. */
  public byte[] getByteArray() {
    int arrayLength = getVarUnsignedInt();
    byte[] array = new byte[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_BYTE, position, array, 0, arrayLength);
    position += arrayLength * LAYOUT_BYTE.byteSize();
    return array;
  }

  /** same as {@link #putByte(byte)} put for <code>byte[]</code> */
  public void putByteArray(byte[] array) {
    ensureCapacity(position + VAR_INT_MAX_BYTE_SIZE + (array.length * LAYOUT_BYTE.byteSize()));
    putByteArrayUnchecked(array);
    if (position > limit) {
      limit = position;
    }
  }

  /** same as {@link #putByteUnchecked(byte)} but writes a <code>byte[]</code> */
  public void putByteArrayUnchecked(byte[] array) {
    putVarUnsignedIntUnchecked(array.length);
    MemorySegment.copy(array, 0, memSegment, LAYOUT_BYTE, position, array.length);
    position += array.length * LAYOUT_BYTE.byteSize();
  }

  /** same as {@link #getByte()} for <code>short</code>. */
  public short getShort() {
    short value = memSegment.get(LAYOUT_SHORT, position);
    position += LAYOUT_SHORT.byteSize();
    return value;
  }

  /** see {@link #getByteAtIndex(long)} for <code>short</code>. */
  public short getShortAtIndex(long index) {
    return memSegment.getAtIndex(LAYOUT_SHORT, index);
  }

  /** same as {@link #putByte(byte)} put for <code>short</code> */
  public void putShort(short value) {
    ensureCapacity(position + LAYOUT_SHORT.byteSize());
    putShortUnchecked(value);
    if (position > limit) {
      limit = position;
    }
  }

  /** same as {@link #putByteUnchecked(byte)} but writes a <code>short</code> */
  public void putShortUnchecked(short value) {
    memSegment.set(LAYOUT_SHORT, position, value);
    position += LAYOUT_SHORT.byteSize();
  }

  /** same as {@link #setByteAtIndex(long, byte)} for <code>short</code> */
  public void setShortAtIndex(long index, short value) {
    long minCapacity = (index * LAYOUT_SHORT.byteSize()) + LAYOUT_SHORT.byteSize();
    ensureCapacity(minCapacity);
    setShortAtIndexUnchecked(index, value);
    if (minCapacity > limit) {
      limit = minCapacity;
    }
  }

  /** see {@link #setByteAtIndexUnchecked(long, byte)} for <code>int</code>. */
  public void setShortAtIndexUnchecked(long index, short value) {
    memSegment.setAtIndex(LAYOUT_SHORT, index, value);
  }

  /** same as {@link #getByte()} for <code>int</code>. */
  public int getInt() {
    int value = getInt(position);
    position += LAYOUT_INT.byteSize();
    return value;
  }

  /** same as {@link #getByte(long)} for <code>int</code>. */
  public int getInt(long pos) {
    return memSegment.get(LAYOUT_INT, pos);
  }

  /** see {@link #getByteAtIndex(long)} for <code>int</code>. */
  public int getIntAtIndex(long index) {
    return memSegment.getAtIndex(LAYOUT_INT, index);
  }

  /** same as {@link #putByte(byte)} put for <code>int</code> */
  public void putInt(int value) {
    ensureCapacity(position + LAYOUT_INT.byteSize());
    putIntUnchecked(value);
    if (position > limit) {
      limit = position;
    }
  }

  /** same as {@link #putByteUnchecked(byte)} but writes a <code>int</code> */
  public void putIntUnchecked(int value) {
    memSegment.set(LAYOUT_INT, position, value);
    position += LAYOUT_INT.byteSize();
  }

  /** same as {@link #setByteAtIndex(long, byte)} for <code>int</code> */
  public void setIntAtIndex(long index, int value) {
    long minCapacity = (index * LAYOUT_INT.byteSize()) + LAYOUT_INT.byteSize();
    ensureCapacity(minCapacity);
    setIntAtIndexUnchecked(index, value);
    if (minCapacity > limit) {
      limit = minCapacity;
    }
  }

  /** see {@link #setByteAtIndexUnchecked(long, byte)} for <code>int</code>. */
  public void setIntAtIndexUnchecked(long index, int value) {
    memSegment.setAtIndex(LAYOUT_INT, index, value);
  }

  /** same as {@link #getByte()} for <code>int[]</code>. */
  public int[] getIntArray() {
    int arrayLength = getVarUnsignedInt();
    int[] array = new int[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_INT, position, array, 0, arrayLength);
    position += arrayLength * LAYOUT_INT.byteSize();
    return array;
  }

  public void putIntArray(int[] array) {
    ensureCapacity(position + VAR_INT_MAX_BYTE_SIZE + (array.length * LAYOUT_INT.byteSize()));
    putIntArrayUnchecked(array);
    if (position > limit) {
      limit = position;
    }
  }

  /** same as {@link #putByteUnchecked(byte)} but writes a <code>int[]</code> */
  public void putIntArrayUnchecked(int[] array) {
    putVarUnsignedIntUnchecked(array.length);
    MemorySegment.copy(array, 0, memSegment, LAYOUT_INT, position, array.length);
    position += array.length * LAYOUT_INT.byteSize();
  }

  /** Writes a 1-5 byte int. */
  public void putVarUnsignedInt(int value) {
    ensureCapacity(position + VAR_INT_MAX_BYTE_SIZE);
    putVarUnsignedIntUnchecked(value);
    if (position > limit) {
      limit = position;
    }
  }

  /** same as {@link #putByteUnchecked(byte)} but writes a <code>var unsigned int</code> */
  public void putVarUnsignedIntUnchecked(int value) {
    // derived from
    // https://github.com/apache/fory/blob/v0.12.3/java/fory-core/src/main/java/org/apache/fory/memory/MemoryBuffer.java#L772
    long index = position;

    // fast path: fits in 7 bits
    if ((value >>> 7) == 0) {
      LAYOUT_BYTE_VAR_HANDLE.set(memSegment, index, (byte) value);
      position = index + 1;
      return;
    }

    // fits in 14 bits: write short
    long encoded = (value & 0x7F) | (((value & 0x3F80) << 1) | 0x80);
    if ((value >>> 14) == 0) {
      LAYOUT_SHORT_VAR_HANDLE.set(memSegment, index, (short) encoded);
      position = index + 2;
      return;
    }

    // fits in 21 bits: write lower 2-bytes as short and upper 1-byte as byte
    encoded |= (((value & 0x1FC000) << 2) | 0x8000);
    if ((value >>> 21) == 0) {
      LAYOUT_SHORT_VAR_HANDLE.set(memSegment, index, (short) (encoded & 0xFFFF));
      LAYOUT_BYTE_VAR_HANDLE.set(memSegment, index + 2, (byte) ((encoded >>> 16) & 0xFF));
      position = index + 3;
      return;
    }

    // fits in 28 bits: write int
    encoded |= ((value & 0xFE00000L) << 3) | 0x800000L;
    if ((value >>> 28) == 0) {
      LAYOUT_INT_VAR_HANDLE.set(memSegment, index, (int) encoded);
      position = index + 4;
      return;
    }

    // fits in 32 bits: write lower 4-bytes as int and upper 1-byte as byte
    encoded |= ((value & 0xFF0000000L) << 4) | 0x80000000L;
    LAYOUT_INT_VAR_HANDLE.set(memSegment, index, (int) (encoded & 0xFFFFFFFFL));
    LAYOUT_BYTE_VAR_HANDLE.set(memSegment, index + 4, (byte) ((encoded >>> 32) & 0xFF));
    position = index + 5;
  }

  /**
   * Reads a 1–5 byte unsigned integer written with {@link #putVarUnsignedIntUnchecked}
   *
   * @return decoded integer
   */
  public int getVarUnsignedInt() {
    long index = position;
    int b0 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memSegment, index++) & 0xFF;

    if ((b0 & 0x80) == 0) {
      position = index;
      return b0;
    }

    int b1 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memSegment, index++) & 0xFF;
    int result = (b0 & 0x7F) | ((b1 & 0x7F) << 7);
    if ((b1 & 0x80) == 0) {
      position = index;
      return result;
    }

    int b2 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memSegment, index++) & 0xFF;
    result |= (b2 & 0x7F) << 14;
    if ((b2 & 0x80) == 0) {
      position = index;
      return result;
    }

    int b3 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memSegment, index++) & 0xFF;
    result |= (b3 & 0x7F) << 21;
    if ((b3 & 0x80) == 0) {
      position = index;
      return result;
    }

    int b4 = (byte) LAYOUT_BYTE_VAR_HANDLE.get(memSegment, index++) & 0xFF;
    result |= (b4 & 0x0F) << 28;
    position = index;
    return result;
  }

  /** same as {@link #getByte()} for <code>long</code>. */
  public long getLong() {
    long value = getLong(position);
    position += LAYOUT_LONG.byteSize();
    return value;
  }

  /** same as {@link #getByte(long)} for <code>long</code>. */
  public long getLong(long pos) {
    return memSegment.get(LAYOUT_LONG, pos);
  }

  /** same as {@link #putByte(byte)} for <code>long</code> */
  public void putLong(long value) {
    ensureCapacity(position + LAYOUT_LONG.byteSize());
    putLongUnchecked(value);
    if (position > limit) {
      limit = position;
    }
  }

  /** same as {@link #putByteUnchecked(byte)} but writes a <code>long</code> */
  public void putLongUnchecked(long value) {
    memSegment.set(LAYOUT_LONG, position, value);
    position += LAYOUT_LONG.byteSize();
  }

  /**
   * Reads a {@link ByteBuffer} with position set to {@link #position} and limit set to {@link
   * #limit}. Returned object is invalid after a call to {@link #ensureCapacity(long)} increased the
   * capacity.
   */
  public ByteBuffer getByteBuffer() {
    if (byteBufferWrapper == null) {
      byteBufferWrapper = memSegment.asByteBuffer();
    }
    return byteBufferWrapper.position(Math.toIntExact(position)).limit(Math.toIntExact(limit));
  }

  /**
   * Reads a {@link MemorySegment} from {@code position} to {@code limit}. Returned object is
   * invalid after a call to {@link #ensureCapacity(long)} increased the capacity.
   */
  public MemorySegment getMemSegment() {
    MemorySegment dstSegment;
    if (position == 0 && limit == getCapacity()) {
      dstSegment = memSegment;
    } else {
      long dstAlignment = (position % alignment == 0 && limit % alignment == 0) ? alignment : 1L;
      dstSegment = memSegment.asSlice(position, limit - position, dstAlignment);
    }
    position = limit;
    return dstSegment;
  }

  /**
   * Copies memory buffer from {@code position} into {@code limit} into this memory buffer starting
   * at {@code position}.
   */
  public void copyFrom(MemoryBuffer memBuffer) {
    long nrBytes = memBuffer.getLimit() - memBuffer.getPosition();
    if (nrBytes > 0) {
      ensureCapacity(getCapacity() + nrBytes);

      MemorySegment.copy(
          memBuffer.memSegment, memBuffer.getPosition(), memSegment, getPosition(), nrBytes);
      memBuffer.setPosition(memBuffer.getPosition() + nrBytes);
      setPosition(getPosition() + nrBytes);
    }
  }

  /**
   * Create a native memory buffer with the given size and default alignment (one byte).
   *
   * @param byteSize buffer size
   * @return native memory buffer that can be grown with {@link #ensureCapacity(long)}}
   */
  public static MemoryBuffer allocate(long byteSize) {
    return allocate(byteSize, 1L);
  }

  /**
   * Create a native memory buffer with the given size and alignment.
   *
   * @param byteSize buffer size
   * @param byteAlignment buffer alignment, must be a power of two
   * @return native memory buffer that can be grown with {@link #ensureCapacity(long)}}
   */
  public static MemoryBuffer allocate(long byteSize, long byteAlignment) {
    Arena arena = Arena.ofConfined();
    MemorySegment memSegment = arena.allocate(byteSize, byteAlignment);
    return new MemoryBuffer(memSegment, byteAlignment, arena);
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

  /** Same as {@link #wrap(byte[])} for {@code long[]} arrays. */
  public static MemoryBuffer wrap(long[] array) {
    return new MemoryBuffer(MemorySegment.ofArray(array));
  }

  @Override
  public void close() {
    ClosableUtils.close(arena);
  }
}
