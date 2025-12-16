package org.molgenis.vipannotate.format.vcf;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.BufferedLineReader;

@RequiredArgsConstructor
public class VcfHeaderParser {
  private final VcfMetaInfoLineParser vcfMetaInfoLineParser;
  private final VcfHeaderLineParser vcfHeaderLineParser;

  public VcfHeader parse(BufferedLineReader reader) {
    List<VcfMetaInfo.Line> metaInfoLines = new ArrayList<>();
    VcfHeaderLine headerLine = null;

    StringBuilder stringBuilder = new StringBuilder(256);
    while (reader.readLineInto(stringBuilder) != -1) {
      String line = stringBuilder.toString();
      stringBuilder.setLength(0);

      if (line.startsWith("##")) {
        metaInfoLines.add(vcfMetaInfoLineParser.parseLine(line));
      } else if (line.startsWith("#")) {
        headerLine = vcfHeaderLineParser.parseLine(line);
        break;
      } else {
        throw new VcfParserException(
            "invalid vcf line '%s' encountered before vcf header line".formatted(line));
      }
    }

    if (headerLine == null) {
      throw new VcfParserException("end of file encountered before vcf header line");
    }

    return new VcfHeader(new VcfMetaInfo(metaInfoLines), headerLine);
  }
}
