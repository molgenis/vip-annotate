package org.molgenis.vipannotate.db.exact.format;

import java.io.Serializable;

/** sorted int[] wrapper with custom {@link SortedIntArrayWrapperSerializer} */
public record SortedIntArrayWrapper(int[] array) implements Serializable {}
