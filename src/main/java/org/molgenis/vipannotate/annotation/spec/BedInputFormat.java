package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.nio.file.Path;

@JsonTypeName("bed")
public record BedInputFormat(Path file, BedField from) implements InputFormat {
  @Override
  public InputFormatType type() {
    return InputFormatType.BED;
  }
}
