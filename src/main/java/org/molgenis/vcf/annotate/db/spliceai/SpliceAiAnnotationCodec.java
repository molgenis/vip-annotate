package org.molgenis.vcf.annotate.db.spliceai;

import java.util.Arrays;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.Quantized8UnitIntervalDoublePrimitive;

public class SpliceAiAnnotationCodec {
  public static byte[] encode(SpliceAiAnnotation annotation) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(9);

    byte quantizedDeltaScoreAcceptorGain =
        Quantized8UnitIntervalDoublePrimitive.toByte(annotation.getDeltaScoreAcceptorGain());
    byte quantizedDeltaScoreAcceptorLoss =
        Quantized8UnitIntervalDoublePrimitive.toByte(annotation.getDeltaScoreAcceptorLoss());
    byte quantizedDeltaScoreDonorGain =
        Quantized8UnitIntervalDoublePrimitive.toByte(annotation.getDeltaScoreDonorGain());
    byte quantizedDeltaScoreDonorLoss =
        Quantized8UnitIntervalDoublePrimitive.toByte(annotation.getDeltaScoreDonorLoss());
    byte deltaPositionAcceptorGain = annotation.getDeltaPositionAcceptorGain();
    byte deltaPositionAcceptorLoss = annotation.getDeltaPositionAcceptorLoss();
    byte deltaPositionDonorGain = annotation.getDeltaPositionDonorGain();
    byte deltaPositionDonorLoss = annotation.getDeltaPositionDonorLoss();

    boolean writeQuantizedDeltaScoreAcceptorGain = quantizedDeltaScoreAcceptorGain != 0;
    boolean writeQuantizedDeltaScoreAcceptorLoss = quantizedDeltaScoreAcceptorLoss != 0;
    boolean writeQuantizedDeltaScoreDonorGain = quantizedDeltaScoreDonorGain != 0;
    boolean writeQuantizedDeltaScoreDonorLoss = quantizedDeltaScoreDonorLoss != 0;
    boolean writeDeltaPositionAcceptorGain = deltaPositionAcceptorGain != 0;
    boolean writeDeltaPositionAcceptorLoss = deltaPositionAcceptorLoss != 0;
    boolean writeDeltaPositionDonorGain = deltaPositionDonorGain != 0;
    boolean writeDeltaPositionDonorLoss = deltaPositionDonorLoss != 0;

    byte controlBits =
        (byte)
            ((writeQuantizedDeltaScoreAcceptorGain ? 1 : 0) << 7
                | (writeQuantizedDeltaScoreAcceptorLoss ? 1 : 0) << 6
                | (writeQuantizedDeltaScoreDonorGain ? 1 : 0) << 5
                | (writeQuantizedDeltaScoreDonorLoss ? 1 : 0) << 4
                | (writeDeltaPositionAcceptorGain ? 1 : 0) << 3
                | (writeDeltaPositionAcceptorLoss ? 1 : 0) << 2
                | (writeDeltaPositionDonorGain ? 1 : 0) << 1
                | (writeDeltaPositionDonorLoss ? 1 : 0));
    memoryBuffer.writeByte(controlBits);
    if (writeQuantizedDeltaScoreAcceptorGain) {
      memoryBuffer.writeByte(quantizedDeltaScoreAcceptorGain);
    }
    if (writeQuantizedDeltaScoreAcceptorLoss) {
      memoryBuffer.writeByte(quantizedDeltaScoreAcceptorLoss);
    }
    if (writeQuantizedDeltaScoreDonorGain) {
      memoryBuffer.writeByte(quantizedDeltaScoreDonorGain);
    }
    if (writeQuantizedDeltaScoreDonorLoss) {
      memoryBuffer.writeByte(quantizedDeltaScoreDonorLoss);
    }
    if (writeDeltaPositionAcceptorGain) {
      memoryBuffer.writeByte(deltaPositionAcceptorGain);
    }
    if (writeDeltaPositionAcceptorLoss) {
      memoryBuffer.writeByte(deltaPositionAcceptorLoss);
    }
    if (writeDeltaPositionDonorGain) {
      memoryBuffer.writeByte(deltaPositionDonorGain);
    }
    if (writeDeltaPositionDonorLoss) {
      memoryBuffer.writeByte(deltaPositionDonorLoss);
    }

    return Arrays.copyOfRange(memoryBuffer.getHeapMemory(), 0, memoryBuffer.writerIndex());
  }

  public static SpliceAiAnnotation decode(MemoryBuffer memoryBuffer) {
    // FIXME use control bits
    float deltaScoreAcceptorGain =
        Quantized8UnitIntervalDoublePrimitive.toFloat(memoryBuffer.readByte());
    float deltaScoreAcceptorLoss =
        Quantized8UnitIntervalDoublePrimitive.toFloat(memoryBuffer.readByte());
    float deltaScoreDonorGain =
        Quantized8UnitIntervalDoublePrimitive.toFloat(memoryBuffer.readByte());
    float deltaScoreDonorLoss =
        Quantized8UnitIntervalDoublePrimitive.toFloat(memoryBuffer.readByte());
    byte deltaPositionAcceptorGain = memoryBuffer.readByte();
    byte deltaPositionAcceptorLoss = memoryBuffer.readByte();
    byte deltaPositionDonorGain = memoryBuffer.readByte();
    byte deltaPositionDonorLoss = memoryBuffer.readByte();
    return new SpliceAiAnnotation(
        deltaScoreAcceptorGain,
        deltaScoreAcceptorLoss,
        deltaScoreDonorGain,
        deltaScoreDonorLoss,
        deltaPositionAcceptorGain,
        deltaPositionAcceptorLoss,
        deltaPositionDonorGain,
        deltaPositionDonorLoss);
  }
}
