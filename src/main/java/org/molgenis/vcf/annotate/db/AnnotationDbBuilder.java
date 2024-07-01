package org.molgenis.vcf.annotate.db;

import static java.util.Objects.requireNonNull;

import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.db.model.Chromosome;
import org.molgenis.vcf.annotate.db.utils.BitList;
import org.molgenis.vcf.annotate.db.utils.Gff3Parser;
import org.molgenis.vcf.annotate.db.utils.IntervalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationDbBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationDbBuilder.class);

  private final ReferenceSequenceFile referenceSequenceFile;

  public AnnotationDbBuilder(ReferenceSequenceFile referenceSequenceFile) {
    this.referenceSequenceFile = requireNonNull(referenceSequenceFile);
  }

  public GenomeAnnotationDb create(File gff3File) {
    // parse gff3
    List<Gff3Parser.Feature> features = new ArrayList<>();
    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(new FileInputStream(gff3File)), StandardCharsets.UTF_8))) {
      Gff3Parser gff3Parser = new Gff3Parser(bufferedReader);
      Gff3Parser.Feature feature;
      while ((feature = gff3Parser.parseLine()) != null) {
        features.add(feature);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    // pass 1: create genes
    Map<String, Gene> genes = new LinkedHashMap<>();
    for (Gff3Parser.Feature feature : features) {
      if (feature.type().equals("gene")) {
        String geneName = (String) feature.attributes().attributeMap().get("gene_name");
        int nrGenes = geneName != null ? geneName.split(",", -1).length : 1;
        for (int i = 0; i < nrGenes; i++) {
          Gene gene = createGene(feature, i);
          genes.put(feature.getAttributeId(), gene);
        }
      }
    }

    // pass 2: group features by chromosome
    EnumMap<Chromosome, List<Gff3Parser.Feature>> chromosomeFeatureMap =
        new EnumMap<>(Chromosome.class);
    for (Gff3Parser.Feature feature : features) {
      Chromosome chromosome = Chromosome.from(feature.seqid());
      chromosomeFeatureMap.computeIfAbsent(chromosome, k -> new ArrayList<>()).add(feature);
    }

    // create transcript database per chromosome
    EnumMap<Chromosome, AnnotationDb> chromosomeTranscriptDatabaseMap =
        new EnumMap<>(Chromosome.class);
    chromosomeFeatureMap.forEach(
        (chromosome, featureList) -> {
          AnnotationDb annotationDb = createAnnotationChromosomeDb(chromosome, genes, featureList);
          chromosomeTranscriptDatabaseMap.put(chromosome, annotationDb);
        });
    return new GenomeAnnotationDb(chromosomeTranscriptDatabaseMap);
  }

  private Gene createGene(Gff3Parser.Feature feature, int index) {
    Gene.GeneBuilder builder = Gene.builder();
    String geneName = (String) feature.attributes().attributeMap().get("gene_name");
    if (geneName != null) {
      geneName = geneName.split(",", -1)[index].trim();
    } else {
      geneName = feature.getAttributeId();
    }
    builder.name(geneName);
    String geneType = (String) feature.attributes().attributeMap().get("gene_type");
    if (geneType != null) {
      String[] tokens = geneType.split(",", -1);
      if (tokens.length > 1) geneType = tokens[index].trim();
      else geneType = tokens[0];

      builder.type(
          switch (geneType) {
            case "antisense_RNA" -> Gene.Type.ANTISENSE_RNA;
            case "C_region" -> Gene.Type.C_REGION;
            case "J_segment" -> Gene.Type.J_SEGMENT;
            case "lncRNA" -> Gene.Type.LNC_RNA;
            case "miRNA" -> Gene.Type.MI_RNA;
            case "misc_RNA" -> Gene.Type.MISC_RNA;
            case "ncRNA" -> Gene.Type.NC_RNA;
            case "ncRNA_pseudogene" -> Gene.Type.NC_RNA_PSEUDOGENE;
            case "protein_coding" -> Gene.Type.PROTEIN_CODING;
            case "pseudogene" -> Gene.Type.PSEUDOGENE;
            case "rRNA" -> Gene.Type.R_RNA;
            case "RNase_MRP_RNA" -> Gene.Type.RNASE_MRP_RNA;
            case "scRNA" -> Gene.Type.SC_RNA;
            case "snRNA" -> Gene.Type.SN_RNA;
            case "snoRNA" -> Gene.Type.SNO_RNA;
            case "TEC" -> Gene.Type.TEC;
            case "tRNA" -> Gene.Type.T_RNA;
            case "telomerase_RNA" -> Gene.Type.TELOMERASE_RNA;
            case "transcribed_pseudogene" -> Gene.Type.TRANSCRIBED_PSEUDOGENE;
            case "V_segment" -> Gene.Type.V_SEGMENT;
            case "V_segment_pseudogene" -> Gene.Type.V_SEGMENT_PSEUDOGENE;
            default -> {
              System.out.println(geneType);
              yield Gene.Type.V_SEGMENT;
            } // throw new IllegalStateException("Unexpected value: " + geneType);
          });
    }
    if (feature.strand() == null) {
      LOGGER.warn("null strand for " + feature);
      builder.strand(Strand.UNKNOWN);
    } else {
      builder.strand(feature.strand());
    }
    return builder.build();
  }

  private AnnotationDb createAnnotationChromosomeDb(
      Chromosome chromosome, Map<String, Gene> genes, List<Gff3Parser.Feature> featureList) {
    LOGGER.debug("processing chromosome: {}", chromosome.getId());

    // pass 1: create transcripts
    Map<String, Transcript.TranscriptBuilder> transcriptBuilderMap =
        new LinkedHashMap<>(); // preserve order

    featureList.forEach(
        feature -> {
          if (feature.type().equals("transcript")) {
            Transcript.TranscriptBuilder builder = Transcript.builder();
            builder.start(Math.toIntExact(feature.start()));
            builder.length(Math.toIntExact(feature.end() - feature.start() + 1));

            Gene gene = genes.get(feature.getAttributeParent());
            builder.gene(gene);

            String dbXrefValue = (String) feature.attributes().attributeMap().get("db_xref");
            if (dbXrefValue != null) {
              TranscriptCatalog currentTranscriptCatalog = null;
              for (String part : dbXrefValue.split(",", -1)) {
                String[] tokens = part.split(":", -1);
                String id;
                if (tokens.length == 2) {
                  currentTranscriptCatalog =
                      switch (tokens[0]) {
                        case "RefSeq" -> TranscriptCatalog.REFSEQ;
                        case "GENCODE" -> TranscriptCatalog.GENCODE;
                        default ->
                            throw new IllegalStateException("Unexpected value: " + tokens[0]);
                      };
                  id = tokens[1];
                } else {
                  id = tokens[0];
                }
                if (currentTranscriptCatalog == null) throw new RuntimeException();

                builder.transcriptRef(
                    TranscriptRef.builder()
                        .transcriptCatalog(currentTranscriptCatalog)
                        .id(id)
                        .build());
              }
            }

            transcriptBuilderMap.put(feature.getAttributeId(), builder);
          }
        });

    // pass 2: populate transcripts with exons and cds
    List<Cds> requestedCdsSequences = new ArrayList<>();
    featureList.forEach(
        feature -> {
          if (feature.type().equals("exon")) {
            Exon exon =
                Exon.builder()
                    .start(Math.toIntExact(feature.start()))
                    .length(Math.toIntExact(feature.end() - feature.start() + 1))
                    .build();
            transcriptBuilderMap.get(feature.getAttributeParent()).exon(exon);
          } else if (feature.type().equals("CDS")) {
            Cds cds =
                Cds.builder()
                    .start(Math.toIntExact(feature.start()))
                    .length(Math.toIntExact(feature.end() - feature.start() + 1))
                    .build();
            transcriptBuilderMap.get(feature.getAttributeParent()).codingSequence(cds);

            // request sequence data for cds
            requestedCdsSequences.add(cds);
          }
        });

    // pass 3: create transcript db
    List<Transcript> transcripts =
        transcriptBuilderMap.values().stream().map(Transcript.TranscriptBuilder::build).toList();

    IntervalTree.Builder intervalTreeBuilder = new IntervalTree.Builder(transcripts.size());
    transcripts.forEach(
        transcript -> intervalTreeBuilder.add(transcript.getStart(), transcript.getStop()));
    IntervalTree intervalTree = intervalTreeBuilder.build();
    TranscriptDb transcriptDb =
        TranscriptDb.builder().intervalTree(intervalTree).transcripts(transcripts).build();

    // pass 4: create sequence db
    SequenceDb sequenceDb;
    if (!requestedCdsSequences.isEmpty()) {
      // merge cds intervals
      List<IntervalUtils.MutableInterval> mergedIntervals =
          IntervalUtils.mergeIntervals(
              requestedCdsSequences.stream()
                  .map(cds -> new IntervalUtils.MutableInterval(cds.getStart(), cds.getStop()))
                  .toArray(IntervalUtils.MutableInterval[]::new));

      // get sequences
      List<Sequence> sequenceList =
          mergedIntervals.stream()
              .map(interval -> createSequence(chromosome, interval.getStart(), interval.getEnd()))
              .toList();

      // create interval tree
      IntervalTree.Builder sequenceIntervalTreeBuilder =
          new IntervalTree.Builder(mergedIntervals.size());
      sequenceList.forEach(
          sequence ->
              sequenceIntervalTreeBuilder.add(
                  sequence.getStart(), sequence.getStop())); // TODO range ok? [x,y)?

      IntervalTree sequenceIntervalTree = sequenceIntervalTreeBuilder.build();

      sequenceDb =
          SequenceDb.builder().intervalTree(sequenceIntervalTree).sequences(sequenceList).build();
    } else {
      sequenceDb = null;
    }
    return AnnotationDb.builder().transcriptDb(transcriptDb).sequenceDb(sequenceDb).build();
  }

  private Sequence createSequence(Chromosome chromosome, long start, long stop) {
    ReferenceSequence sequence =
        referenceSequenceFile.getSubsequenceAt(chromosome.getId(), start, stop);
    SequenceType sequenceType =
        sequence.getBaseString().contains("N") ? SequenceType.ACTGN : SequenceType.ACTG;
    BitList bitList;
    if (sequenceType == SequenceType.ACTGN) {
      bitList = new BitList(sequence.length() * 3);

      int pos = 0;
      for (byte b : sequence.getBases()) {
        switch (b) {
          case 'A' -> {} // 000
          case 'C' -> bitList.set(pos + 2); // 001
          case 'T' -> bitList.set(pos + 1); // 010
          case 'G' -> { // 011
            bitList.set(pos + 1);
            bitList.set(pos + 2);
          }
          case 'N' -> bitList.set(pos); // 100
          default -> throw new IllegalStateException();
        }
        pos += 3;
      }
    } else {
      bitList = new BitList(sequence.length() * 2);

      int pos = 0;
      for (byte b : sequence.getBases()) {
        switch (b) {
          case 'A' -> {} // 00
          case 'C' -> bitList.set(pos + 1); // 01
          case 'T' -> bitList.set(pos); // 10
          case 'G' -> { // 11
            bitList.set(pos);
            bitList.set(pos + 1);
          }
          default -> throw new IllegalStateException();
        }
        pos += 2;
      }
    }

    return Sequence.builder()
        .start(Math.toIntExact(start))
        .length(Math.toIntExact(stop - start + 1))
        .sequenceType(sequenceType)
        .bits(bitList.getBits())
        .build();
  }
}
