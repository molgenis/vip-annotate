package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

// TODO handle 'phase' https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md
@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Cds extends Interval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
