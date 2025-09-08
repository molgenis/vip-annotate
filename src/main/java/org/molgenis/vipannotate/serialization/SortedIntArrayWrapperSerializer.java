package org.molgenis.vipannotate.serialization;

import static java.util.Objects.requireNonNull;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import org.apache.fory.Fory;
import org.apache.fory.memory.MemoryBuffer;
import org.apache.fory.serializer.ArraySerializers;
import org.apache.fory.serializer.Serializer;

/**
 * Serializer for sorted int arrays.
 *
 * @see <a href="https://fory.apache.org/docs/specification/fory_java_serialization_spec#array">Fory
 *     Java Serialization Format</a>
 */
public class SortedIntArrayWrapperSerializer extends Serializer<SortedIntArrayWrapper> {
  private final ArraySerializers.IntArraySerializer intArraySerializer;
  private final IntegratedIntCompressor integratedIntCompressor;

  public SortedIntArrayWrapperSerializer(Fory fory) {
    super(fory, SortedIntArrayWrapper.class);
    this.intArraySerializer = new ArraySerializers.IntArraySerializer(fory);
    this.integratedIntCompressor = new IntegratedIntCompressor();
  }

  @Override
  public void write(MemoryBuffer buffer, SortedIntArrayWrapper value) {
    int[] compressed = requireNonNull(integratedIntCompressor.compress(value.array()));
    intArraySerializer.write(buffer, compressed);
  }

  @Override
  public SortedIntArrayWrapper read(MemoryBuffer buffer) {
    int[] intArray = intArraySerializer.read(buffer);
    int[] uncompress = requireNonNull(integratedIntCompressor.uncompress(intArray));
    return new SortedIntArrayWrapper(uncompress);
  }
}
