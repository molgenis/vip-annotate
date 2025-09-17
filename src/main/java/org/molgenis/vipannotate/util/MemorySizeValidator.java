package org.molgenis.vipannotate.util;

import static java.util.Objects.requireNonNull;

import java.lang.management.ManagementFactory;
import java.util.List;

public class MemorySizeValidator {
  static final long ONE_MB = 1024L * 1024L;
  static final long THRESHOLD_DIRECT_MEMORY = 512 * ONE_MB;
  static final long THRESHOLD_HEAP_SIZE =
      Math.floorDiv(
          256 * ONE_MB * 9, 10); // 90% because getting actual max heap size is an approximation

  /**
   * throws a RuntimeException if max direct memory size is less than 512MB or max heap size is less
   * than 256MB.
   */
  public static void validateMemorySizes() {
    if (GraalVm.isGraalVmRuntime()) {
      return; // GraalVM native image does not use a JVM, no validation required
    }

    long maxHeapSize = getJvmMaxHeapSizeApproximation();
    if (maxHeapSize < THRESHOLD_HEAP_SIZE) {
      throw new InvalidMaxHeapSizeException(THRESHOLD_HEAP_SIZE, maxHeapSize);
    }

    long maxDirectMemory = getJvmMaxDirectMemorySize();
    if (maxDirectMemory < THRESHOLD_DIRECT_MEMORY) {
      throw new InvalidDirectMemorySizeException(THRESHOLD_DIRECT_MEMORY, maxDirectMemory);
    }
  }

  private static long getJvmMaxDirectMemorySize() {
    List<String> jvmInputArgs =
        requireNonNull(requireNonNull(ManagementFactory.getRuntimeMXBean()).getInputArguments());

    for (String arg : jvmInputArgs) {
      if (arg.startsWith("-XX:MaxDirectMemorySize=")) {
        String valueStr = arg.substring("-XX:MaxDirectMemorySize=".length());
        return parseMemorySize(valueStr);
      }
    }

    return getJvmMaxHeapSizeApproximation(); // default max direct memory size is the max heap size
  }

  private static long getJvmMaxHeapSizeApproximation() {
    return requireNonNull(Runtime.getRuntime()).maxMemory();
  }

  private static long parseMemorySize(String value) {
    value = value.toLowerCase().trim();
    long multiplier = 1;

    if (value.endsWith("k")) {
      multiplier = 1024L;
      value = value.substring(0, value.length() - 1);
    } else if (value.endsWith("m")) {
      multiplier = 1024L * 1024L;
      value = value.substring(0, value.length() - 1);
    } else if (value.endsWith("g")) {
      multiplier = 1024L * 1024L * 1024L;
      value = value.substring(0, value.length() - 1);
    }

    return Long.parseLong(value) * multiplier;
  }
}
