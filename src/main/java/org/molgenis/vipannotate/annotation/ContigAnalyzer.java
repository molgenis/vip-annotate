package org.molgenis.vipannotate.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.Record;
import org.molgenis.vipannotate.util.CharSequenceUtils;

@RequiredArgsConstructor
public final class ContigAnalyzer<F extends Field, R extends Record<F>> {
  private final Function<R, CharSequence> contigAccessor;

  /// 1-based position accessor
  private final ToLongFunction<R> posAccessor;

  private final Map<String, ContigAnalysis> analysisMap = new LinkedHashMap<>();

  private @Nullable String currentContig;
  private long currentMinPos;
  private long currentMaxPos;
  private long currentRecordCount;

  /// records must be analyzed in order of contig and within contig in order of position
  public void analyze(R record) {
    CharSequence contig = contigAccessor.apply(record);

    if (currentContig == null) {
      // first record
      startContig(contig, posAccessor.applyAsLong(record));
    } else if (!CharSequenceUtils.equals(currentContig, contig)) {
      // first record of new contig
      finishContig();
      startContig(contig, posAccessor.applyAsLong(record));
    } else {
      // record in same contig
      currentMaxPos = posAccessor.applyAsLong(record);
      currentRecordCount++;
    }
  }

  public Map<String, ContigAnalysis> collect() {
    if (currentContig != null) {
      // last contig
      finishContig();
    }
    return analysisMap;
  }

  private void startContig(CharSequence contig, long pos) {
    currentContig = contig.toString();
    currentMinPos = pos;
    currentMaxPos = pos;
    currentRecordCount = 1;
  }

  private void finishContig() {
    if (analysisMap.put(
            currentContig, new ContigAnalysis(currentMinPos, currentMaxPos, currentRecordCount))
        != null) {
      throw new IllegalStateException();
    }
  }
}
