package org.molgenis.vipannotate.format.vcf;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.molgenis.vipannotate.util.BufferedLineReader;
import org.molgenis.vipannotate.util.CloseIgnoringInputStream;
import org.molgenis.vipannotate.util.Input;

public class VcfParserFactory {
  public static final int ANNOTATE_BATCH_SIZE = 100;

  public enum InputType {
    COMPRESSED,
    UNCOMPRESSED
  }

  private VcfParserFactory() {}

  public static VcfParser create(Input inputVcf) {
    Path inputVcfPath = inputVcf.path();
    InputType inputType;
    InputStream inputStream;
    if (inputVcfPath != null) {
      Path pathFileName = inputVcfPath.getFileName();
      if (pathFileName == null) {
        throw new IllegalArgumentException(
            "Input VCF file path '%s' must not have zero elements".formatted(inputVcfPath));
      }
      String inputVcfFilename = pathFileName.toString();
      inputType =
          inputVcfFilename.endsWith(".gz") || inputVcfFilename.endsWith(".bgz")
              ? InputType.COMPRESSED
              : InputType.UNCOMPRESSED;
      try {
        inputStream = Files.newInputStream(inputVcfPath);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      int pushbackBufferSize = 2;
      PushbackInputStream pushbackInputStream =
          new PushbackInputStream(new CloseIgnoringInputStream(System.in), pushbackBufferSize);

      byte[] buffer = new byte[pushbackBufferSize];
      int bytesRead;
      try {
        bytesRead = pushbackInputStream.read(buffer);
        if (bytesRead != 2) {
          throw new VcfParserException("vcf file is not a valid gzip file");
        }

        // gzip magic number: 1F 8B
        inputType =
            (buffer[0] == ((byte) 0x1F) && buffer[1] == ((byte) 0x8B))
                ? InputType.COMPRESSED
                : InputType.UNCOMPRESSED;

        pushbackInputStream.unread(buffer, 0, bytesRead);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      inputStream = pushbackInputStream;
    }

    return create(inputStream, inputType);
  }

  private static VcfParser create(InputStream inputStream, InputType inputType) {
    BufferedLineReader reader = createReader(inputStream, inputType);

    VcfHeader vcfHeader =
        new VcfHeaderParser(new VcfMetaInfoLineParser(), new VcfHeaderLineParser()).parse(reader);
    int nrSamples = vcfHeader.getNrSamples();
    List<VcfRecord> vcfRecords = new ArrayList<>(ANNOTATE_BATCH_SIZE);
    for (int i = 0; i < ANNOTATE_BATCH_SIZE; i++) {
      vcfRecords.add(
          nrSamples == 0
              ? VcfRecordDummyFactory.INSTANCE.createDummy()
              : VcfRecordDummyFactory.INSTANCE.createDummyWithGenotypeFields());
    }
    VcfRecordBatchIterator vcfRecordBatchIterator = new VcfRecordBatchIterator(reader, vcfRecords);
    return new VcfParser(vcfHeader, vcfRecordBatchIterator);
  }

  private static BufferedLineReader createReader(InputStream inputStream, InputType inputType) {
    final int inputStreamReaderBufferSize = 32768;

    BufferedLineReader reader;
    try {
      InputStream wrappedInputStream =
          switch (inputType) {
            case COMPRESSED -> new GZIPInputStream(inputStream, inputStreamReaderBufferSize);
            case UNCOMPRESSED -> inputStream;
          };
      reader = new BufferedLineReader(new InputStreamReader(wrappedInputStream, UTF_8));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return reader;
  }
}
