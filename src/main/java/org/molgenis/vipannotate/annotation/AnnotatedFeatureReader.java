package org.molgenis.vipannotate.annotation;

import java.util.Iterator;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface AnnotatedFeatureReader
    extends Iterator<AnnotatedFeature<?, ?>>, AutoCloseableNoThrow {}
