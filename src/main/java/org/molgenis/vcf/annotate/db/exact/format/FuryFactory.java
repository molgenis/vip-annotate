package org.molgenis.vcf.annotate.db.exact.format;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationData;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndex;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexBig;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexSmall;

public class FuryFactory {
  private static Fury fury;

  private FuryFactory() {}

  public static Fury createFury() {
    if (fury == null) {
      fury =
          Fury.builder()
              .withLanguage(Language.JAVA)
              .requireClassRegistration(true)
              .registerGuavaTypes(false)
              .build();

      fury.register(AnnotationData.class);
      fury.register(AnnotationDbImpl.class);
      fury.register(VariantAltAlleleAnnotationData.class);
      fury.register(VariantAltAlleleAnnotationIndex.class);
      fury.register(VariantAltAlleleAnnotationIndexSmall.class);
      fury.register(VariantAltAlleleAnnotationIndexBig.class);
      fury.registerSerializer(SortedIntArrayWrapper.class, SortedIntArrayWrapperSerializer.class);
    }

    return fury;
  }
}
