package org.molgenis.vipannotate.serialization;

import java.io.Serializable;

/** sorted long[] wrapper with custom {@link SortedLongArrayWrapperSerializer} */
public record SortedLongArrayWrapper(long[] array) implements Serializable {}
