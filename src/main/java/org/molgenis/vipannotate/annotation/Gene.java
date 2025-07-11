package org.molgenis.vipannotate.annotation;

/**
 * example: ENSG00000012048 --> source=Source.ENSEMBL identifier=12048
 *
 * <p>example: HGNC:1100 --> source=Source.HGNC identifier=1100
 *
 * <p>example: 672 --> source: Source.REFSEQ identifier=672
 *
 * @param source gene identifier source
 * @param identifier gene identifier
 */
public record Gene(Source source, int identifier) {
  public enum Source {
    HGNC,
    ENSEMBL,
    REFSEQ,
    OMIM
  }
}
