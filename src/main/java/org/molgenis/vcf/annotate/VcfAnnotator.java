package org.molgenis.vcf.annotate;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.model.Consequence;
import org.molgenis.vcf.annotate.model.FeatureType;
import org.molgenis.vcf.annotate.util.ContigUtils;

@RequiredArgsConstructor
public class VcfAnnotator {
  @NonNull private final GenomeAnnotationDb genomeAnnotationDb;

  public long annotate(VCFIterator reader, VariantContextWriter writer) throws IOException {
    VCFHeader vcfHeader = reader.getHeader();
    vcfHeader.addMetaDataLine(
        new VCFInfoHeaderLine(
            "CSQ",
            VCFHeaderLineCount.UNBOUNDED,
            VCFHeaderLineType.String,
            "Consequence annotations from VIP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|ALLELE_NUM|STRAND"));
    writer.writeHeader(vcfHeader);

    long records = 0;
    while (reader.hasNext()) {
      VariantContext vcfRecord = reader.next();
      VariantContext annotatedVcfRecord = annotate(vcfRecord);
      writer.add(annotatedVcfRecord);
      records++;
    }
    return records;
  }

  private VariantContext annotate(VariantContext vcfRecord) {
    // determine annotations per alternative allele
    List<AlleleAnnotation> annotationList = new ArrayList<>();
    for (int i = 0; i < vcfRecord.getNAlleles() - 1; i++) {
      annotationList.addAll(annotate(vcfRecord, i));
    }
    if (annotationList.isEmpty()) return vcfRecord;

    // create variant context with annotations
    List<String> attributeValue =
        annotationList.stream().map(VcfAnnotator::createAttributeValue).toList();
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(vcfRecord);
    variantContextBuilder.attribute("CSQ", attributeValue);

    return variantContextBuilder.make();
  }

  private List<AlleleAnnotation> annotate(VariantContext vcfRecord, int altAlleleIndex) {
    Chromosome chromosome = ContigUtils.map(vcfRecord.getContig());
    AnnotationDb annotationDb = genomeAnnotationDb.get(chromosome);
    VariantTranscriptConsequenceAnnotator variantTranscriptConsequenceAnnotator =
        new VariantTranscriptConsequenceAnnotator(annotationDb);

    Allele alt = vcfRecord.getAlternateAllele(altAlleleIndex);

    AlleleAnnotation.AlleleAnnotationBuilder builder = AlleleAnnotation.builder();
    builder.alleleNum(altAlleleIndex);
    builder.allele(alt.getDisplayString());

    int start = vcfRecord.getStart();
    Allele ref = vcfRecord.getReference();
    if (ref.isSymbolic()
        || ref.isSingleBreakend()
        || ref.isBreakpoint()
        || ref.getDisplayBases().length != 1) {
      // FIXME support symbolic alleles
      // FIXME support breakends
      // FIXME support non-SNPs
      return Collections.emptyList();
    }

    if (alt.isSymbolic()
        || alt.isSingleBreakend()
        || alt.isBreakpoint()
        || alt.getDisplayBases().length != 1) {
      // FIXME support symbolic alleles
      // FIXME support breakends
      // FIXME support non-SNPs
      return Collections.emptyList();
    }

    // determine annotations
    List<AlleleAnnotation> annotations = new ArrayList<>();

    List<Transcript> transcripts = annotationDb.findOverlapTranscripts(start, start);

    if (!transcripts.isEmpty()) {
      for (Transcript transcript : transcripts) {
        Gene gene = annotationDb.getGene(transcript);
        Strand strand = gene.getStrand();

        TranscriptAnnotation transcriptAnnotation =
            variantTranscriptConsequenceAnnotator.annotate(
                start, ref.getBases(), alt.getBases(), strand, transcript);

        builder.geneSymbol(gene.getName());
        builder.gene(gene.getId());
        builder.strand(strand);
        builder.featureType(FeatureType.TRANSCRIPT);
        builder.hgvsC(transcriptAnnotation.getHgvsC());
        builder.hgvsP(transcriptAnnotation.getHgvsP());

        builder.consequence(transcriptAnnotation.getConsequence());

        builder.feature(transcript.getId());
        builder.biotype(gene.getBioType());
        builder.exon(transcriptAnnotation.getExon());
        builder.intron(transcriptAnnotation.getIntron());
        annotations.add(builder.build());
      }
    } else {
      builder.consequence(Consequence.INTERGENIC_VARIANT);
      annotations.add(builder.build());
    }

    return annotations;
  }

  private static String createAttributeValue(AlleleAnnotation annotation) {
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
    Integer gene = annotation.getGene();
    values.add(gene != null ? String.valueOf(gene) : "");
    FeatureType featureType = annotation.getFeatureType();
    values.add(
        featureType != null
            ? switch (featureType) {
              case TRANSCRIPT -> "transcript";
            }
            : "");

    String feature = annotation.getFeature();
    values.add(feature != null ? feature : "");

    Gene.BioType biotype = annotation.getBiotype();
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
              case NEGATIVE -> "0";
            }
            : "");

    return String.join("|", values);
  }
}
