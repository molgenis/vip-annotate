package org.molgenis.vipannotate.serialization;

import static org.mockito.Mockito.inOrder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemoryEncoderTest {
  @Mock(answer = Answers.CALLS_REAL_METHODS)
  private MemoryEncoder memEncoder;

  @Test
  void putDoubleNullableNonNull() {
    memEncoder.putDoubleNullable(1.23);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(true);
    inOrder.verify(memEncoder).putDouble(1.23);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putDoubleNullableNull() {
    memEncoder.putDoubleNullable(null);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(false);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putFloatNullableNonNull() {
    memEncoder.putFloatNullable(1.23f);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(true);
    inOrder.verify(memEncoder).putFloat(1.23f);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putFloatNullableNull() {
    memEncoder.putFloatNullable(null);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(false);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putIntNullableNonNull() {
    memEncoder.putIntNullable(1);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(true);
    inOrder.verify(memEncoder).putInt(1);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putIntNullableNull() {
    memEncoder.putIntNullable(null);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(false);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putLongNullableNonNull() {
    memEncoder.putLongNullable(1L);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(true);
    inOrder.verify(memEncoder).putLong(1);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putLongNullableNull() {
    memEncoder.putLongNullable(null);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(false);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putShortNullableNonNull() {
    memEncoder.putShortNullable((short) 1);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(true);
    inOrder.verify(memEncoder).putShort((short) 1);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putShortNullableNull() {
    memEncoder.putShortNullable(null);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(false);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putStringNullableNonNull() {
    memEncoder.putStringNullable("abc");

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(true);
    inOrder.verify(memEncoder).putString("abc");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putStringNullableNull() {
    memEncoder.putStringNullable(null);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(false);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putNullableNonNull() {
    memEncoder.putNullable("abc", memEncoder::putString);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(true);
    inOrder.verify(memEncoder).putString("abc");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putNullableNull() {
    memEncoder.putNullable(null, memEncoder::putString);

    InOrder inOrder = inOrder(memEncoder);
    inOrder.verify(memEncoder).putBoolean(false);
    inOrder.verifyNoMoreInteractions();
  }
}
