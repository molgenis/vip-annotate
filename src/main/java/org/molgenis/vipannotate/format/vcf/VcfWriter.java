package org.molgenis.vipannotate.format.vcf;

import java.io.*;
import java.util.List;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.ZeroCopyBufferedWriter;

public class VcfWriter implements AutoCloseable {
  private final ZeroCopyBufferedWriter writer;

  VcfWriter(ZeroCopyBufferedWriter writer) {
    this.writer = writer;
  }

  public static VcfWriter create(Writer writer) {
    return new VcfWriter(new ZeroCopyBufferedWriter(writer));
  }

  public void writeHeader(VcfHeader vcfHeader) {
    vcfHeader.write(writer);
    writer.flushBuffer();
  }

  public void write(VcfRecord vcfRecord) {
    vcfRecord.write(writer);
    writer.flushBuffer();
  }

  public void write(List<VcfRecord> vcfRecords) {
    for (VcfRecord vcfRecord : vcfRecords) {
      vcfRecord.write(writer);
    }
    writer.flushBuffer();
  }

  @Override
  public void close() {
    ClosableUtils.close(writer);
  }
}
