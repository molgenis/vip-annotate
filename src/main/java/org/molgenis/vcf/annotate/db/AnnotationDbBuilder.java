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
import org.molgenis.vcf.annotate.db.utils.*;
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
    LOGGER.info("parsing {}", gff3File);
    Gff3 gff3 = createGff3(gff3File);

    // create annotation db
    EnumMap<Chromosome, AnnotationDb> chromosomeTranscriptDatabaseMap =
        new EnumMap<>(Chromosome.class);
    gff3.forEach(
        entry -> {
          Chromosome chromosome = Chromosome.from(entry.getKey());
          AnnotationDb annotationDb = createAnnotationDb(chromosome, entry.getValue());
          chromosomeTranscriptDatabaseMap.put(chromosome, annotationDb);
        });

    return new GenomeAnnotationDb(chromosomeTranscriptDatabaseMap);
  }

  private static Gff3 createGff3(File gff3File) {
    Set<String> seqIds = new HashSet<>();
    seqIds.add("NC_000001.11");
    seqIds.add("NC_000002.12");
    seqIds.add("NC_000003.12");
    seqIds.add("NC_000004.12");
    seqIds.add("NC_000005.10");
    seqIds.add("NC_000006.12");
    seqIds.add("NC_000007.14");
    seqIds.add("NC_000008.11");
    seqIds.add("NC_000009.12");
    seqIds.add("NC_000010.11");
    seqIds.add("NC_000011.10");
    seqIds.add("NC_000012.12");
    seqIds.add("NC_000013.11");
    seqIds.add("NC_000014.9");
    seqIds.add("NC_000015.10");
    seqIds.add("NC_000016.10");
    seqIds.add("NC_000017.11");
    seqIds.add("NC_000018.10");
    seqIds.add("NC_000019.10");
    seqIds.add("NC_000020.11");
    seqIds.add("NC_000021.9");
    seqIds.add("NC_000022.11");
    seqIds.add("NC_000023.11"); // X
    seqIds.add("NC_000024.10"); // Y
    // FIXME enable
    // seqIds.add("NC_012920.1"); // MT

    Set<String> sources = new HashSet<>();
    sources.add("RefSeq");
    sources.add("BestRefSeq");
    sources.add("RefSeqFE");
    sources.add("Curated Genomic");
    sources.add("BestRefSeq,Gnomon");
    sources.add("tRNAscan-SE");
    sources.add("Curated Genomic,cmsearch");

    Set<String> attributes = new HashSet<>();
    attributes.add("ID");
    attributes.add("Parent");
    attributes.add("gene");
    attributes.add("gene_biotype");
    attributes.add("transcript_id");
    attributes.add("protein_id");
    attributes.add("Dbxref");

    Gff3 gff3;
    try (Gff3Parser gff3Parser =
        new Gff3Parser(
            new InputStreamReader(
                new GZIPInputStream(new FileInputStream(gff3File)), StandardCharsets.UTF_8))) {
      gff3 = gff3Parser.parse(new Gff3Parser.Filter(seqIds, sources, attributes));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return gff3;
  }

  private Gene createGene(Gff3Parser.Feature feature) {
    Strand strand =
        switch (feature.strand()) {
          case PLUS -> Strand.POSITIVE;
          case MINUS -> Strand.NEGATIVE;
          default -> throw new RuntimeException();
        };
    Gene.GeneBuilder builder = Gene.builder().name(feature.getAttribute("gene")).strand(strand);

    List<String> dbxref = feature.getAttributeAsList("Dbxref");
    if (dbxref != null) {
      for (String ref : dbxref) {
        if (ref.startsWith("GeneID:")) {
          int id = Integer.parseInt(ref.substring("GeneID:".length()));
          builder.id(id);
          break;
        }
      }
    }
    String geneBiotype = feature.getAttribute("gene_biotype");
    builder.bioType(Gene.BioType.from(geneBiotype));

    return builder.build();
  }

  private AnnotationDb createAnnotationDb(Chromosome chromosome, Gff3.Features features) {
    LOGGER.info("processing chromosome {}", chromosome.getId());

    Set<String> exonIds = new HashSet<>();

    Map<String, Integer> geneMap = new LinkedHashMap<>();
    List<Gene> geneList = new ArrayList<>();
    Map<String, TranscriptStub.TranscriptStubBuilder> transcriptBuilderMap =
        new LinkedHashMap<>(); // preserve order

    // pass 1: create transcripts
    List<IntervalUtils.MutableInterval> sequenceIntervals = new ArrayList<>();
    features.forEach(
        feature -> {
          if (isGene(feature)) {
            // create gene
            Gene gene = createGene(feature);
            geneList.add(gene);
            geneMap.put(feature.getAttributeId(), geneList.size() - 1);
          } else if (isTranscript(feature)) {
            // create transcript stub
            TranscriptStub.TranscriptStubBuilder<?, ?> transcriptBuilder =
                TranscriptStub.builder()
                    .start(Math.toIntExact(feature.start()))
                    .length(Math.toIntExact(feature.end() - feature.start() + 1))
                    .id(feature.getAttribute("transcript_id"))
                    .type(Transcript.Type.from(feature.type()))
                    .geneIndex(geneMap.get(feature.getAttributeParent().getFirst()));

            transcriptBuilderMap.put(feature.getAttributeId(), transcriptBuilder);
          } else if (isExon(feature)) {
            String parentFeatureId = feature.getAttributeParent().getFirst();
            TranscriptStub.TranscriptStubBuilder transcriptBuilder =
                transcriptBuilderMap.get(parentFeatureId);

            // FIXME can be null in case of 'miRNA', what to do?
            if (transcriptBuilder != null) {
              // create exon
              Exon exon =
                  Exon.builder()
                      .start(Math.toIntExact(feature.start()))
                      .length(Math.toIntExact(feature.end() - feature.start() + 1))
                      .build();

              // add to transcript stub
              transcriptBuilder.exon(exon);
            }
          } else if (isCds(feature)) {
            String parentFeatureId = feature.getAttributeParent().getFirst();
            TranscriptStub.TranscriptStubBuilder transcriptBuilder =
                transcriptBuilderMap.get(parentFeatureId);

            // FIXME can be null in case of *_gene_segment, and in case of mRNA on MT
            if (transcriptBuilder != null) {
              String proteinId = feature.getAttribute("protein_id");

              // create cds
              CdsStub cds =
                  CdsStub.builder()
                      .start(Math.toIntExact(feature.start()))
                      .length(Math.toIntExact(feature.end() - feature.start() + 1))
                      .phase(
                          switch (feature.phase()) {
                            case ZERO -> (byte) 0;
                            case ONE -> (byte) 1;
                            case TWO -> (byte) 2;
                          })
                      .proteinId(proteinId)
                      .build();

              // add to transcript stub
              transcriptBuilderMap.get(feature.getAttributeParent().getFirst()).codingSequence(cds);

              // store interval for sequence data retrieval
              IntervalUtils.MutableInterval sequenceInterval =
                  new IntervalUtils.MutableInterval(cds.getStart(), cds.getStop());
              sequenceIntervals.add(sequenceInterval);
            }
          }
        });

    // pass 3: create transcript db
    Transcript[] transcripts =
        transcriptBuilderMap.values().stream()
            .map(transcriptStubBuilder -> transcriptStubBuilder.build().createTranscript())
            .toArray(Transcript[]::new);

    IntervalTree.Builder intervalTreeBuilder = new IntervalTree.Builder(transcripts.length);
    for (Transcript transcript : transcripts) {
      // end + 1, because interval tree builder requires [x, y) interval
      intervalTreeBuilder.add(transcript.getStart(), transcript.getStop() + 1);
    }
    IntervalTree intervalTree = intervalTreeBuilder.build();
    TranscriptDb transcriptDb = new TranscriptDb(intervalTree, transcripts);

    // pass 4: create sequence db
    SequenceDb sequenceDb;
    if (!sequenceIntervals.isEmpty()) {
      // merge cds intervals
      List<IntervalUtils.MutableInterval> mergedIntervals =
          IntervalUtils.mergeIntervals(
              sequenceIntervals.toArray(IntervalUtils.MutableInterval[]::new));

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
              // end + 1, because interval tree builder requires [x, y) interval
              sequenceIntervalTreeBuilder.add(sequence.getStart(), sequence.getStop() + 1));

      IntervalTree sequenceIntervalTree = sequenceIntervalTreeBuilder.build();

      sequenceDb = new SequenceDb(sequenceIntervalTree, sequenceList.toArray(Sequence[]::new));
    } else {
      sequenceDb = null;
    }
    return new AnnotationDb(transcriptDb, geneList.toArray(Gene[]::new), sequenceDb);
  }

  // TODO keep casing in sequence? see
  // https://gatk.broadinstitute.org/hc/en-us/articles/360035890951-Human-genome-reference-builds-GRCh38-or-hg38-b37-hg19
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
          case 'A', 'a' -> {} // 000
          case 'C', 'c' -> bitList.set(pos + 2); // 001
          case 'T', 't' -> bitList.set(pos + 1); // 010
          case 'G', 'g' -> { // 011
            bitList.set(pos + 1);
            bitList.set(pos + 2);
          }
          case 'N', 'n' -> bitList.set(pos); // 100
          default -> throw new IllegalStateException();
        }
        pos += 3;
      }
    } else {
      bitList = new BitList(sequence.length() * 2);

      int pos = 0;
      for (byte b : sequence.getBases()) {
        switch (b) {
          case 'A', 'a' -> {} // 00
          case 'C', 'c' -> bitList.set(pos + 1); // 01
          case 'T', 't' -> bitList.set(pos); // 10
          case 'G', 'g' -> { // 11
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

  private boolean isGene(Gff3Parser.Feature feature) {
    return feature.type().equals("gene") || feature.type().equals("pseudogene");
  }

  private boolean isTranscript(Gff3Parser.Feature feature) {
    return feature.hasAttribute("transcript_id") && !isExon(feature);
  }

  private boolean isExon(Gff3Parser.Feature feature) {
    return feature.type().equals("exon");
  }

  private boolean isCds(Gff3Parser.Feature feature) {
    return feature.type().equals("CDS");
  }
}
