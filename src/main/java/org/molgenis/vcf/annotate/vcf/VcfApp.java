package org.molgenis.vcf.annotate.vcf;

import java.nio.file.Files;
import java.nio.file.Paths;

public class VcfApp {
  public static void main(String[] args) throws Exception {
    try (VcfReader vcfReader = VcfReader.create(Files.newInputStream(Paths.get(args[0])))) {
      vcfReader.forEachRemaining(vcfRecord -> {});
    }
  }
}
