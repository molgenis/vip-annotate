package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexSmallWriter<T extends SequenceVariant>
    implements MemoryBufferWriter<AnnotationIndex<T>> {
  private final MemoryBufferFactory memBufferFactory;
  private final StreamVByte streamVByte;

  @Override
  public MemoryBuffer writeTo(AnnotationIndex<T> object) {
    MemoryBuffer memBuffer = memBufferFactory.newMemoryBuffer();
    writeInto(object, memBuffer);
    return memBuffer;
  }

  @Override
  public void writeInto(AnnotationIndex<T> index, MemoryBuffer memBuffer) {
    SequenceVariantAnnotationIndexSmall<T> indexSmall = getTyped(index);
    int[] encodedVariantsArray = indexSmall.getEncodedVariantsArray();
    long maxCompressedBytes = streamVByte.maxCompressedBytes(encodedVariantsArray.length);

    // write number of index values
    memBuffer.putVarUnsignedInt(encodedVariantsArray.length);

    // encode index and write to buffer
    long pos = memBuffer.getPosition();
    memBuffer.ensureCapacity(pos + maxCompressedBytes);
    memBuffer.setLimit(pos + maxCompressedBytes);

    long nrBytesWritten =
        streamVByte.deltaEncode(
            MemorySegment.ofArray(encodedVariantsArray),
            memBuffer.getMemSegment(),
            encodedVariantsArray.length);

    // update limit and position
    memBuffer.setPosition(pos + nrBytesWritten);
  }

  // TODO dedup with reader
  private SequenceVariantAnnotationIndexSmall<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexSmall<T> indexSmall)) {
      throw new IllegalArgumentException(
          "index must be of type SequenceVariantAnnotationIndexSmall");
    }
    return indexSmall;
  }
}
