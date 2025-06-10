package org.molgenis.vipannotate.vcf;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.molgenis.vipannotate.util.CharArrayBuffer;

// TODO write bgzip instead of gzip, see
// https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
public class VcfWriter implements AutoCloseable {
  private final Writer writer;
  private final CharArrayBuffer reusableCharArrayBuffer;

  public VcfWriter(Writer writer) {
    this.writer = requireNonNull(writer);
    this.reusableCharArrayBuffer = new CharArrayBuffer(32768);
  }

  public void writeHeader(VcfHeader vcfHeader) {
    try {
      VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();
      for (VcfMetaInfo.Line line : vcfMetaInfo.lines()) {
        writer.write(line.line());
        writer.write('\n');
      }
      writer.write(vcfHeader.vcfHeaderLine().line());
      writer.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void write(VcfRecord vcfRecord) {
    write(List.of(vcfRecord));
  }

  public void write(Iterable<VcfRecord> vcfRecords) {
    // zero-copy, significantly faster than BufferedWriter
    reusableCharArrayBuffer.clear();

    for (VcfRecord vcfRecord : vcfRecords) {
      write(vcfRecord, reusableCharArrayBuffer);
    }

    try {
      writer.write(reusableCharArrayBuffer.getBuffer(), 0, reusableCharArrayBuffer.getLength());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void write(VcfRecord vcfRecord, CharArrayBuffer reusableCharArrayBuffer) {
    // chrom
    reusableCharArrayBuffer.append(vcfRecord.chrom());
    reusableCharArrayBuffer.append('\t');

    // pos
    reusableCharArrayBuffer.append(Long.toString(vcfRecord.pos()));
    reusableCharArrayBuffer.append('\t');

    // id
    String[] id = vcfRecord.id();
    if (id.length == 0) {
      reusableCharArrayBuffer.append('.');
    } else if (id.length == 1) {
      reusableCharArrayBuffer.append(id[0]);
    } else {
      int i;
      for (i = 0; i < id.length - 1; ++i) {
        reusableCharArrayBuffer.append(id[i]);
        reusableCharArrayBuffer.append(',');
      }
      reusableCharArrayBuffer.append(id[i]);
    }
    reusableCharArrayBuffer.append('\t');

    // ref
    reusableCharArrayBuffer.append(vcfRecord.ref());
    reusableCharArrayBuffer.append('\t');

    // alt
    String[] alt = vcfRecord.alt();
    if (alt.length == 0) {
      reusableCharArrayBuffer.append('.');
    } else if (alt.length == 1) {
      if (alt[0] != null) {
        reusableCharArrayBuffer.append(alt[0]);
      } else {
        reusableCharArrayBuffer.append('.');
      }
    } else {
      int i;
      for (i = 0; i < alt.length - 1; ++i) {
        if (alt[i] != null) {
          reusableCharArrayBuffer.append(alt[i]);
        } else {
          reusableCharArrayBuffer.append('.');
        }
        reusableCharArrayBuffer.append(',');
      }
      if (alt[i] != null) {
        reusableCharArrayBuffer.append(alt[i]);
      } else {
        reusableCharArrayBuffer.append('.');
      }
    }
    reusableCharArrayBuffer.append('\t');

    // qual
    String qual = vcfRecord.qual();
    if (qual != null) {
      reusableCharArrayBuffer.append(qual);
    } else {
      reusableCharArrayBuffer.append('.');
    }
    reusableCharArrayBuffer.append('\t');

    // filter
    String[] filter = vcfRecord.filter();
    if (filter.length == 0) {
      reusableCharArrayBuffer.append('.');
    } else if (filter.length == 1) {
      reusableCharArrayBuffer.append(filter[0]);
    } else {
      int i;
      for (i = 0; i < filter.length - 1; ++i) {
        reusableCharArrayBuffer.append(filter[i]);
        reusableCharArrayBuffer.append(',');
      }
      reusableCharArrayBuffer.append(filter[i]);
    }
    reusableCharArrayBuffer.append('\t');

    // info
    Map<String, String> info = vcfRecord.info();
    int infoSize = info.size();
    if (infoSize == 0) {
      reusableCharArrayBuffer.append('.');
    } else if (infoSize == 1) {
      reusableCharArrayBuffer.append(info.keySet().iterator().next());
      reusableCharArrayBuffer.append('=');
      reusableCharArrayBuffer.append(info.values().iterator().next());
    } else {
      Iterator<String> keyIterator = info.keySet().iterator();
      Iterator<String> valueIterator = info.values().iterator();
      int i;
      for (i = 0; i < infoSize - 1; ++i) {
        reusableCharArrayBuffer.append(keyIterator.next());
        reusableCharArrayBuffer.append('=');
        reusableCharArrayBuffer.append(valueIterator.next());
        reusableCharArrayBuffer.append(';');
      }
      reusableCharArrayBuffer.append(keyIterator.next());
      reusableCharArrayBuffer.append('=');
      reusableCharArrayBuffer.append(valueIterator.next());
    }
    reusableCharArrayBuffer.append('\t');

    // format
    boolean hasGenotypeInfo = vcfRecord.sampleData().length > 0;
    if (hasGenotypeInfo) {
      // format
      String[] format = vcfRecord.format();
      if (format.length == 0) {
        reusableCharArrayBuffer.append('.');
      } else {
        int i;
        for (i = 0; i < format.length - 1; ++i) {
          reusableCharArrayBuffer.append(format[i]);
          reusableCharArrayBuffer.append(':');
        }
        reusableCharArrayBuffer.append(format[i]);
      }
      reusableCharArrayBuffer.append('\t');

      // sample data
      String[] sampleData = vcfRecord.sampleData();
      if (sampleData.length == 1) {
        reusableCharArrayBuffer.append(sampleData[0]);
      } else {
        int i;
        for (i = 0; i < sampleData.length - 1; ++i) {
          reusableCharArrayBuffer.append(sampleData[i]);
          reusableCharArrayBuffer.append('\t');
        }
        reusableCharArrayBuffer.append(sampleData[i]);
      }
    }

    // newline
    reusableCharArrayBuffer.append('\n');
  }

  @Override
  public void close() {
    try {
      writer.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
