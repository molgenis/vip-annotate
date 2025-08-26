package org.molgenis.vipannotate.serialization;


import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.serializer.ArraySerializers;
import org.apache.fury.serializer.Serializer;

/**
 * Serializer for sorted long arrays.
 *
 * @see <a href="https://fury.apache.org/docs/specification/fury_java_serialization_spec#array">Fury
 *     Java Serialization Format</a>
 */
public class SortedLongArrayWrapperSerializer extends Serializer<SortedLongArrayWrapper> {
  private final ArraySerializers.LongArraySerializer longArraySerializer;

  // TODO use long version of IntegratedIntCompressor (does not exist in me.lemire package)
  //  private final IntegratedLongCompressor integratedLongCompressor;

  public SortedLongArrayWrapperSerializer(Fury fury) {
    super(fury, SortedLongArrayWrapper.class);
    this.longArraySerializer = new ArraySerializers.LongArraySerializer(fury);
    //    this.integratedLongCompressor = new IntegratedLongCompressor();
  }

  @Override
  public void write(MemoryBuffer buffer, SortedLongArrayWrapper value) {
    //    long[] compressed = requireNonNull(integratedLongCompressor.compress(value.array()));
    //    longArraySerializer.write(buffer, compressed);
    longArraySerializer.write(buffer, value.array());
  }

  @Override
  public SortedLongArrayWrapper read(MemoryBuffer buffer) {
    long[] longArray = longArraySerializer.read(buffer);
//    int[] uncompress = requireNonNull(integratedLongCompressor.uncompress(longArray));
//    return new SortedLongArrayWrapper(uncompress);
      return  new SortedLongArrayWrapper(longArray);
  }
}
