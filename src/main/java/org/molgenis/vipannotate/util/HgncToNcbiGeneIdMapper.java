package org.molgenis.vipannotate.util;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HgncToNcbiGeneIdMapper {
  private final Map<String, Integer> hgncToNcbiGeneIdMap;

  public @Nullable Integer map(String hgncGeneSymbol) {
    return hgncToNcbiGeneIdMap.get(hgncGeneSymbol);
  }

  /**
   * download from <a href="https://www.ncbi.nlm.nih.gov/datasets/gene/taxon/9606/">NCBI</a> with
   * 'Selected columns' 'GeneID' and 'Symbol'.
   */
  public static HgncToNcbiGeneIdMapper create(Path ncbiGeneTsvPath) {
    int capacity = (int) Math.ceil(192436 / 0.75);
    Map<String, Integer> hgncToNcbiGeneIdMap = new HashMap<>(capacity);

    try (BufferedReader bufferedReader = Readers.newBufferedReaderUtf8(ncbiGeneTsvPath)) {
      // parse header
      String headerLine = bufferedReader.readLine();
      if (headerLine == null || !headerLine.equals("NCBI GeneID\tSymbol")) {
        throw new IOException(
            "'%s' is missing expected header 'NCBI GeneID<tab>Symbol'".formatted(headerLine));
      }

      // parse records
      for (TsvIterator tsvIterator = new TsvIterator(bufferedReader); tsvIterator.hasNext(); ) {
        String[] tokens = tsvIterator.next();
        hgncToNcbiGeneIdMap.put(tokens[1], Integer.parseInt(tokens[0]));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new HgncToNcbiGeneIdMapper(hgncToNcbiGeneIdMap);
  }
}
