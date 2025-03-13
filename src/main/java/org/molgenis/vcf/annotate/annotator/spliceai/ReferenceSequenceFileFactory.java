package org.molgenis.vcf.annotate.annotator.spliceai;

import htsjdk.samtools.reference.BlockCompressedIndexedFastaSequenceFile;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.util.GZIIndex;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReferenceSequenceFileFactory {

  public ReferenceSequenceFile create(Path fastaPath) {
    Path fastaFaiPath = Path.of(fastaPath.toString() + ".fai");
    Path fastaGziPath = Path.of(fastaPath.toString() + ".gzi");

    FastaSequenceIndex fastaSequenceIndex = new FastaSequenceIndex(fastaFaiPath);
    GZIIndex gziIndex = createGziIndex(fastaGziPath);

    return createReferenceSequenceFile(fastaPath, fastaSequenceIndex, gziIndex);
  }

  private static BlockCompressedIndexedFastaSequenceFile createReferenceSequenceFile(
      Path fastaPath, FastaSequenceIndex fastaSequenceIndex, GZIIndex gziIndex) {
    return new BlockCompressedIndexedFastaSequenceFile(fastaPath, fastaSequenceIndex, gziIndex);
  }

  private static GZIIndex createGziIndex(Path fastaGziPath) {
    GZIIndex gziIndex;
    try (ReadableByteChannel byteChannel = Files.newByteChannel(fastaGziPath)) {
      gziIndex = GZIIndex.loadIndex(null, byteChannel);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return gziIndex;
  }
}
