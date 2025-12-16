package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("DataFlowIssue")
class BufferedLineReaderTest {
  private static Stream<Arguments> readLineIntoProvider() {
    return Stream.of(
        Arguments.of("aaa\nbbb\nccc\n"),
        Arguments.of("aa\r\nbb\ncc\r\n"),
        Arguments.of("aa\nbb\ncc"),
        Arguments.of("\n\n\n"));
  }

  @ParameterizedTest
  @MethodSource("readLineIntoProvider")
  void readLineInto(String str) throws IOException {
    List<String> lines = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();
    int nrCharsRead;
    try (BufferedLineReader bufferedLineReader = new BufferedLineReader(new StringReader(str), 5)) {
      int totalCharsRead = 0;
      while ((nrCharsRead = bufferedLineReader.readLineInto(stringBuilder)) != -1) {
        totalCharsRead += nrCharsRead;
        lines.add(stringBuilder.toString());
        stringBuilder.setLength(0);
      }
      assertEquals(str.replace("\n", "").replace("\r", "").length(), totalCharsRead);
    }

    List<String> expectedLines = new ArrayList<>();
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        expectedLines.add(line);
      }
    }

    assertEquals(expectedLines, lines);
  }
}
