package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BedField {
  @JsonProperty(value = "chrom")
  CHROM(0),
  @JsonProperty(value = "chromStart")
  CHROM_START(1),
  @JsonProperty(value = "chromEnd")
  CHROM_END(2),
  @JsonProperty(value = "name")
  NAME(3),
  @JsonProperty(value = "score")
  SCORE(4),
  @JsonProperty(value = "strand")
  STRAND(5),
  @JsonProperty(value = "thickStart")
  THICK_START(6),
  @JsonProperty(value = "thickEnd")
  THICK_END(7),
  @JsonProperty(value = "itemRgb")
  ITEM_RGB(8),
  @JsonProperty(value = "blockCount")
  BLOCK_COUNT(9),
  @JsonProperty(value = "blockSizes")
  BLOCK_SIZES(10),
  @JsonProperty(value = "blockStarts")
  BLOCK_STARTS(11);

  @Getter private final int colIndex;
}
