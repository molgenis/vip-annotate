package org.molgenis.vipannotate.annotation;

import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

/// provides {@link MemoryDecoder} if available
@FunctionalInterface
public interface MemoryDecoderProvider extends Supplier<@Nullable MemoryDecoder> {}
