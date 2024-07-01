package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TranscriptRef implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull TranscriptCatalog transcriptCatalog;
  String id;
}
