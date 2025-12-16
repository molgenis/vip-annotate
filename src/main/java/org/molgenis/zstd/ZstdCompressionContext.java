package org.molgenis.zstd;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ZstdCompressionContext implements AutoCloseable {
  private final Zstd zstd;
  private final MemorySegment compressionContextMemorySegment;
  private final MethodHandle compressCCtxMethodHandle;
  private final MethodHandle compressBoundMethodHandle;
  private final MethodHandle freeCCtxMethodHandle;
  private final int compressionLevel;

  public long compress(MemorySegment dstMemorySegment, MemorySegment srcMemorySegment) {
    long returnValue;
    try {
      //noinspection DataFlowIssue
      returnValue =
          (long)
              compressCCtxMethodHandle.invokeExact(
                  compressionContextMemorySegment,
                  dstMemorySegment,
                  dstMemorySegment.byteSize(),
                  srcMemorySegment,
                  srcMemorySegment.byteSize(),
                  compressionLevel);
    } catch (Throwable e) {
      throw new ZstdException(e);
    }

    if (zstd.isError(returnValue)) {
      throw new ZstdException("error compressing memory segment");
    }
    return returnValue;
  }

  public long compressBound(MemorySegment srcMemorySegment) {
    long returnValue;
    try {
      //noinspection DataFlowIssue
      returnValue = (long) compressBoundMethodHandle.invokeExact(srcMemorySegment.byteSize());
    } catch (Throwable e) {
      throw new ZstdException(e);
    }

    if (zstd.isError(returnValue)) {
      throw new ZstdException("error determining compress bound for memory segment");
    }
    return returnValue;
  }

  static ZstdCompressionContext create(
      Zstd zstd,
      MethodHandle createCCtxMethodHandle,
      MethodHandle compressCCtxMethodHandle,
      MethodHandle compressBoundMethodHandle,
      MethodHandle freeCCtxMethodHandle,
      int compressionLevel) {
    MemorySegment createCCtxMemorySegment;
    try {
      createCCtxMemorySegment = (MemorySegment) createCCtxMethodHandle.invokeExact();
    } catch (Throwable e) {
      throw new ZstdException(e);
    }

    //noinspection DataFlowIssue
    return new ZstdCompressionContext(
        zstd,
        createCCtxMemorySegment,
        compressCCtxMethodHandle,
        compressBoundMethodHandle,
        freeCCtxMethodHandle,
        compressionLevel);
  }

  @Override
  public void close() {
    long returnValue;
    try {
      //noinspection DataFlowIssue
      returnValue = (long) freeCCtxMethodHandle.invokeExact(compressionContextMemorySegment);
    } catch (Throwable e) {
      throw new ZstdException(e);
    }

    if (zstd.isError(returnValue)) {
      Logger.error("error closing zstd compression context");
    }
  }
}
