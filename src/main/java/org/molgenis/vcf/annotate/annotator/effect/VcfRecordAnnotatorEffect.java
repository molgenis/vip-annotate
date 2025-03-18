package org.molgenis.vcf.annotate.annotator.effect;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;
import org.molgenis.vcf.annotate.annotator.VcfRecordAnnotator;
import org.molgenis.vcf.annotate.annotator.effect.model.Consequence;
import org.molgenis.vcf.annotate.annotator.effect.model.Impact;
import org.molgenis.vcf.annotate.db.effect.model.*;
import org.molgenis.vcf.annotate.db.effect.utils.AnnotationDbImpl;
import org.molgenis.vcf.annotate.util.ContigUtils;

@RequiredArgsConstructor
public class VcfRecordAnnotatorEffect implements VcfRecordAnnotator {
  @NonNull private AnnotationDbImpl genomeAnnotationDb;

  @Override
  public void updateHeader(VCFHeader vcfHeader) {
    vcfHeader.addMetaDataLine(
        new VCFInfoHeaderLine(
            "CSQ",
            VCFHeaderLineCount.UNBOUNDED,
            VCFHeaderLineType.String,
            "Consequence annotations from VIP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|ALLELE_NUM|STRAND"));
  }

  @Override
  public void annotate(VariantContext vcfRecord, VariantContextBuilder vcfRecordBuilder) {
    // determine annotations per alternative allele
    List<AlleleAnnotation> annotationList = new ArrayList<>();
    int nrAltAlleles = vcfRecord.getNAlleles() - 1;
    for (int i = 0; i < nrAltAlleles; i++) {
      List<AlleleAnnotation> altAlleleAnnotations = annotate(vcfRecord, i);
      if (!altAlleleAnnotations.isEmpty()) {
        annotationList.addAll(altAlleleAnnotations);
      }
    }
    if (annotationList.isEmpty()) return;

    // create variant context with annotations
    List<String> attributeValue = new ArrayList<>(annotationList.size());
    for (AlleleAnnotation alleleAnnotation : annotationList) {
      attributeValue.add(createAttributeValue(alleleAnnotation));
    }
    vcfRecordBuilder.attribute("CSQ", attributeValue);
  }

  public static VcfRecordAnnotatorEffect create(Path annotationsZip) {
    AnnotationDbImpl genomeAnnotationDb = new AnnotationDbImpl(annotationsZip);
    return new VcfRecordAnnotatorEffect(genomeAnnotationDb);
  }

  public static int NR_VAR_ALTS_ANNOTATED = 0;

  private List<AlleleAnnotation> annotate(VariantContext vcfRecord, int altAlleleIndex) {
    Chromosome chromosome = ContigUtils.map(vcfRecord.getContig());
    if (chromosome == null) {
      return Collections.emptyList();
    }
    AnnotationDb annotationDb = this.genomeAnnotationDb.get(chromosome);
    if (annotationDb == null) {
      return Collections.emptyList();
    }
    SnpTranscriptEffectAnnotator snpTranscriptEffectAnnotator =
        new SnpTranscriptEffectAnnotator(annotationDb, true);

    Allele alt = vcfRecord.getAlternateAllele(altAlleleIndex);

    int start = vcfRecord.getStart();
    Allele ref = vcfRecord.getReference();
    if (ref.isSymbolic() || ref.isSingleBreakend() || ref.isBreakpoint()) {
      // FIXME support symbolic alleles
      // FIXME support breakends
      return Collections.emptyList();
    }

    if (alt.isSymbolic() || alt.isSingleBreakend() || alt.isBreakpoint()) {
      // FIXME support symbolic alleles
      // FIXME support breakends
      return Collections.emptyList();
    }

    if (vcfRecord.getEnd() - vcfRecord.getStart() != 0) {
      // FIXME support INDEL
      return Collections.emptyList();
    }

    NR_VAR_ALTS_ANNOTATED++;
    // determine annotations
    List<AlleleAnnotation> annotations = new ArrayList<>();

    List<Transcript> transcripts = annotationDb.findOverlapTranscripts(start, vcfRecord.getEnd());

    if (!transcripts.isEmpty()) {
      for (Transcript transcript : transcripts) {
        Gene gene = annotationDb.getGene(transcript);
        Strand strand = gene.getStrand();

        // TODO merge AlleleAnnotation and VariantEffect
        VariantEffect variantEffect =
            snpTranscriptEffectAnnotator.annotateTranscriptVariant(
                start, ref.getBases(), alt.getBases(), strand, transcript);

        AlleleAnnotation.AlleleAnnotationBuilder builder = AlleleAnnotation.builder();
        builder.alleleNum(1 + altAlleleIndex);
        builder.allele(alt.getDisplayString());
        builder.geneSymbol(gene.getName());
        builder.gene(gene.getId());
        builder.strand(strand);
        builder.featureType("Transcript");
        builder.hgvsC(variantEffect.getHgvsC());
        builder.hgvsP(variantEffect.getHgvsP());

        builder.consequences(variantEffect.getConsequences());

        builder.feature(transcript.getId());
        builder.biotype(transcript.getType());
        if (variantEffect.getExonNumber() != null) {
          builder.exon(variantEffect.getExonNumber() + "/" + variantEffect.getExonTotal());
        }
        if (variantEffect.getIntronNumber() != null) {
          builder.intron(variantEffect.getIntronNumber() + "/" + variantEffect.getIntronTotal());
        }
        annotations.add(builder.build());
      }
    } else {
      AlleleAnnotation.AlleleAnnotationBuilder builder = AlleleAnnotation.builder();
      builder.alleleNum(1 + altAlleleIndex);
      builder.allele(alt.getDisplayString());
      builder.consequence(Consequence.INTERGENIC_VARIANT);
      annotations.add(builder.build());
    }

    return annotations;
  }

  private static String createAttributeValue(AlleleAnnotation annotation) {
    List<String> values = new ArrayList<>();
    values.add(annotation.getAllele());
    List<Consequence> consequences = annotation.getConsequences();
    values.add(
        consequences.stream()
            .sorted(Comparator.comparingInt(o -> o.getImpact().ordinal()))
            .map(Consequence::getTerm)
            .collect(Collectors.joining("&")));
    Impact impact = Impact.MODIFIER;
    for (Consequence consequence : consequences) {
      if (consequence.getImpact().ordinal() < impact.ordinal()) impact = consequence.getImpact();
    }
    values.add(
        switch (impact) {
          case HIGH -> "HIGH";
          case MODERATE -> "MODERATE";
          case LOW -> "LOW";
          case MODIFIER -> "MODIFIER";
        });
    String geneSymbol = annotation.getGeneSymbol();
    values.add(geneSymbol != null ? geneSymbol : "");
    Integer gene = annotation.getGene();
    values.add(gene != null ? String.valueOf(gene) : "");
    values.add(annotation.getFeatureType() != null ? annotation.getFeatureType() : "");

    String feature = annotation.getFeature();
    values.add(feature != null ? feature : "");

    Transcript.Type biotype = annotation.getBiotype();
    values.add(biotype != null ? biotype.getTerm() : "");
    values.add(annotation.getExon() != null ? annotation.getExon() : "");
    values.add(annotation.getIntron() != null ? annotation.getIntron() : "");
    values.add(annotation.getHgvsC() != null ? annotation.getHgvsC() : "");
    values.add(annotation.getHgvsP() != null ? annotation.getHgvsP() : "");
    values.add(String.valueOf(annotation.getAlleleNum()));
    Strand strand = annotation.getStrand();
    values.add(
        strand != null
            ? switch (strand) {
              case POSITIVE -> "1";
              case NEGATIVE -> "-1";
            }
            : "");

    return String.join("|", values);
  }

  @Value
  @Builder(toBuilder = true)
  public static class AlleleAnnotation {
    @NonNull String allele;
    @NonNull @Singular List<Consequence> consequences;
    String geneSymbol;
    Integer gene;
    String featureType;
    String feature;
    Transcript.Type biotype;
    String exon;
    String intron;
    String hgvsC;
    String hgvsP;
    Strand strand;
    int alleleNum;
    /*
    Allele                done
    Consequence           done
    IMPACT                done
    SYMBOL                done
    Gene                  done
    Feature_type          done
    Feature               done
    BIOTYPE               done
    EXON                  done
    INTRON                done
    HGVSc                 done
    HGVSp                 done
    cDNA_position
    CDS_position
    Protein_position
    Amino_acids
    Codons
    Existing_variation
    ALLELE_NUM            done
    DISTANCE
    STRAND                done
    FLAGS
    PICK
    SYMBOL_SOURCE
    HGNC_ID
    REFSEQ_MATCH
    REFSEQ_OFFSET
    SOURCE
    SIFT
    PolyPhen
    HGVS_OFFSET
    CLIN_SIG
    SOMATIC
    PHENO
    PUBMED
    CHECK_REF
    MOTIF_NAME
    MOTIF_POS
    HIGH_INF_POS
    MOTIF_SCORE_CHANGE
    TRANSCRIPTION_FACTORS
    Grantham
    SpliceAI_pred_DP_AG
    SpliceAI_pred_DP_AL
    SpliceAI_pred_DP_DG
    SpliceAI_pred_DP_DL
    SpliceAI_pred_DS_AG
    SpliceAI_pred_DS_AL
    SpliceAI_pred_DS_DG
    SpliceAI_pred_DS_DL
    SpliceAI_pred_SYMBOL
    CAPICE_CL
    CAPICE_SC
    existing_InFrame_oORFs
    existing_OutOfFrame_oORFs
    existing_uORFs
    five_prime_UTR_variant_annotation
    five_prime_UTR_variant_consequence
    IncompletePenetrance
    InheritanceModesGene
    VKGL
    VKGL_CL
    gnomAD_AF
    gnomAD_COV
    gnomAD_FAF95
    gnomAD_FAF99
    gnomAD_HN
    gnomAD_QC
    gnomAD_SRC
    clinVar_CLNID
    clinVar_CLNREVSTAT
    clinVar_CLNSIG
    clinVar_CLNSIGINCL
    ASV_ACMG_class
    ASV_AnnotSV_ranking_criteria
    ASV_AnnotSV_ranking_score
    ALPHSCORE
    ncER
    FATHMM_MKL_NC
    ReMM
    phyloP
    */
  }
}
