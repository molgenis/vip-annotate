package org.molgenis.vcf.annotate;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.molgenis.vcf.annotate.db.model.Gene;
import org.molgenis.vcf.annotate.db.model.Strand;
import org.molgenis.vcf.annotate.db.model.Transcript;
import org.molgenis.vcf.annotate.model.Consequence;
import org.molgenis.vcf.annotate.model.FeatureType;

@Value
@Builder(toBuilder = true)
public class AlleleAnnotation {
  @NonNull String allele;
  @NonNull @Singular List<Consequence> consequences;
  String geneSymbol;
  Integer gene;
  Transcript.Type featureType;
  String feature;
  Gene.BioType biotype;
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
