package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class VcfRecordTest {
  private Chrom chrom;
  private Pos pos;
  private Id id;
  private Ref ref;
  private Alt alt;
  private Qual qual;
  private Filter filter;
  private Info info;
  private Genotype genotype;
  private VcfRecord vcfRecord;

  @BeforeEach
  void setUp() {
    chrom = Chrom.wrap("chr1");
    pos = Pos.wrap("123");
    id = Id.wrap("id0.");
    ref = Ref.wrap("A");
    alt = Alt.wrap("C,T");
    qual = Qual.wrap("1.23");
    filter = Filter.wrap("PASS");
    info = Info.wrap("KEY=VALUE");
    genotype = Genotype.wrap("GT\t0/1");
    vcfRecord = new VcfRecord(new Field[] {chrom, pos, id, ref, alt, qual, filter, info, genotype});
  }

  @Test
  void getChrom() {
    assertEquals(chrom, vcfRecord.getChrom());
  }

  @Test
  void getPos() {
    assertEquals(pos, vcfRecord.getPos());
  }

  @Test
  void getId() {
    assertEquals(id, vcfRecord.getId());
  }

  @Test
  void getRef() {
    assertEquals(ref, vcfRecord.getRef());
  }

  @Test
  void getAlt() {
    assertEquals(alt, vcfRecord.getAlt());
  }

  @Test
  void getQual() {
    assertEquals(qual, vcfRecord.getQual());
  }

  @Test
  void getFilter() {
    assertEquals(filter, vcfRecord.getFilter());
  }

  @Test
  void getInfo() {
    assertEquals(info, vcfRecord.getInfo());
  }

  @Test
  void getGenotype() {
    assertEquals(genotype, vcfRecord.getGenotype());
  }

  @Test
  void getGenotypeNull() {
    vcfRecord = new VcfRecord(new Field[] {chrom, pos, id, ref, alt, qual, filter, info});
    assertNull(vcfRecord.getGenotype());
  }

  @Test
  void resetAndWrite() throws IOException {
    String dataLine = "chr2\t124\tid1\tAA\tCC,TT\t2.34\tLQ\tKEY2=VALUE2\tGT\t0|1";
    vcfRecord.reset(dataLine);
    try (StringWriter stringWriter = new StringWriter()) {
      vcfRecord.write(stringWriter);
      assertEquals(dataLine + '\n', stringWriter.toString());
    }
  }

  @Test
  void resetAndWriteGenoType() throws IOException {
    VcfRecord vcfRecord = new VcfRecord(new Field[] {chrom, pos, id, ref, alt, qual, filter, info});
    String dataLine = "chr2\t124\tid1\tAA\tCC,TT\t2.34\tLQ\tKEY2=VALUE2";
    vcfRecord.reset(dataLine);
    try (StringWriter stringWriter = new StringWriter()) {
      vcfRecord.write(stringWriter);
      assertEquals(dataLine + '\n', stringWriter.toString());
    }
  }
}
