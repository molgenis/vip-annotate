package org.molgenis.vcf.annotate.spliceai;

import static java.util.Objects.requireNonNull;

import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.tensorflow.Result;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.SessionFunction;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.StdArrays;
import org.tensorflow.types.TFloat32;

public class SpliceAiPredictor {
  private final Models models;
  private final ReferenceSequenceFile refSequence;

  public SpliceAiPredictor(Models models, ReferenceSequenceFile refSequence) {
    this.models = requireNonNull(models);
    this.refSequence = requireNonNull(refSequence);
  }

  /**
   * @param chrom chromosome
   * @param pos 1-based position
   * @param refLength reference sequence length
   * @param altBases alternate sequence bases
   * @param distance maximum distance between the variant and gained/lost splice site
   */
  public void predict(String chrom, int pos, int refLength, byte[] altBases, int distance) {
    if (chrom == null) {
      throw new IllegalArgumentException("chrom cannot be null");
    }
    if (pos <= 0) {
      throw new IllegalArgumentException("pos must be greater than 0");
    }
    if (refLength != 1) {
      throw new IllegalArgumentException("refLength must be 1"); // FIXME support other values
    }
    if (altBases.length != 1) {
      throw new IllegalArgumentException("altBases must be 1"); // FIXME support other values
    }
    if (distance < 0 || distance > 5000) {
      throw new IllegalArgumentException("distance must be between 0 and 5000");
    }

    Strand strand = Strand.MINUS; // FIXME retrieve from annotations instead of hardcode

    int cov = 2 * distance + 1; // 101
    int wid = 10000 + cov;

    ReferenceSequence seq = refSequence.getSubsequenceAt(chrom, pos - wid / 2, pos + wid / 2);
    byte[] refSeq = seq.getBases();
    byte[] altSeq = new byte[refSeq.length];
    System.arraycopy(refSeq, 0, altSeq, 0, refSeq.length);
    altSeq[wid / 2] = altBases[0];

    FloatNdArray refSeqEncoded = oneHotEncode(refSeq, strand);
    FloatNdArray altSeqEncoded = oneHotEncode(altSeq, strand);

    List<float[][][]> refPredictions = new ArrayList<>(models.count());
    List<float[][][]> altPredictions = new ArrayList<>(models.count());
    long startCurrentTimeMillis = System.currentTimeMillis();
    for (SavedModelBundle model : models) {
      SessionFunction sessionFunction = model.function("serving_default");

      float[][][] refPrediction = predict(sessionFunction, refSeqEncoded);
      refPredictions.add(refPrediction);

      float[][][] altPrediction = predict(sessionFunction, altSeqEncoded);
      altPredictions.add(altPrediction);
    }
    long stop = System.currentTimeMillis() - startCurrentTimeMillis;
    System.out.println(stop);
    float[][][] refPredictionMean = Utils.mean(refPredictions);
    float[][][] altPredictionMean = Utils.mean(altPredictions);

    if (strand == Strand.MINUS) {
      Utils.reverse(refPredictionMean[0]);
      Utils.reverse(altPredictionMean[0]);
    }
    // TODO what is the meaning of the data at zIndex 0?
    int idx_pa = Utils.argmax(altPredictionMean[0], refPredictionMean[0], 1);
    int idx_na = Utils.argmax(refPredictionMean[0], altPredictionMean[0], 1);
    int idx_pd = Utils.argmax(altPredictionMean[0], refPredictionMean[0], 2);
    int idx_nd = Utils.argmax(refPredictionMean[0], altPredictionMean[0], 2);

    float deltaScoreAcceptorGain =
        altPredictionMean[0][idx_pa][1] - refPredictionMean[0][idx_pa][1];
    float deltaScoreAcceptorLoss =
        refPredictionMean[0][idx_na][1] - altPredictionMean[0][idx_na][1];
    float deltaScoreDonorGain = altPredictionMean[0][idx_pd][2] - refPredictionMean[0][idx_pd][2];
    float deltaScoreDonorLoss = refPredictionMean[0][idx_nd][2] - altPredictionMean[0][idx_nd][2];

    int deltaPositionAcceptorGain = idx_pa - cov / 2;
    int deltaPositionAcceptorLoss = idx_na - cov / 2;
    int deltaPositionDonorGain = idx_pd - cov / 2;
    int deltaPositionDonorLoss = idx_nd - cov / 2;
    // TODO masking

    /*
    These include delta scores (DS) and delta positions (DP) for '
                    'acceptor gain (AG), acceptor loss (AL), donor gain (DG), and donor loss (DL). '
                    'Format: ALLELE|SYMBOL|DS_AG|DS_AL|DS_DG|DS_DL|DP_AG|DP_AL|DP_DG|DP_DL">')

    (y[1, idx_pa, 1]-y[0, idx_pa, 1])*(1-mask_pa),
                                (y[0, idx_na, 1]-y[1, idx_na, 1])*(1-mask_na),
                                (y[1, idx_pd, 2]-y[0, idx_pd, 2])*(1-mask_pd),
                                (y[0, idx_nd, 2]-y[1, idx_nd, 2])*(1-mask_nd),
                                idx_pa-cov//2,
                                idx_na-cov//2,
                                idx_pd-cov//2,
                                idx_nd-cov//2))
     */
    System.out.printf(
        "%s|%s|%.2f|%.2f|%.2f|%.2f|%d|%d|%d|%d%n",
        new String(altBases, StandardCharsets.UTF_8),
        "<TODO:genesymbol>",
        deltaScoreAcceptorGain,
        deltaScoreAcceptorLoss,
        deltaScoreDonorGain,
        deltaScoreDonorLoss,
        deltaPositionAcceptorGain,
        deltaPositionAcceptorLoss,
        deltaPositionDonorGain,
        deltaPositionDonorLoss);
  }

  private float[][][] predict(SessionFunction sessionFunction, FloatNdArray seqEncoded) {
    try (TFloat32 tensor = TFloat32.tensorOf(seqEncoded)) {
      Map<String, Tensor> feedDict = Map.of("input_1", tensor);

      try (Result outputTensorMap = sessionFunction.call(feedDict)) {
        try (Tensor outputTensor = outputTensorMap.get("output_0").orElseThrow()) {
          return StdArrays.array3dCopyOf((FloatNdArray) outputTensor);
        }
      }
    }
  }

  private static FloatNdArray oneHotEncodeStrandPlus(byte[] bases) {
    float[][][] data = new float[1][bases.length][4];
    for (int i = 0; i < bases.length; ++i) {
      byte base = bases[i];
      switch (base) {
        case 'N':
          data[0][i][0] = 0;
          data[0][i][1] = 0;
          data[0][i][2] = 0;
          data[0][i][3] = 0;
          break;
        case 'a':
        case 'A':
          data[0][i][0] = 1;
          data[0][i][1] = 0;
          data[0][i][2] = 0;
          data[0][i][3] = 0;
          break;
        case 'c':
        case 'C':
          data[0][i][0] = 0;
          data[0][i][1] = 1;
          data[0][i][2] = 0;
          data[0][i][3] = 0;
          break;
        case 'g':
        case 'G':
          data[0][i][0] = 0;
          data[0][i][1] = 0;
          data[0][i][2] = 1;
          data[0][i][3] = 0;
          break;
        case 't':
        case 'T':
          data[0][i][0] = 0;
          data[0][i][1] = 0;
          data[0][i][2] = 0;
          data[0][i][3] = 1;
          break;
        default:
          throw new IllegalArgumentException("Unsupported base: " + base);
      }
    }
    return StdArrays.ndCopyOf(data);
  }

  private static FloatNdArray oneHotEncodeStrandMinus(byte[] bases) {
    float[][][] data = new float[1][bases.length][4];
    for (int i = bases.length - 1, j = 0; i >= 0; --i, ++j) {
      byte base = bases[i];
      switch (base) {
        case 'N':
          data[0][j][0] = 0;
          data[0][j][1] = 0;
          data[0][j][2] = 0;
          data[0][j][3] = 0;
          break;
        case 'a':
        case 'A':
          data[0][j][0] = 0;
          data[0][j][1] = 0;
          data[0][j][2] = 0;
          data[0][j][3] = 1;
          break;
        case 'c':
        case 'C':
          data[0][j][0] = 0;
          data[0][j][1] = 0;
          data[0][j][2] = 1;
          data[0][j][3] = 0;
          break;
        case 'g':
        case 'G':
          data[0][j][0] = 0;
          data[0][j][1] = 1;
          data[0][j][2] = 0;
          data[0][j][3] = 0;
          break;
        case 't':
        case 'T':
          data[0][j][0] = 1;
          data[0][j][1] = 0;
          data[0][j][2] = 0;
          data[0][j][3] = 0;
          break;
        default:
          throw new IllegalArgumentException("Unsupported base: " + base);
      }
    }
    return StdArrays.ndCopyOf(data);
  }

  private static FloatNdArray oneHotEncode(byte[] bases, Strand strand) {
    return switch (strand) {
      case PLUS -> oneHotEncodeStrandPlus(bases);
      case MINUS -> oneHotEncodeStrandMinus(bases);
    };
  }
}
