package org.molgenis.vipannotate.annotation.gnomad;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Filter.*;
import static org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source.*;

import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Filter;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class GnomAdAnnotationDatasetEncoderDecoderIT {
  private GnomAdAnnotationDatasetEncoder encoder;
  private GnomAdAnnotationDatasetDecoder decoder;

  @BeforeEach
  void setUp() {
    encoder = new GnomAdAnnotationDatasetEncoder();
    decoder = new GnomAdAnnotationDatasetDecoder();
  }

  @Test
  void encodeDecodeSources() {
    List<Source> sources = List.of(GENOMES, EXOMES, TOTAL, GENOMES, EXOMES, TOTAL);
    try (MemoryBuffer blob =
        encoder.encodeSources(new SizedIterator<>(sources.iterator(), sources.size()))) {

      Executable[] executables = new Executable[sources.size()];
      for (int i = 0, size = sources.size(); i < size; i++) {
        int finalI = i;
        executables[i] =
            () -> assertEquals(sources.get(finalI), decoder.decodeSource(blob, finalI));
      }
      assertAll(executables);
    }
  }

  @Test
  void encodeDecodeAf() {
    List<Double> afList = List.of(1d, 0.1, 0.01, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001);
    try (MemoryBuffer blob =
        encoder.encodeAf(new SizedIterator<>(afList.iterator(), afList.size()))) {

      double maxError = 1 / 131068d;
      Executable[] executables = new Executable[afList.size()];
      for (int i = 0, size = afList.size(); i < size; i++) {
        int finalI = i;
        executables[i] =
            () -> assertEquals(afList.get(finalI), decoder.decodeAf(blob, finalI), maxError);
      }
      assertAll(executables);
    }
  }

  @Test
  void encodeDecodeFaf95() {
    List<Double> afList = List.of(1d, 0.1, 0.01, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001);
    try (MemoryBuffer blob =
        encoder.encodeFaf95(new SizedIterator<>(afList.iterator(), afList.size()))) {

      double maxError = 1 / 131070d;
      Executable[] executables = new Executable[afList.size()];
      for (int i = 0, size = afList.size(); i < size; i++) {
        int finalI = i;
        executables[i] =
            () -> assertEquals(afList.get(finalI), decoder.decodeFaf95(blob, finalI), maxError);
      }
      assertAll(executables);
    }
  }

  @Test
  void encodeDecodeFaf99() {
    List<Double> afList = List.of(1d, 0.1, 0.01, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001);
    try (MemoryBuffer blob =
        encoder.encodeFaf99(new SizedIterator<>(afList.iterator(), afList.size()))) {

      double maxError = 1 / 131070d;
      Executable[] executables = new Executable[afList.size()];
      for (int i = 0, size = afList.size(); i < size; i++) {
        int finalI = i;
        executables[i] =
            () -> assertEquals(afList.get(finalI), decoder.decodeFaf99(blob, finalI), maxError);
      }
      assertAll(executables);
    }
  }

  @Test
  void encodeDecodeHn() {
    List<Integer> hnList = List.of(0, 1, 10, 100, 1000, 10000, 100000);
    try (MemoryBuffer blob =
        encoder.encodeHn(new SizedIterator<>(hnList.iterator(), hnList.size()))) {

      Executable[] executables = new Executable[hnList.size()];
      for (int i = 0, size = hnList.size(); i < size; i++) {
        int finalI = i;
        executables[i] = () -> assertEquals(hnList.get(finalI), decoder.decodeHn(blob, finalI));
      }
      assertAll(executables);
    }
  }

  @Test
  void encodeFilters() {
    List<EnumSet<Filter>> filters =
        List.of(
            EnumSet.noneOf(Filter.class),
            EnumSet.of(AC0),
            EnumSet.of(AS_VQSR),
            EnumSet.of(INBREEDING_COEFF),
            EnumSet.of(AC0, AS_VQSR),
            EnumSet.of(AC0, INBREEDING_COEFF),
            EnumSet.of(AS_VQSR, INBREEDING_COEFF),
            EnumSet.of(AC0, AS_VQSR, INBREEDING_COEFF));

    try (MemoryBuffer blob =
        encoder.encodeFilters(new SizedIterator<>(filters.iterator(), filters.size()))) {

      Executable[] executables = new Executable[filters.size()];
      for (int i = 0, size = filters.size(); i < size; i++) {
        int finalI = i;
        executables[i] =
            () -> assertEquals(filters.get(finalI), decoder.decodeFilters(blob, finalI));
      }
      assertAll(executables);
    }
  }

  @Test
  void encodeDecodeCov() {
    List<Double> afList = List.of(1d, 0.1, 0.01, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001);
    try (MemoryBuffer blob =
        encoder.encodeCov(new SizedIterator<>(afList.iterator(), afList.size()))) {

      double maxError = 1 / 131070d;
      Executable[] executables = new Executable[afList.size()];
      for (int i = 0, size = afList.size(); i < size; i++) {
        int finalI = i;
        executables[i] =
            () -> assertEquals(afList.get(finalI), decoder.decodeCov(blob, finalI), maxError);
      }
      assertAll(executables);
    }
  }
}
