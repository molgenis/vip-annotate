package org.molgenis.vipannotate.vcf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VcfHeaderParser {
  @NonNull VcfMetaInfoLineParser vcfMetaInfoLineParser;
  @NonNull VcfHeaderLineParser vcfHeaderLineParser;

  public VcfHeader parse(@NonNull BufferedReader bufferedReader) {
    List<VcfMetaInfo.Line> metaInfoLines = new ArrayList<>();
    VcfHeaderLine headerLine = null;

    String line;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        if (line.startsWith("##")) {
          metaInfoLines.add(vcfMetaInfoLineParser.parseLine(line));
        } else if (line.startsWith("#")) {
          headerLine = vcfHeaderLineParser.parseLine(line);
          break;
        } else {
          throw new VcfParserException("invalid vcf line '%s' encountered before vcf header line");
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    if (line == null) {
      throw new VcfParserException("end of file encountered before vcf header line");
    }

    return new VcfHeader(new VcfMetaInfo(metaInfoLines), headerLine);
  }
}
