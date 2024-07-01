// package org.molgenis.vcf.annotate.db.spliceai;
//
// import java.io.File;
// import util.org.molgenis.vipannotate.Logger;
//
// public class AppSpliceAiAnnotationDbBuilder {
//
//  public static void main(String[] args) {
//    File inputFile = new File(args[0]);
//    File outputFile = new File(args[1]);
//
//    Logger.info("creating database ...");
//    long startCreateDb = System.currentTimeMillis();
//    new SpliceAiAnnotationDbBuilder().create(inputFile, outputFile);
//    long endCreateDb = System.currentTimeMillis();
//    Logger.info("creating database done in {}ms", endCreateDb - startCreateDb);
//  }
// }
