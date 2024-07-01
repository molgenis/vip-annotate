// package org.molgenis.vcf.annotate.db.spliceai;
//
// import static java.util.Objects.requireNonNull;
//
// import htsjdk.variant.variantcontext.VariantContext;
// import htsjdk.variant.vcf.VCFIterator;
// import htsjdk.variant.vcf.VCFIteratorBuilder;
// import java.io.*;
// import java.util.*;
// import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
// import exact.db.org.molgenis.vipannotate.AnnotationDbWriter;
// import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
// import exact.db.org.molgenis.vipannotate.VariantAltAlleleAnnotation;
//
// public class SpliceAiAnnotationDbBuilder {
//  public SpliceAiAnnotationDbBuilder() {}
//
//  public void create(File SpliceAiFile, File zipFile) {
//    try (VCFIterator vcfIterator = new VCFIteratorBuilder().open(SpliceAiFile);
//        ZipArchiveOutputStream zipOutputStream = createWriter(zipFile)) {
//      new AnnotationDbWriter().create(new SpliceAiVariantIterator(vcfIterator), zipOutputStream);
//    } catch (IOException e) {
//      throw new UncheckedIOException(e);
//    }
//  }
//
//  private static class SpliceAiVariantIterator implements Iterator<VariantAltAlleleAnnotation> {
//
//    private final VCFIterator vcfIterator;
//
//    public SpliceAiVariantIterator(VCFIterator vcfIterator) {
//      this.vcfIterator = requireNonNull(vcfIterator);
//    }
//
//    VariantContext variantContext;
//
//    @Override
//    public boolean hasNext() {
//      while (vcfIterator.hasNext()) {
//        variantContext = vcfIterator.next();
//        if (CONTIGS.contains(variantContext.getContig())) {
//          return true;
//        }
//      }
//      return false;
//    }
//
//    private static final Set<String> CONTIGS = new HashSet<>();
//
//    static {
//      for (int i = 0; i <= 22; ++i) {
//        CONTIGS.add(String.valueOf(i));
//      }
//      CONTIGS.add("X");
//      CONTIGS.add("Y");
//    }
//
//    @Override
//    public VariantAltAlleleAnnotation next() {
//      // TODO liftover input data from SpliceAI b38 flavor to vip b38 flavor
//      String contig = "contig" + variantContext.getContig();
//      int start = variantContext.getStart();
//      int stop = variantContext.getEnd();
//      byte[] altBases = variantContext.getAlternateAllele(0).getDisplayBases();
//
//      String str = (String) variantContext.getAttributeAsList("SpliceAI").getFirst();
//      String[] tokens = str.split("\\|", -1);
//
//      float deltaScoreAcceptorGain = Float.parseFloat(tokens[2]);
//      float deltaScoreAcceptorLoss = Float.parseFloat(tokens[3]);
//      float deltaScoreDonorGain = Float.parseFloat(tokens[4]);
//      float deltaScoreDonorLoss = Float.parseFloat(tokens[5]);
//      byte deltaPositionAcceptorGain = Byte.parseByte(tokens[6]);
//      byte deltaPositionAcceptorLoss = Byte.parseByte(tokens[7]);
//      byte deltaPositionDonorGain = Byte.parseByte(tokens[8]);
//      byte deltaPositionDonorLoss = Byte.parseByte(tokens[9]);
//
//      return new VariantAltAlleleAnnotation(
//          new VariantAltAllele(contig, start, stop, altBases),
//          SpliceAiAnnotationCodec.encode(
//              new SpliceAiAnnotation(
//                  deltaScoreAcceptorGain,
//                  deltaScoreAcceptorLoss,
//                  deltaScoreDonorGain,
//                  deltaScoreDonorLoss,
//                  deltaPositionAcceptorGain,
//                  deltaPositionAcceptorLoss,
//                  deltaPositionDonorGain,
//                  deltaPositionDonorLoss)));
//    }
//  }
//
//  private static ZipArchiveOutputStream createWriter(File zipFile) throws FileNotFoundException {
//    return new ZipArchiveOutputStream(
//        new BufferedOutputStream(new FileOutputStream(zipFile), 1048576));
//  }
// }
