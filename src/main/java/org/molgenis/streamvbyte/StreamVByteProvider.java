package org.molgenis.streamvbyte;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.ClosableUtils;

public enum StreamVByteProvider implements AutoCloseable {
  INSTANCE;

  @Nullable private static StreamVByte streamVByte;

  public StreamVByte get() {
    if (streamVByte == null) {
      streamVByte = StreamVByte.create();
    }
    return streamVByte;
  }

  @Override
  public void close() {
    ClosableUtils.close(streamVByte);
    streamVByte = null;
  }
}
