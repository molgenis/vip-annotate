package org.molgenis.vcf.annotate.annotator.spliceai;

import java.net.URL;

class Annotator {
  public Annotator(String ref_fasta, String annotations) {
    String annotationResourcename;
    if ("grch37".equals(annotations)) {
      annotationResourcename = "splice/annotations/grch37.txt";
    } else if ("grch38".equals(annotations)) {
      annotationResourcename = "splice/annotations/grch38.txt";
    } else {
      throw new IllegalArgumentException();
    }
    URL annotationsUrl =
        Thread.currentThread().getContextClassLoader().getResource(annotationResourcename);
    if (annotationsUrl == null)
      throw new IllegalArgumentException(
          "annotations resource not found: " + annotationResourcename);
    annotationsUrl.getPath();
  }

  //  public void get_name_and_strand(chrom, pos) {}
  //
  //    public void get_pos_data(idx, pos){}
  //
  //    private record NameAndStrand(genes, strands, idxs){}
}
