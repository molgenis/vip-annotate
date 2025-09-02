package org.molgenis.vipannotate.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.ContigRegistry;

/** genomic region parser */
@RequiredArgsConstructor
public class RegionParser {
  private final ContigRegistry contigRegistry;

  public List<Region> parse(String str) {
    String[] tokens = str.split(",", -1);

    List<Region> regions;
    if (tokens.length == 0) {
      throw new RuntimeException("Empty interval"); // FIXME proper exception and message
    } else if (tokens.length == 1) {
      Region region = parseRegion(tokens[0]);
      return Collections.singletonList(region);
    } else {
      regions = new ArrayList<>(tokens.length);
      for (String token : tokens) {
        Region region = parseRegion(token);
        regions.add(region);
      }
    }
    return regions;
  }

  private Region parseRegion(String token) {
    int index = token.indexOf(':');

    Contig contig;
    Integer start = null, stop = null;
    if (index == -1) {
      contig = parseContig(token);
    } else {
      contig = parseContig(token.substring(0, index));

      int startStopSepIndex = token.indexOf('-', index + 1);
      if (startStopSepIndex == -1) {
        start = Integer.parseInt(token.substring(index + 1));
        stop = contig.getLength();
      } else {
        start = Integer.parseInt(token.substring(index + 1, startStopSepIndex));
        stop = Integer.parseInt(token.substring(startStopSepIndex + 1));
      }
    }
    return new Region(contig, start, stop);
  }

  private Contig parseContig(String str) {
    Contig contig = contigRegistry.getContig(str);
    if (contig == null) {
      throw new RuntimeException("Invalid contig"); // FIXME proper exception and message
    }
    return contig;
  }
}
