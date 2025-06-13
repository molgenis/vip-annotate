package org.molgenis.vipannotate.serialization;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.serializer.ArraySerializers;
import org.apache.fury.serializer.Serializer;

/**
 * Serializer for unsorted int arrays.
 *
 * @see <a href="https://fury.apache.org/docs/specification/fury_java_serialization_spec#array">Fury
 *     Java Serialization Format</a>
 */
public class SortedIntArrayWrapperSerializer extends Serializer<SortedIntArrayWrapper> {
  private final ArraySerializers.IntArraySerializer intArraySerializer;
  private final IntegratedIntCompressor integratedIntCompressor;

  public SortedIntArrayWrapperSerializer(Fury fury) {
    super(fury, SortedIntArrayWrapper.class);
    this.intArraySerializer = new ArraySerializers.IntArraySerializer(fury);
    this.integratedIntCompressor = new IntegratedIntCompressor();
  }

  @Override
  public void write(MemoryBuffer buffer, SortedIntArrayWrapper value) {
    int[] compressed = integratedIntCompressor.compress(value.array());
    intArraySerializer.write(buffer, compressed);
  }

  @Override
  public SortedIntArrayWrapper read(MemoryBuffer buffer) {
    int[] intArray = intArraySerializer.read(buffer);
    return new SortedIntArrayWrapper(integratedIntCompressor.uncompress(intArray));
  }
}
