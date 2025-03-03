package org.molgenis.vcf.annotate.db2.exact.format;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.apache.fury.logging.LoggerFactory;

public class FuryFactory {
  private static Fury fury;

  private FuryFactory() {}

  public static Fury createFury() {
    if (fury == null) {
      LoggerFactory.useSlf4jLogging(true);

      fury = Fury.builder().withLanguage(Language.JAVA).requireClassRegistration(true).build();
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
