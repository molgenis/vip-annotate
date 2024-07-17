package org.molgenis.vcf.annotate;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class VcfAnnotatorBenchmarkTest {
  @Test
  public void runJmhBenchmark() throws RunnerException {
    Options opt = new OptionsBuilder().include(VcfAnnotatorBenchmark.class.getSimpleName()).build();
    Collection<RunResult> runResults = new Runner(opt).run();
    assertFalse(runResults.isEmpty());
  }
}
