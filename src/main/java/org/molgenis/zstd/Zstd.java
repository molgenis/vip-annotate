package org.molgenis.zstd;

import static java.util.Objects.requireNonNull;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class Zstd implements AutoCloseable {
  private final Arena arena;
  private final Linker linker;
  private final SymbolLookup symbolLookup;

  // common method handles
  @Nullable private MethodHandle isErrorMethodHandle;

  // compression method handles
  @Nullable private MethodHandle createCCtxMethodHandle;
  @Nullable private MethodHandle compressBoundMethodHandle;
  @Nullable private MethodHandle compressCCtxMethodHandle;
  @Nullable private MethodHandle freeCCtxMethodHandle;
  private boolean compressMethodHandlesInitialized;

  // decompression method handles
  @Nullable private MethodHandle createDCtxMethodHandle;
  @Nullable private MethodHandle decompressDCtxMethodHandle;
  @Nullable private MethodHandle freeDCtxMethodHandle;
  private boolean decompressMethodHandlesInitialized;

  public static Zstd create() {
    //noinspection DataFlowIssue
    return create(Linker.nativeLinker());
  }

  public static Zstd create(Linker linker) {
    String overridePath = System.getProperty("zstd.lib.path");
    if (overridePath == null) {
      overridePath = System.getenv("ZSTD_LIB_PATH");
    }

    Arena arena = Arena.ofConfined();

    SymbolLookup symbolLookup;
    if (overridePath != null) {
      //noinspection DataFlowIssue
      symbolLookup = SymbolLookup.libraryLookup(Paths.get(overridePath), arena);
    } else {
      //noinspection DataFlowIssue
      symbolLookup = SymbolLookup.libraryLookup("zstd", arena);
    }

    validateZstdVersion(linker, symbolLookup);

    return new Zstd(arena, linker, symbolLookup);
  }

  public boolean isError(long result) {
    try {
      if (isErrorMethodHandle == null) {
        isErrorMethodHandle =
            linker.downcallHandle(
                symbolLookup.findOrThrow("ZSTD_isError"),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG),
                Linker.Option.critical(false));
      }
      //noinspection DataFlowIssue
      long returnValue = (long) isErrorMethodHandle.invokeExact(result);
      return returnValue == 1;
    } catch (Throwable e) {
      throw new ZstdException(e);
    }
  }

  public ZstdCompressionContext createCompressionContext() {
    return createCompressionContext(19);
  }

  public ZstdCompressionContext createCompressionContext(int compressionLevel) {
    if (!compressMethodHandlesInitialized) {
      // create context
      createCCtxMethodHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_createCCtx"),
              FunctionDescriptor.of(ValueLayout.ADDRESS),
              Linker.Option.critical(false));

      // compress
      compressCCtxMethodHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_compressCCtx"),
              FunctionDescriptor.of(
                  ValueLayout.JAVA_LONG,
                  ValueLayout.ADDRESS,
                  ValueLayout.ADDRESS,
                  ValueLayout.JAVA_LONG,
                  ValueLayout.ADDRESS,
                  ValueLayout.JAVA_LONG,
                  ValueLayout.JAVA_INT),
              Linker.Option.critical(true));

      // compress size
      compressBoundMethodHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_compressBound"),
              FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG),
              Linker.Option.critical(false));

      // free context
      freeCCtxMethodHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_freeCCtx"),
              FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
              Linker.Option.critical(false));

      compressMethodHandlesInitialized = true;
    }

    // requireNonNull to suppress false positive compiler warnings
    return ZstdCompressionContext.create(
        this,
        requireNonNull(createCCtxMethodHandle),
        requireNonNull(compressCCtxMethodHandle),
        requireNonNull(compressBoundMethodHandle),
        requireNonNull(freeCCtxMethodHandle),
        compressionLevel);
  }

  public ZstdDecompressionContext createDecompressionContext() {
    if (!decompressMethodHandlesInitialized) {
      // create context
      createDCtxMethodHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_createDCtx"),
              FunctionDescriptor.of(ValueLayout.ADDRESS),
              Linker.Option.critical(false));

      // decompress
      decompressDCtxMethodHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_decompressDCtx"),
              FunctionDescriptor.of(
                  ValueLayout.JAVA_LONG,
                  ValueLayout.ADDRESS,
                  ValueLayout.ADDRESS,
                  ValueLayout.JAVA_LONG,
                  ValueLayout.ADDRESS,
                  ValueLayout.JAVA_LONG),
              Linker.Option.critical(true));

      // free context
      freeDCtxMethodHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_freeDCtx"),
              FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
              Linker.Option.critical(false));

      decompressMethodHandlesInitialized = true;
    }

    // requireNonNull to suppress false positive compiler warnings
    return ZstdDecompressionContext.create(
        this,
        requireNonNull(createDCtxMethodHandle),
        requireNonNull(decompressDCtxMethodHandle),
        requireNonNull(freeDCtxMethodHandle));
  }

  private static void validateZstdVersion(Linker linker, SymbolLookup symbolLookup) {
    try {
      MethodHandle versionHandle =
          linker.downcallHandle(
              symbolLookup.findOrThrow("ZSTD_versionNumber"),
              FunctionDescriptor.of(ValueLayout.JAVA_INT),
              Linker.Option.critical(false));

      @SuppressWarnings("DataFlowIssue")
      int version = (int) versionHandle.invokeExact();
      if (version < 10507 || version >= 20000) {
        throw new IllegalStateException(
            "Unsupported zstd version %d.%d.%d, minimum required version is v1.5.7"
                .formatted(version / 10000, version / 100 % 100, version % 100));
      }
    } catch (Throwable t) {
      throw new ZstdException(t);
    }
  }

  @Override
  public void close() {
    arena.close();
  }
}
