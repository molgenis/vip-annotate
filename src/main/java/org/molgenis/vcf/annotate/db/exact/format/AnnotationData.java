package org.molgenis.vcf.annotate.db.exact.format;

import org.apache.fury.memory.MemoryBuffer;

// TODO idea: variant alt allele annotations with a fixed size do not need variantOffsets array
public record AnnotationData(SortedIntArrayWrapper variantOffsets, byte[] variantsBytes) {
  public MemoryBuffer get(int index) {
    int[] variantOffsetsArr = variantOffsets.array();

    int variantOffset = variantOffsetsArr[index];
    int length =
        (index < variantOffsetsArr.length - 1 ? variantOffsetsArr[index + 1] : variantsBytes.length)
            - variantOffset;
    return MemoryBuffer.fromByteArray(variantsBytes, variantOffset, length);
  }
}
