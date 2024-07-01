package org.molgenis.streamvbyte;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * <a href="https://github.com/fast-pack/streamvbyte/tree/v2.0.0">streamvbyte v2.0.0</a> native
 * library wrapper using the Foreign Function and Memory (FFM) API.
 *
 * @see <a href=https://doi.org/10.48550/arXiv.1709.08990>Stream VByte: Faster Byte-Oriented Integer
 *     Compression</a>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamVByte implements AutoCloseable {
  private final Arena arena;
  private final Linker linker;
  private final SymbolLookup symbolLookup;

  @Nullable private MethodHandle encodeHandle;
  @Nullable private MethodHandle decodeHandle;
  @Nullable private MethodHandle deltaEncodeHandle;
  @Nullable private MethodHandle deltaDecodeHandle;

  /** Returns a StreamVByte library wrapper using the native linker */
  public static StreamVByte create() {
    //noinspection DataFlowIssue --> false positive
    return create(Linker.nativeLinker());
  }

  /** Returns a StreamVByte library wrapper using the given linker */
  public static StreamVByte create(Linker linker) {
    String overridePath = System.getProperty("streamvbyte.lib.path");
    if (overridePath == null) {
      overridePath = System.getenv("STREAMVBYTE_LIB_PATH");
    }

    Arena arena = Arena.ofConfined();
    //noinspection DataFlowIssue --> false positive
    SymbolLookup symbolLookup =
        overridePath != null
            ? SymbolLookup.libraryLookup(Paths.get(overridePath), arena)
            : SymbolLookup.libraryLookup("streamvbyte", arena);

    return new StreamVByte(arena, linker, symbolLookup);
  }

  /**
   * Encodes an array of integers in an array of bytes using StreamVByte compression.
   *
   * @param srcSegment input memory segment containing 'length' integer values
   * @param dstSegment output memory segment of at least 'maxCompressedByte(length)' bytes
   * @param length number of integer values to encode
   * @return the number of bytes written
   * @throws IllegalArgumentException output memory segment is too small
   * @throws StreamVByteException if method handle could not be created or invocation failed
   */
  public long encode(MemorySegment srcSegment, MemorySegment dstSegment, int length) {
    validateEncodeDstMemorySegment(length, dstSegment);

    try {
      if (encodeHandle == null) {
        encodeHandle =
            linker.downcallHandle(
                symbolLookup.findOrThrow("streamvbyte_encode"),
                FunctionDescriptor.of(
                    ValueLayout.JAVA_LONG, // size_t return
                    ValueLayout.ADDRESS, // const uint32_t* in
                    ValueLayout.JAVA_INT, // uint32_t length
                    ValueLayout.ADDRESS), // uint8_t* out
                Linker.Option.critical(true));
      }

      //noinspection DataFlowIssue
      return (long) encodeHandle.invokeExact(srcSegment, length, dstSegment);
    } catch (Throwable e) {
      throw new StreamVByteException(e);
    }
  }

  /**
   * Returns the maximum number of compressed bytes required to encode the given number of integer
   * values. This value may overestimate the required space by up to four data bytes in the worst
   * case.
   *
   * @param length number of integer values
   * @return maximum number of bytes required to encode 'length' integer values
   */
  public long maxCompressedBytes(int length) {
    // see https://github.com/fast-pack/streamvbyte/blob/v2.0.0/include/streamvbyte.h#L33
    // the static inline function is not accessible through the FFM API so it reimplemented in java
    long nrControlBytes = (length + 3L) / 4L;
    long maxNrControlBytes = (long) length * Integer.BYTES;
    long streamVBytePadding = 16;
    return nrControlBytes + maxNrControlBytes + streamVBytePadding;
  }

  /**
   * Decodes a StreamVByte-compressed byte array into an array of integers.
   *
   * @param srcSegment input memory segment with 'length' encoded integers
   * @param dstSegment output memory segment that can store at least 'length' integers
   * @param length number of integer values
   * @return number of bytes read
   * @throws IllegalArgumentException output memory segment is too small
   * @throws StreamVByteException if method handle could not be created or invocation failed
   */
  public long decode(MemorySegment srcSegment, MemorySegment dstSegment, int length) {
    validateDecodeDstMemorySegment(length, dstSegment);

    try {
      if (decodeHandle == null) {
        decodeHandle =
            linker.downcallHandle(
                symbolLookup.findOrThrow("streamvbyte_decode"),
                FunctionDescriptor.of(
                    ValueLayout.JAVA_LONG, // size_t return
                    ValueLayout.ADDRESS, // const uint8_t* in
                    ValueLayout.ADDRESS, // uint32_t* out
                    ValueLayout.JAVA_INT), // uint32_t length
                Linker.Option.critical(true));
      }

      //noinspection DataFlowIssue
      return (long) decodeHandle.invokeExact(srcSegment, dstSegment, length);
    } catch (Throwable e) {
      throw new StreamVByteException(e);
    }
  }

  /**
   * Encodes an array of integers in an array of bytes using StreamVByte compression with
   * differential encoding. Preferable for sorted input values.
   *
   * @param srcSegment input memory segment containing {@code length} integers
   * @param dstSegment output memory segment of at least 'maxCompressedByte(length)' bytes
   * @param length number of integer values
   * @return the number of bytes written
   * @throws IllegalArgumentException output memory segment is too small
   * @throws StreamVByteException if method handle could not be created or invocation failed
   */
  public long deltaEncode(MemorySegment srcSegment, MemorySegment dstSegment, int length) {
    validateEncodeDstMemorySegment(length, dstSegment);

    try {
      if (deltaEncodeHandle == null) {
        deltaEncodeHandle =
            linker.downcallHandle(
                symbolLookup.findOrThrow("streamvbyte_delta_encode"),
                FunctionDescriptor.of(
                    ValueLayout.JAVA_LONG, // size_t
                    ValueLayout.ADDRESS, // const uint32_t* in
                    ValueLayout.JAVA_INT, // uint32_t length
                    ValueLayout.ADDRESS, // uint8_t* out
                    ValueLayout.JAVA_INT), // uint32_t prev
                Linker.Option.critical(true));
      }

      //noinspection DataFlowIssue
      return (long) deltaEncodeHandle.invokeExact(srcSegment, length, dstSegment, 0);
    } catch (Throwable e) {
      throw new StreamVByteException(e);
    }
  }

  /**
   * Decodes a StreamVByte-compressed byte array into an array of integers with differential
   * encoding.
   *
   * @param srcSegment input memory segment with 'length' encoded integers
   * @param dstSegment output memory segment that can store at least 'length' integers
   * @param length number of integer values
   * @return number of bytes read
   * @throws IllegalArgumentException output memory segment is too small
   * @throws StreamVByteException if method handle could not be created or invocation failed
   */
  public long deltaDecode(MemorySegment srcSegment, MemorySegment dstSegment, int length) {
    validateDecodeDstMemorySegment(length, dstSegment);

    try {
      if (deltaDecodeHandle == null) {
        deltaDecodeHandle =
            linker.downcallHandle(
                symbolLookup.findOrThrow("streamvbyte_delta_decode"),
                FunctionDescriptor.of(
                    ValueLayout.JAVA_LONG, // size_t return
                    ValueLayout.ADDRESS, // const uint8_t* in
                    ValueLayout.ADDRESS, // uint32_t* out
                    ValueLayout.JAVA_INT, // uint32_t length
                    ValueLayout.JAVA_INT), // uint32_t prev
                Linker.Option.critical(true));
      }

      //noinspection DataFlowIssue
      return (long) deltaDecodeHandle.invokeExact(srcSegment, dstSegment, length, 0);
    } catch (Throwable e) {
      throw new StreamVByteException(e);
    }
  }

  private void validateEncodeDstMemorySegment(int length, MemorySegment dstMemorySegment) {
    long actualByteSize = dstMemorySegment.byteSize();
    long requiredByteSize = maxCompressedBytes(length);

    if (actualByteSize < requiredByteSize) {
      throw new IllegalArgumentException(
          "Output memory segment is too small: required %d bytes, but got %d"
              .formatted(requiredByteSize, actualByteSize));
    }
  }

  private void validateDecodeDstMemorySegment(int length, MemorySegment dstMemorySegment) {
    long actualByteSize = dstMemorySegment.byteSize();
    long requiredByteSize = (long) length * Integer.BYTES;

    if (actualByteSize < requiredByteSize) {
      throw new IllegalArgumentException(
          "Output memory segment is too small: required %d bytes, but got %d"
              .formatted(requiredByteSize, actualByteSize));
    }
  }

  @Override
  public void close() {
    arena.close();
  }
}
