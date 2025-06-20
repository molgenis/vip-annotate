package org.molgenis.vipannotate.format.fasta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.util.TsvIterator;

public class FastaIndexParser {

  public static FastaIndex create(Path faiPath) {
    FastaIndex fastaIndex = new FastaIndex();

    try (BufferedReader bufferedReader = createReader(faiPath)) {
      for (TsvIterator tsvIterator = new TsvIterator(bufferedReader); tsvIterator.hasNext(); ) {
        FastaIndexRecord fastaIndexRecord = createRecord(tsvIterator.next());
        fastaIndex.addRecord(fastaIndexRecord);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return fastaIndex;
  }

  private static FastaIndexRecord createRecord(String[] tokens) {
    String name = tokens[0];
    int length = Integer.parseInt(tokens[1]);
    long offset = Long.parseLong(tokens[2]);
    int lineBases = Integer.parseInt(tokens[3]);
    int lineWidth = Integer.parseInt(tokens[4]);

    return new FastaIndexRecord(name, length, offset, lineBases, lineWidth);
  }

  private static BufferedReader createReader(Path faiPath) {
    if (Files.notExists(faiPath)) {
      throw new UncheckedIOException(new FileNotFoundException(faiPath.toString()));
    }

    final int bufferedReaderBufferSize = 32768;
    try {
      return new BufferedReader(
          new InputStreamReader(new FileInputStream(faiPath.toFile()), StandardCharsets.UTF_8),
          bufferedReaderBufferSize);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
