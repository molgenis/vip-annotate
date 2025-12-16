package org.molgenis.vipannotate.util;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileChannelUtils {
  private FileChannelUtils() {}

  /** force changes to both the file content (but not metadata) to be written to storage */
  public static void force(FileChannel fileChannel) {
    try {
      fileChannel.force(false);
    } catch (IOException e) {
      ExceptionUtils.handleThrowable(e);
    }
  }

  public static void forceAll(FileChannel... fileChannels) {
    Throwable firstThrowable = null;

    for (FileChannel fileChannel : fileChannels) {
      try {
        fileChannel.force(false);
      } catch (Throwable throwable) {
        if (firstThrowable == null) {
          firstThrowable = throwable;
        } else {
          firstThrowable.addSuppressed(throwable);
        }
      }
    }

    ExceptionUtils.handleThrowable(firstThrowable);
  }
}
