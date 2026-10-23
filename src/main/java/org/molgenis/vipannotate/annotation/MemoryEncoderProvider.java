package org.molgenis.vipannotate.annotation;

import java.util.function.Supplier;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

/// provides {@link MemoryEncoder}
@FunctionalInterface
public interface MemoryEncoderProvider extends Supplier<MemoryEncoder> {}
