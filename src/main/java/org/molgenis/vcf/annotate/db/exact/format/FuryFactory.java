package org.molgenis.vcf.annotate.db.exact.format;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;

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
      fury.register(AnnotationDbPartition.class);
      fury.register(BigVariantIndexLookupTable.class);
      fury.register(SmallVariantIndexLookupTable.class);
      fury.registerSerializer(SortedIntArrayWrapper.class, SortedIntArrayWrapperSerializer.class);
    }

    return fury;
  }
}
