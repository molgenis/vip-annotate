package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vcf.VcfHeader;

@RequiredArgsConstructor
public class InfoVcfHeaderAnnotator implements VcfHeaderAnnotator {
  private final String infoId;
  private final String infoNumber;
  private final String infoType;
  private final String infoDescription;
  private final String infoSource;
  private final String infoVersion;

  @Override
  public HeaderUpdateResult updateHeader(VcfHeader vcfHeader) {
    // TODO refactor so no exception is thrown on diff
    try {
      boolean added =
          vcfHeader
              .vcfMetaInfo()
              .addOrUpdateInfo(
                  infoId, infoNumber, infoType, infoDescription, infoSource, infoVersion);
      return added ? HeaderUpdateResult.ADDED : HeaderUpdateResult.EXISTS_SAME;
    } catch (IllegalArgumentException e) {
      return HeaderUpdateResult.EXISTS_DIFF;
    }
  }
}
