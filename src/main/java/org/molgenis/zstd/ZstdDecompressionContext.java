package org.molgenis.zstd;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ZstdDecompressionContext implements AutoCloseable {
  private final Zstd zstd;
  private final MemorySegment decompressionContextMemorySegment;
  private final MethodHandle decompressDCtxMethodHandle;
  private final MethodHandle freeDCtxMethodHandle;

  public long decompress(MemorySegment dstMemorySegment, MemorySegment srcMemorySegment) {
    long returnValue;
    try {
      //noinspection DataFlowIssue
      returnValue =
          (long)
              decompressDCtxMethodHandle.invokeExact(
                  decompressionContextMemorySegment,
                  dstMemorySegment,
                  dstMemorySegment.byteSize(),
                  srcMemorySegment,
                  srcMemorySegment.byteSize());
    } catch (Throwable e) {
      throw new ZstdException(e);
    }

    if (zstd.isError(returnValue)) {
      throw new ZstdException("error decompressing memory segment");
    }
    return returnValue;
  }

  static ZstdDecompressionContext create(
      Zstd zstd,
      MethodHandle createDCtxMethodHandle,
      MethodHandle decompressDCtxMethodHandle,
      MethodHandle freeDCtxMethodHandle) {
    MemorySegment createDCtxMemorySegment;
    try {
      createDCtxMemorySegment = (MemorySegment) createDCtxMethodHandle.invokeExact();
    } catch (Throwable e) {
      throw new ZstdException(e);
    }
    //noinspection DataFlowIssue
    return new ZstdDecompressionContext(
        zstd, createDCtxMemorySegment, decompressDCtxMethodHandle, freeDCtxMethodHandle);
  }

  @Override
  public void close() {
    long returnValue;
    try {
      //noinspection DataFlowIssue
      returnValue = (long) freeDCtxMethodHandle.invokeExact(decompressionContextMemorySegment);
    } catch (Throwable e) {
      throw new ZstdException(e);
    }

    if (zstd.isError(returnValue)) {
      Logger.error("error closing zstd decompression context");
    }
  }
}
