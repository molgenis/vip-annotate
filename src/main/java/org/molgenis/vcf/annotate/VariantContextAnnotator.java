package org.molgenis.vcf.annotate;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.model.Consequence;
import org.molgenis.vcf.annotate.model.FeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariantContextAnnotator {
  private static final Logger LOGGER = LoggerFactory.getLogger(VariantContextAnnotator.class);

  private final GenomeAnnotationDb genomeAnnotationDb;
  private final VariantConsequenceCalculator variantConsequenceCalculator;

  public VariantContextAnnotator(
      GenomeAnnotationDb genomeAnnotationDb,
      VariantConsequenceCalculator variantConsequenceCalculator) {
    this.genomeAnnotationDb = requireNonNull(genomeAnnotationDb);
    this.variantConsequenceCalculator = requireNonNull(variantConsequenceCalculator);
  }

  public VariantContext annotate(VariantContext variantContext) {
    // determine annotations per alternative allele
    List<VariantContextAlleleAnnotation> annotationList = new ArrayList<>();
    for (int i = 0; i < variantContext.getNAlleles() - 1; i++) {
      annotationList.addAll(annotate(variantContext, i));
    }
    if (annotationList.isEmpty()) return variantContext;

    // create variant context with annotations
    List<String> attributeValue =
        annotationList.stream().map(VariantContextAnnotator::createAttributeValue).toList();
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute("CSQ", attributeValue);

    return variantContextBuilder.make();
  }

  private List<VariantContextAlleleAnnotation> annotate(VariantContext variantContext, int i) {
    Allele alt = variantContext.getAlternateAllele(i);

    VariantContextAlleleAnnotation.VariantContextAlleleAnnotationBuilder builder =
        VariantContextAlleleAnnotation.builder();
    builder.alleleNum(i);
    builder.allele(alt.getDisplayString());

    // FIXME skip difficult stuff for now
    Chromosome chromosome = Chromosome.from(variantContext.getContig());
    int start = variantContext.getStart();
    Allele ref = variantContext.getReference();
    if (ref.isSymbolic()
        || ref.isSingleBreakend()
        || ref.isBreakpoint()
        || ref.getDisplayBases().length != 1) {
      LOGGER.debug("skipping {}:{} {}", chromosome, start, ref.getDisplayString());
      return Collections.emptyList();
    }

    if (alt.isSymbolic()
        || alt.isSingleBreakend()
        || alt.isBreakpoint()
        || alt.getDisplayBases().length != 1) {
      LOGGER.debug(
          "skipping {}:{} {}>{}",
          chromosome,
          start,
          ref.getDisplayString(),
          alt.getDisplayString());
      return Collections.emptyList();
    }

    // determine annotations
    List<VariantContextAlleleAnnotation> annotations = new ArrayList<>();

    List<Transcript> transcripts = genomeAnnotationDb.findTranscripts(chromosome, start, start);

    if (!transcripts.isEmpty()) {
      for (Transcript transcript : transcripts) {
        // FIXME
        //        builder.geneSymbol((String) feature.attributes().attributeMap().get("gene_name"));
        builder.strand(transcript.getStrand());
        builder.featureType(FeatureType.TRANSCRIPT);

        Consequence consequence =
            variantConsequenceCalculator.determineConsequenceTranscriptVariant(
                chromosome, start, ref, alt, transcript);
        builder.consequence(consequence);

        String feature =
            transcript.getTranscriptRefs().stream()
                .map(
                    transcriptRef -> {
                      String catalog =
                          switch (transcriptRef.getTranscriptCatalog()) {
                            case CHESS -> "Chess";
                            case GENCODE -> "GENCODE";
                            case REFSEQ -> "RefSeq";
                          };
                      return transcriptRef.getId() != null
                          ? catalog + ":" + transcriptRef.getId()
                          : catalog;
                    })
                .collect(Collectors.joining(","));
        builder.feature(feature);

        // FIXME
        //        String geneType = (String) feature.attributes().attributeMap().get("gene_type");
        //        if (geneType != null) {
        //          builder.biotype(
        //              switch (geneType) {
        //                case "protein_coding" -> GeneType.PROTEIN_CODING;
        //                case "antisense_RNA" -> GeneType.ANTISENSE_RNA;
        //                case "lncRNA" -> GeneType.LNC_RNA;
        //                case "snRNA" -> GeneType.SN_RNA;
        //                case "telomerase_RNA" -> GeneType.TELOMERASE_RNA;
        //                case "RNase_MRP_RNA" -> GeneType.RNASE_MRP_RNA;
        //                case "transcribed_pseudogene" -> GeneType.TRANSCRIBED_PSEUDOGENE;
        //                case "snoRNA" -> GeneType.SNO_RNA;
        //                case "miRNA" -> GeneType.MI_RNA;
        //                default -> throw new IllegalStateException("Unexpected value: " +
        // geneType);
        //              });
        //        }
        annotations.add(builder.build());
      }
    } else {
      builder.consequence(Consequence.INTERGENIC_VARIANT);
    }

    return annotations;
  }

  private static String createAttributeValue(VariantContextAlleleAnnotation annotation) {
    List<String> values = new ArrayList<>();
    values.add(annotation.getAllele());
    Consequence consequence = annotation.getConsequence();
    values.add(consequence.getTerm());
    values.add(
        switch (consequence.getImpact()) {
          case HIGH -> "HIGH";
          case MODERATE -> "MODERATE";
          case LOW -> "LOW";
          case MODIFIER -> "MODIFIER";
        });
    String geneSymbol = annotation.getGeneSymbol();
    values.add(geneSymbol != null ? geneSymbol : "");

    FeatureType featureType = annotation.getFeatureType();
    values.add(
        featureType != null
            ? switch (featureType) {
              case TRANSCRIPT -> "transcript";
            }
            : "");

    String feature = annotation.getFeature();
    values.add(feature != null ? feature : "");

    Gene.Type biotype = annotation.getBiotype();
    values.add(
        biotype != null
            ? switch (biotype) {
              case ANTISENSE_RNA -> "antisense_RNA";
              case C_REGION -> "C_region";
              case J_SEGMENT -> "J_segment";
              case LNC_RNA -> "lncRNA";
              case MI_RNA -> "miRNA";
              case MISC_RNA -> "miscRNA";
              case NC_RNA -> "ncRNA";
              case NC_RNA_PSEUDOGENE -> "ncRNA_pseudogene";
              case PROTEIN_CODING -> "protein_coding";
              case PSEUDOGENE -> "pseudogene";
              case R_RNA -> "rRNA";
              case RNASE_MRP_RNA -> "RNase_MRP_RNA";
              case SC_RNA -> "scRNA";
              case SN_RNA -> "snRNA";
              case SNO_RNA -> "snoRNA";
              case TEC -> "TEC";
              case T_RNA -> "tRNA";
              case TELOMERASE_RNA -> "telomerase_RNA";
              case TRANSCRIBED_PSEUDOGENE -> "transcribed_pseudogene";
              case V_SEGMENT -> "v_segment";
              case V_SEGMENT_PSEUDOGENE -> "v_segment_pseudogene";
            }
            : "");

    values.add(String.valueOf(annotation.getAlleleNum()));
    Strand strand = annotation.getStrand();
    values.add(
        strand != null
            ? switch (strand) {
              case PLUS -> "+";
              case MINUS -> "-";
              case UNKNOWN -> "";
            }
            : "");

    return String.join("|", values);
  }
}
