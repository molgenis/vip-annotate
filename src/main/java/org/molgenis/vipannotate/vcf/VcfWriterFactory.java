package org.molgenis.vipannotate.vcf;

import static java.util.Objects.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import org.molgenis.vipannotate.util.CloseIgnoringOutputStream;

public class VcfWriterFactory {
  private static final int GZIP_OUTPUT_STREAM_BUFFER_SIZE = 32768;

  private VcfWriterFactory() {}

  public static VcfWriter create(Path outputVcfPath, VcfType outputVcfType) {
    OutputStream outputStream;
    if (outputVcfPath != null) {
      try {
        Files.createDirectories(outputVcfPath.getParent());
        outputStream = Files.newOutputStream(outputVcfPath);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      outputStream = new CloseIgnoringOutputStream(System.out);
    }

    if (outputVcfType == null) {
      if (outputVcfPath != null) {
        String outputVcfFilename = outputVcfPath.getFileName().toString();
        outputVcfType =
            outputVcfFilename.endsWith(".gz") || outputVcfFilename.endsWith(".bgz")
                ? VcfType.COMPRESSED
                : VcfType.UNCOMPRESSED;
      } else {
        outputVcfType = VcfType.UNCOMPRESSED;
      }
    }

    VcfWriter vcfWriter;
    if (outputVcfType != VcfType.UNCOMPRESSED) {
      Integer compressionLevel = outputVcfType.getCompressionLevel();
      if (compressionLevel != null) {
        vcfWriter = createBgzip(outputStream, compressionLevel);
      } else {
        vcfWriter = createBgzip(outputStream);
      }
    } else {
      vcfWriter = create(outputStream);
    }
    return vcfWriter;
  }

  public static VcfWriter create(OutputStream outputStream) {
    requireNonNull(outputStream);

    Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
    return new VcfWriter(writer);
  }

  public static VcfWriter createBgzip(OutputStream outputStream) {
    requireNonNull(outputStream);
    return createBgzip(outputStream, 1);
  }

  // FIXME create bgzip instead of gzip
  public static VcfWriter createBgzip(OutputStream outputStream, int level) {
    requireNonNull(outputStream);
    if (level < 0 || level > 9) throw new IllegalArgumentException("level must be between 0 and 9");

    GZIPOutputStream gzipOutputStream;
    try {
      gzipOutputStream =
          new GZIPOutputStream(outputStream, GZIP_OUTPUT_STREAM_BUFFER_SIZE) {
            {
              def = new Deflater(level, true); // hack: set protected 'def' field
            }
          };

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return create(gzipOutputStream);
  }
}
