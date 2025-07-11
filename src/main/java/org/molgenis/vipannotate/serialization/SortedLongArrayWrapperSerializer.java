package org.molgenis.vipannotate.serialization;

import static java.util.Objects.requireNonNull;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
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
public class SortedLongArrayWrapperSerializer extends Serializer<SortedIntArrayWrapper> {
  private final ArraySerializers.LongArraySerializer longArraySerializer;
  private final IntegratedLongCompressor integratedIntCompressor;

  public SortedLongArrayWrapperSerializer(Fury fury) {
    super(fury, SortedIntArrayWrapper.class);
    this.longArraySerializer = new ArraySerializers.IntArraySerializer(fury);
    this.integratedIntCompressor = new IntegratedIntCompressor();
  }

  @Override
  public void write(MemoryBuffer buffer, SortedIntArrayWrapper value) {
    int[] compressed = requireNonNull(integratedIntCompressor.compress(value.array()));
    longArraySerializer.write(buffer, compressed);
  }

  @Override
  public SortedIntArrayWrapper read(MemoryBuffer buffer) {
    int[] intArray = longArraySerializer.read(buffer);
    int[] uncompress = requireNonNull(integratedIntCompressor.uncompress(intArray));
    return new SortedIntArrayWrapper(uncompress);
  }
}
