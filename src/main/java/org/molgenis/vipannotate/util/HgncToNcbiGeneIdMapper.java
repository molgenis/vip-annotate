package org.molgenis.vipannotate.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class HgncToNcbiGeneIdMapper {
  private final Map<String, Integer> hgncToNcbiGeneIdMap;

  public @Nullable Integer map(String hgncGeneSymbol) {
    return hgncToNcbiGeneIdMap.get(hgncGeneSymbol);
  }

  // TODO move to parser class
  /**
   * download from <a href="https://www.ncbi.nlm.nih.gov/datasets/gene/taxon/9606/">NCBI</a> with
   * 'Selected columns' 'GeneID' and 'Symbol'.
   */
  public static HgncToNcbiGeneIdMapper create(Path ncbiGeneTsvPath) {
    Map<String, Integer> hgncToNcbiGeneIdMap = new HashMap<>();
    try (BufferedReader bufferedReader = createReader(ncbiGeneTsvPath)) {
      bufferedReader.readLine(); // skip header
      for (TsvIterator tsvIterator = new TsvIterator(bufferedReader); tsvIterator.hasNext(); ) {
        String[] tokens = tsvIterator.next();
        hgncToNcbiGeneIdMap.put(tokens[1], Integer.parseInt(tokens[0]));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new HgncToNcbiGeneIdMapper(hgncToNcbiGeneIdMap);
  }

  // TODO deduplicate with fai and other places
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
