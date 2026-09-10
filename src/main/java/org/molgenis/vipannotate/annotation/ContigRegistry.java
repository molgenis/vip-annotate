package org.molgenis.vipannotate.annotation;

import java.util.Collection;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ContigRegistry {
  private final Map<String, Contig> contigIdToContigMap;

  public @Nullable Contig getContig(String contigId) {
    return contigIdToContigMap.get(contigId);
  }

  public static ContigRegistry create(FastaIndex fastaIndex) {
    Collection<FastaIndexRecord> fastaIndexRecords = fastaIndex.getRecords();
    Map<String, Contig> contigIdToContigMap =
        Maps.newHashMapWithExpectedSize(fastaIndexRecords.size());
    for (FastaIndexRecord record : fastaIndexRecords) {
      String name = record.name();
      contigIdToContigMap.put(name, new Contig(name, record.length()));
    }
    return new ContigRegistry(contigIdToContigMap);
  }
}
