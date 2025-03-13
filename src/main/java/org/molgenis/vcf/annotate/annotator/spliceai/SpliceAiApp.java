package org.molgenis.vcf.annotate.annotator.spliceai;

import htsjdk.samtools.reference.ReferenceSequenceFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * SpliceAI faster performance: https://github.com/Illumina/SpliceAI/issues/138
 *
 * <p>Java example:
 * https://github.com/tensorflow/java-models/blob/master/src/main/java/org/tensorflow/model/examples/cnn/fastrcnn/FasterRcnnInception.java
 */
public class SpliceAiApp {

  /**
   * from https://raw.githubusercontent.com/Illumina/SpliceAI/refs/tags/v1.3.1/examples/input.vcf:
   *
   * <pre>
   * 1	25000	.	A	C,G,T	.	.	.
   * 2	152389953	.	T	A,C,G	.	.	.
   * 2	179415988	.	C	CA	.	.	.
   * 2	179446218	.	ATACT	A	.	.	.
   * 2	179446218	.	ATACT	AT,ATA	.	.	.
   * 2	179642185	.	G	A	.	.	.
   * 19	38958362	.	C	T	.	.	.
   * 21	47406854	.	CCA	C	.	.	.
   * 21	47406856	.	A	AT	.	.	.
   * X	129274636	.	A	C,G,T	.	.	.
   * </pre>
   *
   * https://raw.githubusercontent.com/Illumina/SpliceAI/refs/tags/v1.3.1/examples/output.vcf
   *
   * <pre>
   * 1	25000	.	A	C,G,T	.	.	.
   * 2	152389953	.	T	A,C,G	.	.	SpliceAI=A|NEB|0.01|0.00|0.00|0.74|43|3|-26|3,C|NEB|0.04|0.00|0.00|0.71|43|3|-26|3,G|NEB|0.03|0.00|0.00|0.75|43|3|-26|3
   * 2	179415988	.	C	CA	.	.	SpliceAI=CA|TTN|0.07|1.00|0.00|0.00|-7|-1|35|-29
   * 2	179446218	.	ATACT	A	.	.	SpliceAI=A|TTN|0.00|0.00|0.02|0.91|-7|34|-11|8
   * 2	179446218	.	ATACT	AT,ATA	.	.	SpliceAI=AT|TTN|.|.|.|.|.|.|.|.,ATA|TTN|.|.|.|.|.|.|.|.
   * 2	179642185	.	G	A	.	.	SpliceAI=A|TTN|0.00|0.00|0.64|0.55|2|38|2|-38
   * 19	38958362	.	C	T	.	.	SpliceAI=T|RYR1|0.00|0.00|0.91|0.08|-28|-46|-2|-31
   * 21	47406854	.	CCA	C	.	.	SpliceAI=C|COL6A1|0.04|0.98|0.00|0.00|-38|4|38|4
   * 21	47406856	.	A	AT	.	.	SpliceAI=AT|COL6A1|0.03|0.99|0.00|0.00|-40|2|36|2
   * X	129274636	.	A	C,G,T	.	.	SpliceAI=C|AIFM1|0.00|0.18|0.00|0.00|-28|-44|-44|45,G|AIFM1|0.00|0.17|0.00|0.00|-8|-44|-44|45,T|AIFM1|0.00|0.19|0.00|0.00|-2|-44|-44|45
   * </pre>
   */
  public static void main(String[] args) throws IOException {
    String chrom = "chr2";
    int pos = 152389953;
    String ref = "T";
    String alt = "A";

    // https://github.com/Illumina/SpliceAI/blob/master/spliceai/utils.py
    // https://stackoverflow.com/questions/62241546/java-tensorflow-keras-equivalent-of-model-predict
    String baseExportDir =
        "C:\\Users\\Dennis Hendriksen\\Dev\\vip-annotate\\src\\main\\resources\\spliceai\\models\\";
    String[] exportDirs = new String[5];
    for (int i = 0; i < exportDirs.length; i++) {
      exportDirs[i] = baseExportDir + "spliceai" + (i + 1) + ".pb";
    }

    try (ReferenceSequenceFile refSeq =
            new ReferenceSequenceFileFactory()
                .create(Path.of("C:\\Users\\Dennis Hendriksen\\Dev\\_data\\hg19.fa.gz"));
        Models models = Models.load(exportDirs)) {
      SpliceAiPredictor spliceAiPredictor = new SpliceAiPredictor(models, refSeq);
      spliceAiPredictor.predict(
          chrom,
          pos,
          ref.getBytes(StandardCharsets.UTF_8).length,
          alt.getBytes(StandardCharsets.UTF_8),
          50);
    }
  }
}
