package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryEncodable;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexSmallWriter<T extends SequenceVariant>
    implements MemoryEncodable<AnnotationIndex<T>> {
  private final MemoryBufferFactory memBufferFactory;
  private final StreamVByte streamVByte;

  @Override
  public MemoryEncoder writeTo(AnnotationIndex<T> object) {
    MemoryEncoder memEncoder = MemoryEncoder.wrap(memBufferFactory.newMemoryBuffer());
    writeInto(object, memEncoder);
    return memEncoder;
  }

  @Override
  public void writeInto(AnnotationIndex<T> index, MemoryEncoder memEncoder) {
    SequenceVariantAnnotationIndexSmall<T> indexSmall = getTyped(index);
    int[] encodedVariantsArray = indexSmall.getEncodedVariantsArray();
    long maxCompressedBytes = streamVByte.maxCompressedBytes(encodedVariantsArray.length);

    // write number of index values
    memEncoder.putVarInt(encodedVariantsArray.length);

    // encode index and write to buffer
    MemoryBuffer memBuffer =
        memEncoder.unwrap(); // FIXME replace with functional method on memBuffer?
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
