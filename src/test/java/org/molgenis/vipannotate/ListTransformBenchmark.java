package org.molgenis.vipannotate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 3, time = 2)
public class ListTransformBenchmark {

  @State(Scope.Benchmark)
  public static class BenchmarkState {
    @Param({"1", "2", "5", "10", "20"})
    public int listSize;

    public List<String> stringList;

    @Setup(Level.Trial)
    public void setUp() {
      stringList = new ArrayList<>();
      for (int i = 0; i < listSize; i++) {
        stringList.add(" " + i + " ");
      }
    }
  }

  /**
   *
   *
   * <pre>
   * MiscBenchmark.benchmarkEnhancedFor         avgt   10  232,569 ±  29,083  ns/op
   * MiscBenchmark.benchmarkFor                 avgt   10  245,942 ±  35,558  ns/op
   * MiscBenchmark.benchmarkForEach             avgt   10  225,313 ±  34,281  ns/op
   * MiscBenchmark.benchmarkForEachRemaining    avgt   10  303,162 ± 122,161  ns/op
   * MiscBenchmark.benchmarkStream              avgt   10  244,278 ±  40,749  ns/op
   * MiscBenchmark.benchmarkStreamToCollection  avgt   10  234,998 ±  45,214  ns/op
   * </pre>
   */
  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder().include(ListTransformBenchmark.class.getSimpleName()).build();
    new Runner(opt).run();
  }

  @Benchmark
  public void benchmarkFor(Blackhole blackhole, BenchmarkState state) {
    int size = state.stringList.size();
    List<String> trimmedStringList = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      trimmedStringList.add(state.stringList.get(i).trim());
    }
    blackhole.consume(trimmedStringList);
  }

  @Benchmark
  public void benchmarkEnhancedFor(Blackhole blackhole, BenchmarkState state) {
    int size = state.stringList.size();
    List<String> trimmedStringList = new ArrayList<>(size);
    for (String string : state.stringList) {
      trimmedStringList.add(string.trim());
    }
    blackhole.consume(trimmedStringList);
  }

  @Benchmark
  public void benchmarkForEach(Blackhole blackhole, BenchmarkState state) {
    int size = state.stringList.size();
    List<String> trimmedStringList = new ArrayList<>(size);
    state.stringList.forEach(string -> trimmedStringList.add(string.trim()));
    blackhole.consume(trimmedStringList);
  }

  @Benchmark
  public void benchmarkForEachRemaining(Blackhole blackhole, BenchmarkState state) {
    int size = state.stringList.size();
    List<String> trimmedStringList = new ArrayList<>(size);
    state.stringList.spliterator().forEachRemaining(string -> trimmedStringList.add(string.trim()));
    blackhole.consume(trimmedStringList);
  }

  @Benchmark
  public void benchmarkStream(Blackhole blackhole, BenchmarkState state) {
    List<String> trimmedStringList = state.stringList.stream().map(String::trim).toList();
    blackhole.consume(trimmedStringList);
  }

  @Benchmark
  public void benchmarkStreamToCollection(Blackhole blackhole, BenchmarkState state) {
    int size = state.stringList.size();
    List<String> trimmedStringList =
        state.stringList.stream()
            .map(String::trim)
            .collect(Collectors.toCollection(() -> new ArrayList<>(size)));
    blackhole.consume(trimmedStringList);
  }
}
