package org.molgenis.vipannotate.serialization;

import static org.molgenis.vipannotate.util.Numbers.nextPowerOf2;
import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

/// Memory buffer with little endian byte order.
@SuppressWarnings("MustBeClosedChecker")
public final class MemoryBuffer implements AutoCloseableNoThrow {
  private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

  private static final ValueLayout.OfBoolean LAYOUT_BOOLEAN;
  private static final ValueLayout.OfByte LAYOUT_BYTE;
  private static final ValueLayout.OfDouble LAYOUT_DOUBLE;
  private static final ValueLayout.OfFloat LAYOUT_FLOAT;
  private static final ValueLayout.OfShort LAYOUT_SHORT;
  private static final ValueLayout.OfInt LAYOUT_INT;
  private static final ValueLayout.OfLong LAYOUT_LONG;

  static {
    LAYOUT_BOOLEAN = ValueLayout.JAVA_BOOLEAN.withOrder(BYTE_ORDER);
    LAYOUT_BYTE = ValueLayout.JAVA_BYTE.withOrder(BYTE_ORDER);
    LAYOUT_DOUBLE = ValueLayout.JAVA_DOUBLE_UNALIGNED.withOrder(BYTE_ORDER);
    LAYOUT_FLOAT = ValueLayout.JAVA_FLOAT_UNALIGNED.withOrder(BYTE_ORDER);
    LAYOUT_SHORT = ValueLayout.JAVA_SHORT_UNALIGNED.withOrder(BYTE_ORDER);
    LAYOUT_INT = ValueLayout.JAVA_INT_UNALIGNED.withOrder(BYTE_ORDER);
    LAYOUT_LONG = ValueLayout.JAVA_LONG_UNALIGNED.withOrder(BYTE_ORDER);
  }

  /// Whether the buffer is allowed to grow
  @Getter private final boolean resizable;

  /// alignment constraint (in bytes) of the native memory segment.
  ///
  /// always one for heap-based segments
  @Getter private final long alignment;

  /// Underlying native or heap-based memory segment
  private MemorySegment memSegment;

  /// [Arena] that was used to allocate [#memSegment].
  ///
  /// `null` for heap-based segments
  @Nullable private Arena arena;

  /// index of the next element to read or write. automatically updated on get/put operations.
  ///
  /// 0 <= position <= limit <= capacity
  @Getter private long position;

  /// the index (exclusive) that marks the end of the accessible data in the buffer.
  ///
  /// 0 <= position <= limit <= capacity
  @Getter private long limit;

  private MemoryBuffer(MemorySegment memSegment, boolean resizable) {
    this(memSegment, resizable, 1L, null);
  }

  private MemoryBuffer(
      MemorySegment memSegment, boolean resizable, long alignment, @Nullable Arena arena) {
    this.memSegment = memSegment;
    this.resizable = resizable;
    this.alignment = alignment;
    this.arena = arena;

    this.position = 0;
    this.limit = getCapacity();
  }

  /// Create a native memory buffer with the given size and default alignment (one byte).
  ///
  /// @param byteSize buffer size
  /// @return native memory buffer that can be grown with [#ensureCapacity(long)]}
  public static MemoryBuffer allocate(long byteSize) {
    return allocate(byteSize, 1L);
  }

  /// Create a native memory buffer with the given size and alignment.
  ///
  /// @param byteSize buffer size
  /// @param byteAlignment buffer alignment, must be a power of two
  /// @return native memory buffer that can be grown with [#ensureCapacity(long)]}
  public static MemoryBuffer allocate(long byteSize, long byteAlignment) {
    Arena arena = Arena.ofConfined();
    MemorySegment memSegment = allocateMemSegment(arena, byteSize, byteAlignment);
    return new MemoryBuffer(memSegment, true, byteAlignment, arena);
  }

  private static MemorySegment allocateMemSegment(Arena arena, long byteSize, long byteAlignment) {
    try {
      return arena.allocate(byteSize, byteAlignment);
    } catch (Throwable t) {
      arena.close();
      throw t;
    }
  }

  /// Create a heap-based memory buffer with the given size
  ///
  /// @param byteSize buffer size
  /// @return heap-based memory buffer that can be grown with [#ensureCapacity(long)]}
  public static MemoryBuffer allocateHeap(int byteSize) {
    return new MemoryBuffer(MemorySegment.ofArray(new byte[byteSize]), true);
  }

  /// Wraps an existing `byte[]` array in a `MemoryBuffer`. The buffer provides a view of the
  /// array's contents; modifications are reflected in both.
  ///
  /// @param array the array to wrap
  /// @return a new `MemoryBuffer` view of the given array. can't be grown with
  ///   [#ensureCapacity(long)]}
  public static MemoryBuffer wrap(byte[] array) {
    return new MemoryBuffer(MemorySegment.ofArray(array), false);
  }

  public void setPosition(long position) {
    validateNonNegative(position);
    if (position > limit) {
      throw new IllegalArgumentException();
    }
    this.position = position;
  }

  public void setLimit(long limit) {
    validateNonNegative(limit);
    if (limit < position || limit > getCapacity()) {
      throw new IllegalArgumentException();
    }
    this.limit = limit;
  }

  /// The total number of bytes the buffer can hold
  public long getCapacity() {
    return memSegment.byteSize();
  }

  /// ensures the native memory buffer has at least the given capacity.
  ///
  /// @throws UnsupportedOperationException if this buffer is not resizable
  public void ensureCapacity(long minCapacity) {
    if (minCapacity <= getCapacity()) {
      return;
    }

    if (!resizable) {
      throw new UnsupportedOperationException("cannot resize this memory buffer");
    }

    long newCapacity = Math.max(alignment, nextPowerOf2(minCapacity));
    if (arena == null) {
      MemorySegment newMemorySegment =
          MemorySegment.ofArray(new byte[Math.toIntExact(newCapacity)]);

      newMemorySegment.copyFrom(memSegment);
      memSegment = newMemorySegment;
    } else {
      // create new memory segment
      Arena newArena = Arena.ofConfined();
      MemorySegment newMemorySegment = allocateMemSegment(newArena, newCapacity, alignment);
      newMemorySegment.copyFrom(memSegment);

      // replace old memory segment
      Arena oldArena = arena;
      memSegment = newMemorySegment;
      arena = newArena;
      oldArena.close();
    }
  }

  private void ensureCapacityForWrite(long byteSize) {
    ensureCapacity(Math.addExact(position, byteSize));
  }

  private void ensureCapacityForWrite(long byteSize, long nrElements) {
    validateNonNegative(nrElements);
    ensureCapacityForWrite(Math.multiplyExact(nrElements, byteSize));
  }

  /// Rewinds this buffer: sets the position to zero.
  public void rewind() {
    this.position = 0;
  }

  /// Clears this buffer: sets the position to zero and limit to capacity.
  public void clear() {
    this.position = 0;
    this.limit = getCapacity();
  }

  /// Flips this buffer: sets the limit to the current position and the position to zero.
  public void flip() {
    this.limit = position;
    this.position = 0;
  }

  /// Returns the boolean at the current position and increments the position.
  public boolean getBoolean() {
    boolean value = getBooleanAtPosition(position);
    position += LAYOUT_BOOLEAN.byteSize();
    return value;
  }

  /// Returns the boolean at the given position.
  public boolean getBooleanAtPosition(long pos) {
    checkReadable(pos, LAYOUT_BOOLEAN.byteSize());
    return memSegment.get(LAYOUT_BOOLEAN, pos);
  }

  /// Returns the boolean at the given index.
  public boolean getBooleanAtIndex(long index) {
    checkReadableAtIndex(index, LAYOUT_BOOLEAN.byteSize());
    return memSegment.getAtIndex(LAYOUT_BOOLEAN, index);
  }

  /// Writes a boolean at the current position.
  ///
  /// grows capacity and extends limit if required.
  public void putBoolean(boolean value) {
    ensureCapacityForWrite(LAYOUT_BOOLEAN.byteSize());
    putBooleanUnchecked(value);

    if (position > limit) {
      limit = position;
    }
  }

  /// Writes a boolean at the current position. Very fast. Use with care.
  ///
  /// does not check whether it is possible to write the boolean which could result in writing after
  /// the limit.
  public void putBooleanUnchecked(boolean value) {
    memSegment.set(LAYOUT_BOOLEAN, position, value);
    position += LAYOUT_BOOLEAN.byteSize();
  }

  /// Returns the byte at the current position and increments the position
  public byte getByte() {
    byte value = getByteAtPosition(position);
    position += LAYOUT_BYTE.byteSize();
    return value;
  }

  /// Returns the byte at the given position
  public byte getByteAtPosition(long pos) {
    checkReadable(pos, LAYOUT_BYTE.byteSize());
    return memSegment.get(LAYOUT_BYTE, pos);
  }

  /// Returns the byte at the given index
  public byte getByteAtIndex(long index) {
    checkReadableAtIndex(index, LAYOUT_BYTE.byteSize());
    return memSegment.getAtIndex(LAYOUT_BYTE, index);
  }

  /// Writes a byte at the current position.
  ///
  /// grows capacity and extends limit if required.
  public void putByte(byte value) {
    ensureCapacityForWrite(LAYOUT_BYTE.byteSize());
    putByteUnchecked(value);

    if (position > limit) {
      limit = position;
    }
  }

  /// Writes a byte at the current position. Very fast. Use with care.
  ///
  /// does not check whether it is possible to write the byte which could result in writing after
  /// the limit.
  public void putByteUnchecked(byte value) {
    memSegment.set(LAYOUT_BYTE, position, value);
    position += LAYOUT_BYTE.byteSize();
  }

  /// Writes the same byte x times starting from the current position.
  ///
  /// grows capacity and extends limit if required.
  ///
  /// @throws IllegalArgumentException if count is negative
  /// @throws ArithmeticException if [#position] + {@code count} overflows a {@code long}
  public void putByte(byte value, long count) {
    validateNonNegative(count);
    ensureCapacityForWrite(LAYOUT_BYTE.byteSize(), count);
    putByteUnchecked(value, count);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteUnchecked(byte)] but writes a `byte` a given number of times.
  public void putByteUnchecked(byte value, long count) {
    memSegment.asSlice(position, count).fill(value);
    position += LAYOUT_BYTE.byteSize() * count;
  }

  /// Returns an array containing the next {@code arrayLength} elements.
  ///
  /// Advances this buffer's position by the total byte size of the elements read.
  ///
  /// @param arrayLength number of elements to read
  /// @return an array containing the read elements
  /// @throws IllegalArgumentException if {@code arrayLength} is negative
  /// @throws IndexOutOfBoundsException if the requested elements exceed the buffer's limit
  /// @throws ArithmeticException if calculating the total byte size overflows a {@code long}
  public byte[] getByteArray(int arrayLength) {
    long arrayByteSize = checkReadableArray(arrayLength, LAYOUT_BYTE.byteSize());
    byte[] array = new byte[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_BYTE, position, array, 0, arrayLength);
    position += arrayByteSize;
    return array;
  }

  /// same as [#putByte(byte)] put for `byte[]`
  public void putByteArray(byte[] array) {
    ensureCapacityForWrite(LAYOUT_BYTE.byteSize(), array.length);
    putByteArrayUnchecked(array);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteUnchecked(byte)] but writes a `byte[]`
  public void putByteArrayUnchecked(byte[] array) {
    MemorySegment.copy(array, 0, memSegment, LAYOUT_BYTE, position, array.length);
    position += array.length * LAYOUT_BYTE.byteSize();
  }

  /// Returns the double at the current position and increments the position.
  public double getDouble() {
    double value = getDoubleAtPosition(position);
    position += LAYOUT_DOUBLE.byteSize();
    return value;
  }

  /// same as [#getByteAtPosition(long)] for `double`.
  public double getDoubleAtPosition(long pos) {
    checkReadable(pos, LAYOUT_DOUBLE.byteSize());
    return memSegment.get(LAYOUT_DOUBLE, pos);
  }

  /// Returns the double at the given index.
  public double getDoubleAtIndex(long index) {
    checkReadableAtIndex(index, LAYOUT_DOUBLE.byteSize());
    return memSegment.getAtIndex(LAYOUT_DOUBLE, index);
  }

  /// Writes a double at the current position.
  ///
  /// grows capacity and extends limit if required.
  public void putDouble(double value) {
    ensureCapacityForWrite(LAYOUT_DOUBLE.byteSize());
    putDoubleUnchecked(value);

    if (position > limit) {
      limit = position;
    }
  }

  /// Writes a double without bounds/capacity checks.
  public void putDoubleUnchecked(double value) {
    memSegment.set(LAYOUT_DOUBLE, position, value);
    position += LAYOUT_DOUBLE.byteSize();
  }

  /// same as [#getByteArray(int)] for `double[]`.
  public double[] getDoubleArray(int arrayLength) {
    long arrayByteSize = checkReadableArray(arrayLength, LAYOUT_DOUBLE.byteSize());
    double[] array = new double[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_DOUBLE, position, array, 0, arrayLength);
    position += arrayByteSize;
    return array;
  }

  /// same as [#getByteArray(int)] for `long[]`.
  public long[] getLongArray(int arrayLength) {
    long arrayByteSize = checkReadableArray(arrayLength, LAYOUT_LONG.byteSize());
    long[] array = new long[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_LONG, position, array, 0, arrayLength);
    position += arrayByteSize;
    return array;
  }

  /// same as [#putByteArray(byte\[\])] for `long[]`.
  public void putLongArray(long[] array) {
    ensureCapacityForWrite(LAYOUT_LONG.byteSize(), array.length);
    putLongArrayUnchecked(array);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteArrayUnchecked(byte\[\])] for `long[]`.
  public void putLongArrayUnchecked(long[] array) {
    MemorySegment.copy(array, 0, memSegment, LAYOUT_LONG, position, array.length);
    position += array.length * LAYOUT_LONG.byteSize();
  }

  /// same as [#putByteArray(byte\[\])] for `double[]`.
  public void putDoubleArray(double[] array) {
    ensureCapacityForWrite(LAYOUT_DOUBLE.byteSize(), array.length);
    putDoubleArrayUnchecked(array);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteArrayUnchecked(byte\[\])] for `double[]`.
  public void putDoubleArrayUnchecked(double[] array) {
    MemorySegment.copy(array, 0, memSegment, LAYOUT_DOUBLE, position, array.length);
    position += array.length * LAYOUT_DOUBLE.byteSize();
  }

  /// Returns the float at the current position and increments the position.
  public float getFloat() {
    float value = getFloatAtPosition(position);
    position += LAYOUT_FLOAT.byteSize();
    return value;
  }

  /// same as [#getByteAtPosition(long)] for `float`.
  public float getFloatAtPosition(long pos) {
    checkReadable(pos, LAYOUT_FLOAT.byteSize());
    return memSegment.get(LAYOUT_FLOAT, pos);
  }

  /// Returns the float at the given index.
  public float getFloatAtIndex(long index) {
    checkReadableAtIndex(index, LAYOUT_FLOAT.byteSize());
    return memSegment.getAtIndex(LAYOUT_FLOAT, index);
  }

  /// Writes a float at the current position.
  ///
  /// grows capacity and extends limit if required.
  public void putFloat(float value) {
    ensureCapacityForWrite(LAYOUT_FLOAT.byteSize());
    putFloatUnchecked(value);

    if (position > limit) {
      limit = position;
    }
  }

  /// Writes a float without bounds/capacity checks.
  public void putFloatUnchecked(float value) {
    memSegment.set(LAYOUT_FLOAT, position, value);
    position += LAYOUT_FLOAT.byteSize();
  }

  /// same as [#getByteArray(int)] for `float[]`.
  public float[] getFloatArray(int arrayLength) {
    long arrayByteSize = checkReadableArray(arrayLength, LAYOUT_FLOAT.byteSize());
    float[] array = new float[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_FLOAT, position, array, 0, arrayLength);
    position += arrayByteSize;
    return array;
  }

  /// same as [#putByteArray(byte\[\])] for `float[]`.
  public void putFloatArray(float[] array) {
    ensureCapacityForWrite(LAYOUT_FLOAT.byteSize(), array.length);
    putFloatArrayUnchecked(array);

    if (position > limit) {
      limit = position;
    }
  }

  /// Writes a float[] without capacity/limit checks.
  public void putFloatArrayUnchecked(float[] array) {
    MemorySegment.copy(array, 0, memSegment, LAYOUT_FLOAT, position, array.length);
    position += array.length * LAYOUT_FLOAT.byteSize();
  }

  /// same as [#getByte()] for `short`.
  public short getShort() {
    short value = getShortAtPosition(position);
    position += LAYOUT_SHORT.byteSize();
    return value;
  }

  /// same as [#getByteAtPosition(long)] for `short`.
  public short getShortAtPosition(long pos) {
    checkReadable(pos, LAYOUT_SHORT.byteSize());
    return memSegment.get(LAYOUT_SHORT, pos);
  }

  /// see [#getByteAtIndex(long)] for `short`.
  public short getShortAtIndex(long index) {
    checkReadableAtIndex(index, LAYOUT_SHORT.byteSize());
    return memSegment.getAtIndex(LAYOUT_SHORT, index);
  }

  /// same as [#putByte(byte)] put for `short`
  public void putShort(short value) {
    ensureCapacityForWrite(LAYOUT_SHORT.byteSize());
    putShortUnchecked(value);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteUnchecked(byte)] but writes a `short`
  public void putShortUnchecked(short value) {
    memSegment.set(LAYOUT_SHORT, position, value);
    position += LAYOUT_SHORT.byteSize();
  }

  /// same as [#getByte()] for `short[]`.
  public short[] getShortArray(int arrayLength) {
    long arrayByteSize = checkReadableArray(arrayLength, LAYOUT_SHORT.byteSize());
    short[] array = new short[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_SHORT, position, array, 0, arrayLength);
    position += arrayByteSize;
    return array;
  }

  /// same as [#putByteArray(byte\[\])] for `short[]`.
  public void putShortArray(short[] array) {
    ensureCapacityForWrite(LAYOUT_SHORT.byteSize(), array.length);
    putShortArrayUnchecked(array);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteArrayUnchecked(byte\[\])] for `short[]`.
  public void putShortArrayUnchecked(short[] array) {
    MemorySegment.copy(array, 0, memSegment, LAYOUT_SHORT, position, array.length);
    position += array.length * LAYOUT_SHORT.byteSize();
  }

  /// same as [#getByte()] for `int`.
  public int getInt() {
    int value = getIntAtPosition(position);
    position += LAYOUT_INT.byteSize();
    return value;
  }

  /// same as [#getByteAtPosition(long)] for `int`.
  public int getIntAtPosition(long pos) {
    checkReadable(pos, LAYOUT_INT.byteSize());
    return memSegment.get(LAYOUT_INT, pos);
  }

  /// same as [#getByteAtIndex(long)] for `int`.
  public int getIntAtIndex(long index) {
    checkReadableAtIndex(index, LAYOUT_INT.byteSize());
    return memSegment.getAtIndex(LAYOUT_INT, index);
  }

  /// same as [#putByte(byte)] put for `int`
  public void putInt(int value) {
    ensureCapacityForWrite(LAYOUT_INT.byteSize());
    putIntUnchecked(value);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteUnchecked(byte)] but writes an `int`
  public void putIntUnchecked(int value) {
    memSegment.set(LAYOUT_INT, position, value);
    position += LAYOUT_INT.byteSize();
  }

  /// same as [#getByte()] for `int[]`.
  public int[] getIntArray(int arrayLength) {
    long arrayByteSize = checkReadableArray(arrayLength, LAYOUT_INT.byteSize());
    int[] array = new int[arrayLength];
    MemorySegment.copy(memSegment, LAYOUT_INT, position, array, 0, arrayLength);
    position += arrayByteSize;
    return array;
  }

  /// same as [#putByteArray(byte\[\])] for `int[]`.
  public void putIntArray(int[] array) {
    ensureCapacityForWrite(LAYOUT_INT.byteSize(), array.length);
    putIntArrayUnchecked(array);

    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteArrayUnchecked(byte\[\])] for `int[]`.
  public void putIntArrayUnchecked(int[] array) {
    MemorySegment.copy(array, 0, memSegment, LAYOUT_INT, position, array.length);
    position += array.length * LAYOUT_INT.byteSize();
  }

  /// same as [#getByte()] for `long`.
  public long getLong() {
    long value = getLongAtPosition(position);
    position += LAYOUT_LONG.byteSize();
    return value;
  }

  /// same as [#getByteAtPosition(long)] for `long`.
  public long getLongAtPosition(long pos) {
    checkReadable(pos, LAYOUT_LONG.byteSize());
    return memSegment.get(LAYOUT_LONG, pos);
  }

  /// same as [#getByteAtIndex(long)] for `long`.
  public long getLongAtIndex(long index) {
    checkReadableAtIndex(index, LAYOUT_LONG.byteSize());
    return memSegment.getAtIndex(LAYOUT_LONG, index);
  }

  /// same as [#putByte(byte)] for `long`
  public void putLong(long value) {
    ensureCapacityForWrite(LAYOUT_LONG.byteSize());
    putLongUnchecked(value);
    if (position > limit) {
      limit = position;
    }
  }

  /// same as [#putByteUnchecked(byte)] but writes a `long`
  public void putLongUnchecked(long value) {
    memSegment.set(LAYOUT_LONG, position, value);
    position += LAYOUT_LONG.byteSize();
  }

  /// Reads a [ByteBuffer] with position set to [#position] and limit set to [#limit].
  ///
  /// Returned object is invalid after a call to [#ensureCapacity(long)] increased the capacity.
  ///
  /// @return [ByteBuffer] view of this buffer
  /// @throws ArithmeticException if [#position] or [#limit] overflow an int
  public ByteBuffer getByteBuffer() {
    return memSegment
        .asByteBuffer()
        .order(BYTE_ORDER)
        .position(Math.toIntExact(position))
        .limit(Math.toIntExact(limit));
  }

  /// Returns a {@link MemorySegment} containing the remaining bytes.
  ///
  /// - Advances this buffer's position to its limit
  /// - The returned memory segment shares a region of backing memory with this segment.
  /// - Returned object is invalid after a call to [#ensureCapacity(long)] increased the capacity.
  ///
  /// @return a buffer containing the remaining data
  /// @throws IllegalArgumentException if length is negative
  /// @throws IndexOutOfBoundsException if length exceeds remaining data
  public MemorySegment getMemSegment() {
    return getMemSegment(limit - position);
  }

  /// Returns a {@link MemorySegment} containing the next 'length' bytes.
  ///
  /// - Advances this buffer's position by 'length'.
  /// - The returned memory segment shares a region of backing memory with this segment.
  /// - Returned object is invalid after a call to [#ensureCapacity(long)] increased the capacity.
  ///
  /// @return a buffer containing the remaining data
  /// @throws IllegalArgumentException if length is negative
  /// @throws IndexOutOfBoundsException if length exceeds remaining data
  public MemorySegment getMemSegment(long length) {
    validateNonNegative(length);
    if (length > limit - position) {
      throw new IndexOutOfBoundsException();
    }
    return getMemSegmentUnchecked(length);
  }

  private MemorySegment getMemSegmentUnchecked(long length) {
    MemorySegment dstSegment;
    if (position == 0 && length == getCapacity()) {
      dstSegment = memSegment; // fast path
    } else {
      long dstAlignment = position % alignment == 0 ? alignment : 1L;
      dstSegment = memSegment.asSlice(position, length, dstAlignment);
    }
    position += length;
    return dstSegment;
  }

  /// Returns a buffer containing the remaining data, from the current position to the limit.
  ///
  /// - Advances this buffer's position to its limit.
  /// - The returned buffer shares the underlying memory without copying it and must not be closed.
  /// - Returned object is invalid after a call to [#ensureCapacity(long)] increased the capacity.
  ///
  /// @return a buffer containing the remaining data
  public MemoryBuffer getMemoryBuffer() {
    return getMemoryBufferUnchecked(limit - position);
  }

  /// Returns a buffer containing the next 'length' bytes.
  ///
  /// - Advances this buffer's position by 'length'.
  /// - The returned buffer shares the underlying memory without copying it and must not be closed.
  /// - Returned object is invalid after a call to [#ensureCapacity(long)] increased the capacity.
  ///
  /// @param length number of bytes to include
  /// @return a buffer containing the specified bytes
  /// @throws IllegalArgumentException if length is negative
  /// @throws IndexOutOfBoundsException if length exceeds remaining data
  public MemoryBuffer getMemoryBuffer(long length) {
    validateNonNegative(length);
    if (length > limit - position) {
      throw new IndexOutOfBoundsException();
    }
    return getMemoryBufferUnchecked(length);
  }

  private @NonNull MemoryBuffer getMemoryBufferUnchecked(long length) {
    long dstAlignment = position % alignment == 0 ? alignment : 1L;
    MemorySegment dstSegment = getMemSegmentUnchecked(length);
    MemoryBuffer dstBuffer = new MemoryBuffer(dstSegment, false, dstAlignment, null);
    dstBuffer.limit = length;
    return dstBuffer;
  }

  /// Copies the remaining bytes from {@code srcBuffer} into this buffer.
  ///
  /// <p>Advances both buffers' positions by the number of bytes copied.
  public void copyFrom(MemoryBuffer srcBuffer) {
    if (srcBuffer == this) {
      throw new IllegalArgumentException();
    }

    long length = srcBuffer.limit - srcBuffer.position;
    if (length == 0L) {
      return;
    }

    ensureCapacityForWrite(length);
    MemorySegment.copy(srcBuffer.memSegment, srcBuffer.position, memSegment, position, length);
    srcBuffer.position += length;
    position += length;
    limit = Math.max(limit, position);
  }

  private void checkReadable(long offset, long byteSize) {
    validateNonNegative(offset);
    checkReadableUnchecked(offset, byteSize);
  }

  private void checkReadableAtIndex(long index, long byteSize) {
    validateNonNegative(index);
    long offset = Math.multiplyExact(index, byteSize);
    checkReadableUnchecked(offset, byteSize);
  }

  private void checkReadableUnchecked(long offset, long byteSize) {
    if (offset > limit || byteSize > limit - offset) {
      throw new IndexOutOfBoundsException();
    }
  }

  private long checkReadableArray(int arrayLength, long byteSize) {
    validateNonNegative(arrayLength);

    long arrayByteSize = Math.multiplyExact(arrayLength, byteSize);
    checkReadable(position, arrayByteSize);
    return arrayByteSize;
  }

  @Override
  public void close() {
    ClosableUtils.close(arena);
  }
}
