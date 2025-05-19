package org.molgenis.vcf.annotate.db.effect.model;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import lombok.*;
import lombok.experimental.NonFinal;
import lombok.experimental.PackagePrivate;
import lombok.experimental.SuperBuilder;
import org.apache.fury.Fury;
import org.apache.fury.config.Language;

public class FuryFactory {
  static Fury fury;

  static {
    fury = Fury.builder().withLanguage(Language.JAVA).requireClassRegistration(true).build();

    // order matters
    fury.register(ClosedInterval.class, true);
    fury.register(Strand.class, true);
    fury.register(Cds.Fragment.class, true);
    fury.register(Cds.class, true);
    fury.register(Chromosome.class, true);
    fury.register(Exon.class, true);
    fury.register(Gene.class, true);
    fury.register(IntervalTree.class, true);
    fury.register(SequenceType.class, true);
    fury.register(Sequence.class, true);
    fury.register(SequenceDb.class, true);
    fury.register(Transcript.Type.class, true);
    fury.register(Transcript.class, true);
    fury.register(TranscriptDb.class, true);
    fury.register(AnnotationDb.class, true);
  }

  private FuryFactory() {}

  public static Fury createFury() {
    return fury;
  }

  /**
   * An RNA synthesized on a DNA or RNA template by an RNA polymerase.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0000673">SO:0000673</a>
   */
  @Value
  @EqualsAndHashCode(callSuper = true)
  @SuperBuilder
  public static class Transcript extends ClosedInterval implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @NonNull String id;
    @NonNull FuryFactory.Transcript.Type type;
    @PackagePrivate int geneIndex;
    @NonNull Exon[] exons;
    Cds cds;

    @Getter
    public enum Type {
      PRIMARY_TRANSCRIPT("primary_transcript"),
      R_RNA("rRNA"),
      ANTISENSE_RNA("antisense_RNA"),
      RNASE_MRP_RNA("RNase_MRP_RNA"),
      VAULT_RNA("vault_RNA"),
      M_RNA("mRNA"),
      NC_RNA("ncRNA"),
      SN_RNA("snRNA"),
      Y_RNA("Y_RNA"),
      SNO_RNA("snoRNA"),
      LNC_RNA("lnc_RNA"),
      RNASE_P_RNA("RNase_P_RNA"),
      TRANSCRIPT("transcript"),
      SC_RNA("scRNA"),
      TELOMERASE_RNA("telomerase_RNA");

      private static final Map<String, Type> TERM_TO_TYPE_MAP;

      static {
        TERM_TO_TYPE_MAP = HashMap.newHashMap(Type.values().length);
        for (Type type : Type.values()) {
          TERM_TO_TYPE_MAP.put(type.getTerm(), type);
        }
      }

      private final String term;

      Type(String term) {
        this.term = requireNonNull(term);
      }

      public static Type from(String term) {
        return TERM_TO_TYPE_MAP.get(requireNonNull(term));
      }
    }
  }

  /**
   * @see <a
   *     href="https://www.ncbi.nlm.nih.gov/datasets/genome/GCF_000001405.40/">GCF_000001405.40</a>
   */
  @Getter
  public enum Chromosome {
    CHR1("NC_000001.11"),
    CHR2("NC_000002.12"),
    CHR3("NC_000003.12"),
    CHR4("NC_000004.12"),
    CHR5("NC_000005.10"),
    CHR6("NC_000006.12"),
    CHR7("NC_000007.14"),
    CHR8("NC_000008.11"),
    CHR9("NC_000009.12"),
    CHR10("NC_000010.11"),
    CHR11("NC_000011.10"),
    CHR12("NC_000012.12"),
    CHR13("NC_000013.11"),
    CHR14("NC_000014.9"),
    CHR15("NC_000015.10"),
    CHR16("NC_000016.10"),
    CHR17("NC_000017.11"),
    CHR18("NC_000018.10"),
    CHR19("NC_000019.10"),
    CHR20("NC_000020.11"),
    CHR21("NC_000021.9"),
    CHR22("NC_000022.11"),
    CHRX("NC_000023.11"),
    CHRY("NC_000024.10"),
    CHRM("NC_012920.1");

    private static final Map<String, Chromosome> ID_TO_CHROMOSOME_MAP;

    static {
      ID_TO_CHROMOSOME_MAP = HashMap.newHashMap(Chromosome.values().length);
      for (Chromosome chromosome : Chromosome.values()) {
        ID_TO_CHROMOSOME_MAP.put(chromosome.getId(), chromosome);
      }
    }

    private final String id;

    Chromosome(String id) {
      this.id = requireNonNull(id);
    }

    public static Chromosome from(String id) {
      return ID_TO_CHROMOSOME_MAP.get(requireNonNull(id));
    }
  }

  @Value
  @NonFinal
  @SuperBuilder
  public static class ClosedInterval {
    /** start in closed interval [start, stop] */
    int start;

    /** stop - start */
    int length;

    /** stop in closed interval [start, stop] */
    public int getStop() {
      return start + length - 1;
    }

    /**
     * @return true if [start, stop] overlaps with [x, y]
     */
    public boolean isOverlapping(long start, long stop) {
      int intervalStart = getStart();
      int intervalStop = getStop();
      return ((start >= intervalStart && start <= intervalStop)
          || (stop >= intervalStart && stop <= intervalStop)
          || (start < intervalStart && stop > intervalStop));
    }
  }

  public enum Strand implements Serializable {
    /** sense */
    POSITIVE,
    /** antisense */
    NEGATIVE
  }

  /** nucleotide sequence encoded for space-efficiency and fast lookup */
  @Value
  @EqualsAndHashCode(callSuper = true)
  @SuperBuilder
  public static class Sequence extends ClosedInterval implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @NonNull SequenceType sequenceType;
    long @NonNull [] bits;

    /**
     * @return sequence in [start, stop] if start <= stop, or the reverse sequence in [stop, start]
     */
    public char[] get(int start, int stop, Strand strand) {
      start = start - getStart();
      stop = stop - getStart();

      char[] nucs;
      switch (strand) {
        case POSITIVE -> {
          nucs = new char[stop - start + 1];
          for (int i = start, idx = 0; i <= stop; i++, idx++) {
            nucs[idx] = getNuc(i, strand);
          }
        }
        case NEGATIVE -> {
          nucs = new char[start - stop + 1];
          for (int i = start, idx = 0; i >= stop; i--, idx++) {
            nucs[idx] = getNuc(i, strand);
          }
        }
        case null -> throw new RuntimeException();
      }
      return nucs;
    }

    private char getNuc(int relativePos, Strand strand) {
      return switch (sequenceType) {
        case ACTG -> getActg(relativePos, strand);
        case ACTGN -> getActgn(relativePos, strand);
      };
    }

    private char getActg(int relativePos, Strand strand) {
      int bitIndex = relativePos * 2;

      char nuc;
      if (get(bitIndex)) {
        if (get(bitIndex + 1)) {
          nuc = strand == Strand.POSITIVE ? 'G' : 'C'; // 11
        } else {
          nuc = strand == Strand.POSITIVE ? 'T' : 'A'; // 10
        }
      } else {
        if (get(bitIndex + 1)) {
          nuc = strand == Strand.POSITIVE ? 'C' : 'G'; // 01
        } else {
          nuc = strand == Strand.POSITIVE ? 'A' : 'T'; // 00
        }
      }
      return nuc;
    }

    private char getActgn(int relativePos, Strand strand) {
      int bitIndex = relativePos * 3;

      char nuc;
      if (get(bitIndex)) {
        if (get(bitIndex + 1)) {
          throw new IllegalStateException(); // 111 or 110
        } else {
          if (get(bitIndex + 2)) {
            throw new IllegalStateException(); // 101
          } else {
            nuc = 'N'; // 100
          }
        }
      } else {
        if (get(bitIndex + 1)) {
          if (get(bitIndex + 2)) {
            nuc = strand == Strand.POSITIVE ? 'G' : 'C'; // 011
          } else {
            nuc = strand == Strand.POSITIVE ? 'T' : 'A'; // 010
          }
        } else {
          if (get(bitIndex + 2)) {
            nuc = strand == Strand.POSITIVE ? 'C' : 'G'; // 001
          } else {
            nuc = strand == Strand.POSITIVE ? 'A' : 'T'; // 000
          }
        }
      }
      return nuc;
    }

    private boolean get(int index) {
      int i = index >> 6;
      long bitmask = 1L << index;
      return (bits[i] & bitmask) != 0;
    }
  }

  /**
   * A region (or regions) that includes all of the sequence elements necessary to encode a
   * functional transcript. A gene may include regulatory regions, transcribed regions and/or other
   * functional sequence regions.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000704">SO:0000704</a>
   */
  @Value
  @Builder
  public static class Gene {
    int id;
    @NonNull String name;
    @NonNull FuryFactory.Strand strand;
  }

  public record AnnotationDb(
      @NonNull TranscriptDb transcriptDb, @NonNull Gene[] genes, @NonNull SequenceDb sequenceDb)
      implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public List<Transcript> findOverlapTranscripts(int start, int stop) {
      return transcriptDb.findOverlap(start, stop);
    }

    public Gene getGene(Transcript transcript) {
      return genes[transcript.getGeneIndex()];
    }

    /**
     * @return sequence in [start, stop] if start <= stop, or the reverse sequence in [stop, start]
     * @throws IllegalArgumentException if no sequence exists for [start, stop]
     */
    public char[] getSequence(int start, int stop, Strand strand) {
      Sequence sequence =
          switch (strand) {
            case POSITIVE -> sequenceDb.findAnySequence(start, stop);
            case NEGATIVE -> sequenceDb.findAnySequence(stop, start);
          };
      if (sequence == null) {
        throw new IllegalArgumentException(String.format("invalid sequence [%d, %d]", start, stop));
      }
      return sequence.get(start, stop, strand);
    }
  }

  public record SequenceDb(@NonNull IntervalTree intervalTree, @NonNull Sequence[] sequences)
      implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    /**
     * @return first sequence overlapping with [start, stop] or <code>null</code>
     */
    public Sequence findAnySequence(int start, int stop) {
      // stop + 1, because interval tree builder requires [x, y) interval
      int sequenceId = intervalTree.queryAnyOverlapId(start, stop + 1);
      return sequenceId != -1 ? sequences[sequenceId] : null;
    }
  }

  /**
   * A region of the transcript sequence within a gene which is not removed from the primary RNA
   * transcript by RNA splicing.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000147">SO:0000147</a>
   */
  @Value
  @EqualsAndHashCode(callSuper = true)
  @SuperBuilder
  public static class Exon extends ClosedInterval implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
  }

  public record TranscriptDb(@NonNull IntervalTree intervalTree, @NonNull Transcript[] transcripts)
      implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    /**
     * @return overlapping [start, stop) transcripts
     */
    public List<Transcript> findOverlap(int start, int stop) {
      List<Transcript> overlappingTranscripts = new ArrayList<>();
      // end + 1, because interval tree builder requires [x, y) interval
      intervalTree.queryOverlapId(
          start, stop + 1, id -> overlappingTranscripts.add(transcripts[id]));
      return overlappingTranscripts;
    }
  }

  /**
   * A contiguous sequence which begins with, and includes, a start codon and ends with, and
   * includes, a stop codon.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000316">SO:0000316</a>
   */
  @Builder
  public record Cds(@NonNull String proteinId, @NonNull Fragment[] fragments)
      implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    /**
     * @return first cds fragment id overlapping with [start, stop] or <code>-1</code>
     */
    public int findAnyFragmentId(long start, long stop) {
      for (int i = 0; i < fragments.length; i++) {
        if (fragments[i].isOverlapping(start, stop)) return i;
      }
      return -1;
    }

    /**
     * A portion of a CDS that is not the complete CDS.
     *
     * @see <a
     *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001384">SO:0001384</a>
     */
    @Value
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    public static class Fragment extends ClosedInterval implements Serializable {
      @Serial private static final long serialVersionUID = 1L;

      /**
       * Indicates where the next codon begins relative to the 5' end of the current CDS feature.
       */
      byte phase;
    }
  }

  public enum SequenceType {
    ACTG,
    ACTGN
  }

  /**
   * Source: <a
   * href="https://raw.githubusercontent.com/mlin/iitj/v0.1.0/src/main/java/net/mlin/iitj/IntegerIntervalTree.java">here</a>
   *
   * <p>Data structure storing [int begin, int end) intervals and answering requests for those
   * overlapping a query interval. Each stored interval is associated with an integer equal to the
   * order in which it was added (zero-based). The index is compact in memory and serializes
   * efficiently, but it's read-only once built.
   */
  public static class IntervalTree implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    /** Builder storing items to be indexed in a IntegerIntervalTree */
    public static class Builder {
      private int n;
      private final int initialCapacity;
      private int[] begs, ends;
      private boolean sorted;

      public Builder(int initialCapacity) {
        if (initialCapacity <= 0) {
          throw new IllegalArgumentException();
        }
        this.initialCapacity = initialCapacity;
        reset();
      }

      public Builder() {
        this(16);
      }

      /**
       * Add one [beg,end) interval to be stored. The positions are "half-open" such that an
       * interval [x,y) abuts but does not overlap [w,x) and [y,z). The same interval may be stored
       * multiple times. Adding the intervals in sorted order, by begin position then end position,
       * will save time and space (but isn't required).
       *
       * @param beg interval begin position (inclusive)
       * @param end interval end position (exclusive)
       * @return An ID for the added interval, equal to the number of intervals added before this
       *     one.
       */
      public int add(int beg, int end) {
        if (beg > end) {
          throw new IllegalArgumentException();
        }
        if (n == begs.length) {
          grow();
          assert n < begs.length;
        }
        begs[n] = beg;
        ends[n] = end;
        if (sorted
            && n > 0
            && (begs[n] < begs[n - 1] || (begs[n] == begs[n - 1] && ends[n] < ends[n - 1]))) {
          sorted = false;
        }
        return n++;
      }

      /**
       * Return true iff intervals have so far been added in sorted order, by begin position then
       * end position. This isn't required, but improves time and space needs.
       */
      public boolean isSorted() {
        return sorted;
      }

      /**
       * Build the IntegerIntervalTree from previously stored intervals. After this the Builder
       * object is reset to an empty state.
       */
      public IntervalTree build() {
        return new IntervalTree(this);
      }

      private void reset() {
        n = 0;
        begs = new int[initialCapacity];
        ends = new int[initialCapacity];
        sorted = true;
      }

      private void grow() {
        long capacity = begs.length;
        if (capacity == Integer.MAX_VALUE) {
          throw new UnsupportedOperationException("IntegerIntervalTree capacity overflow");
        }
        assert ends.length == capacity;
        capacity = (capacity * 3L) / 2L + 1L;
        if (capacity > Integer.MAX_VALUE) {
          capacity = Integer.MAX_VALUE;
        }
        begs = Arrays.copyOf(begs, (int) capacity);
        ends = Arrays.copyOf(ends, (int) capacity);
      }
    }

    // We store the N interval begin & end positions in separate arrays to keep them unboxed. The
    // intervals represented by the two arrays are sorted by begin position, then by end position.
    // Note: we've explored transposing these into a single row-major array to improve cache
    //       locality, but it (i) complicates the code and (ii) halves the effective maximum N due
    //       to the maximum Java array size. (commit cca404ab)
    private final int[] begs, ends;
    // If the intervals weren't originally provided to the builder in the same sorted order, then
    // permute stores their original IDs. Otherwise permute is null and the IDs are the indexes
    // in the above sorted arrays.
    private final int[] permute;
    // Write N as a sum of powers of two, e.g. N = 12345 = 8192 + 4096 + 32 + 16 + 8 + 1, and
    // consider the corresponding slices of the interval array. The leftmost item in each slice is
    // an "index node", and the 2^p-1 remaining items (for some 0<=p<32) are an implicit binary
    // search tree as in Li's cgranges. Our trees are "perfect" by construction, avoiding some
    // complications cgranges handles when that's not so.
    // indexNodes stores the array positions of the index nodes, in ascending order. The first
    // element is always zero and the between any two adjacent elements is a powers of two.
    private final int[] indexNodes;
    // Interval tree augmentation values (maxEnds): our search procedure uses the textbook interval
    // tree recursion above level 2, and upon reaching a level <= 2 subtree it just scans the <= 7
    // items. Therefore, we only need to store maxEnds for levels 2 and above (25% of nodes). We
    // keep an array per index node; in each array, the first element is maxEnd of the tree root
    // & index node, followed by the maxEnd for every fourth tree node.
    private final int[][] maxEnds;

    private IntervalTree(Builder builder) {
      final int n = builder.n;

      // compute sorting permutation of builder intervals, if needed, then copy the data in
      // https://stackoverflow.com/a/25778783
      if (!builder.isSorted()) {
        permute =
            java.util.stream.IntStream.range(0, builder.n)
                .mapToObj(i -> Integer.valueOf(i))
                .sorted(
                    (i1, i2) -> {
                      int c = Integer.compare(builder.begs[i1], builder.begs[i2]);
                      if (c != 0) {
                        return c;
                      }
                      return Integer.compare(builder.ends[i1], builder.ends[i2]);
                    })
                .mapToInt(value -> value.intValue())
                .toArray();

        begs = new int[n];
        ends = new int[n];
        for (int i = 0; i < builder.n; i++) {
          begs[i] = builder.begs[permute[i]];
          ends[i] = builder.ends[permute[i]];
        }
      } else if (builder.n != builder.begs.length) {
        begs = Arrays.copyOf(builder.begs, builder.n);
        ends = Arrays.copyOf(builder.ends, builder.n);
        permute = null;
      } else {
        begs = builder.begs;
        ends = builder.ends;
        permute = null;
      }
      builder.reset();

      // compute index nodes
      int nIndexNodes = Integer.bitCount(n);
      indexNodes = new int[nIndexNodes];
      int nRem = n;
      for (int i = 1; i < nIndexNodes; i++) {
        final int p2 = Integer.highestOneBit(nRem);
        indexNodes[i] = p2 + indexNodes[i - 1];
        nRem &= ~p2;
        assert indexNodes[0] == 0 && indexNodes[i] + nRem == n;
      }

      // Compute maxEnds througout each implict tree; for the index nodes themselves, the maxEnd
      // is the greater of its own end position and the maxEnd of the tree root.
      maxEnds = new int[nIndexNodes][0];
      for (int which_i = 0; which_i < indexNodes.length; ++which_i) {
        final int i = indexNodes[which_i];
        final int n_i =
            (which_i + 1 < indexNodes.length ? indexNodes[which_i + 1] : begs.length) - i;
        assert Integer.bitCount(n_i) == 1;
        // allocate maxEnd space for the index node and every fourth tree node
        final int[] maxEnd = new int[1 + (n_i - 1) / 4];
        if (n_i >= 8) {
          // tree with >=2 levels: recursively compute maxEnd[1:] and set maxEnd[0] to maxEnd
          // of index node and tree root
          final int root = rootNode(n_i - 1);
          assert nodeLevel(root) == rootLevel(n_i - 1);
          recurseMaxEnds(maxEnd, i + 1, root, nodeLevel(root));
          maxEnd[0] = max(ends[i], maxEnd[root / 4 + 1]);
        } else {
          // degenerate tree with <8 nodes: scan for maxEnd[0]
          maxEnd[0] = ends[i];
          for (int j = i + 1; j < i + n_i; ++j) {
            maxEnd[0] = max(maxEnd[0], ends[j]);
          }
        }
        maxEnds[which_i] = maxEnd;
      }
    }

    private void recurseMaxEnds(int[] ans, int ofs, int node, int lvl) {
      // for the subtree rooted at (ofs+node), compute interval tree augmentation values for
      // levels >= 2 (every fourth node in the array slice), storing in ans
      if (lvl > 2) {
        // assert node >= 7 && node % 4 == 3;
        Integer maxEnd = ends[ofs + node];
        // recurse left and consider left's maxEnd
        final int lch = nodeLeftChild(node, lvl);
        // assert lch < node && lch % 4 == 3;
        recurseMaxEnds(ans, ofs, lch, lvl - 1);
        maxEnd = max(maxEnd, ans[lch / 4 + 1]);
        // recurse right and consider right's maxEnd
        final int rch = nodeRightChild(node, lvl);
        // assert rch > node && rch % 4 == 3;
        recurseMaxEnds(ans, ofs, rch, lvl - 1);
        maxEnd = max(maxEnd, ans[rch / 4 + 1]);
        // store maxEnd at the appropriate location in ans
        ans[node / 4 + 1] = maxEnd;
      } else if (lvl == 2) {
        // level 2: scan 7 items to compute maxEnd
        final int scanL = ofs + nodeLeftmostChild(node, lvl);
        final int scanR = ofs + nodeRightmostChild(node, lvl);
        // assert node >= 3 && node % 4 == 3 && scanR - scanL == 6;
        Integer maxEnd = ends[scanL];
        for (int j = scanL + 1; j <= scanR; ++j) {
          maxEnd = max(maxEnd, ends[j]);
        }
        ans[node / 4 + 1] = maxEnd;
      }
    }

    /**
     * @hidden
     */
    public void validate() {
      final int n = begs.length;
      assert ends.length == n;
      assert maxEnds.length == indexNodes.length;
      assert permute == null || permute.length == n;

      int m = 0;
      for (int which_i = 0; which_i < indexNodes.length; ++which_i) {
        final int i = indexNodes[which_i];
        final int n_i =
            (which_i + 1 < indexNodes.length ? indexNodes[which_i + 1] : begs.length) - i;
        final int[] maxEnd = maxEnds[which_i];
        assert maxEnd.length == 1 + (n_i - 1) / 4;
        for (int j = i; j < i + n_i; ++j, ++m) {
          assert ends[j] >= begs[j];
          if (j > 0) {
            if (begs[j] == begs[j - 1]) {
              assert ends[j] >= ends[j - 1];
            } else {
              assert begs[j] > begs[j - 1];
            }
          }
          assert ends[j] <= maxEnd[0];
          if (j - i > 0 && (j - i) % 4 == 0) {
            assert ends[j] <= maxEnd[(j - i) / 4];
          }
        }
      }
      assert n == m;
    }

    /**
     * @return Total number of intervals stored.
     */
    public int size() {
      return begs.length;
    }

    /**
     * Result from a query, an interval and its ID as returned by {@link Builder#add Builder.add()}
     */
    public static class QueryResult {
      public final int beg;
      public final int end;
      public final int id;

      public QueryResult(int beg, int end, int id) {
        this.beg = beg;
        this.end = end;
        this.id = id;
      }

      public String toString() {
        // for debugging
        return "[" + String.valueOf(beg) + "," + String.valueOf(end) + ")=" + String.valueOf(id);
      }

      @Override
      public boolean equals(Object rhso) {
        if (rhso instanceof QueryResult) {
          QueryResult rhs = (QueryResult) rhso;
          return beg == rhs.beg && end == rhs.end && id == rhs.id;
        }
        return false;
      }
    }

    /**
     * Query for all stored intervals overlapping the given interval.
     *
     * @param queryBeg Query interval begin position (inclusive)
     * @param queryEnd Query interval end position (exclusive). Given a query interval [x,y), stored
     *     intervals [w,x) and [y,z) are abutting, but NOT overlapping the query, so would not be
     *     returned.
     * @param callback Predicate function to be called with each query result; it may return true to
     *     continue the query, or false to stop immediately.
     */
    public void queryOverlap(int queryBeg, int queryEnd, Predicate<QueryResult> callback) {
      queryOverlapInternal(
          queryBeg,
          queryEnd,
          i -> callback.test(new QueryResult(begs[i], ends[i], permute != null ? permute[i] : i)));
    }

    /**
     * Query for all stored intervals overlapping the given interval.
     *
     * @param queryBeg Query interval begin position (inclusive)
     * @param queryEnd Query interval end position (exclusive). Given a query interval [x,y), stored
     *     intervals [w,x) and [y,z) are abutting, but NOT overlapping the query, so would not be
     *     returned.
     * @return Materialized list of {@link QueryResult QueryResult} (not preferred for large result
     *     sets)
     */
    public List<QueryResult> queryOverlap(int queryBeg, int queryEnd) {
      final ArrayList<QueryResult> results = new ArrayList<QueryResult>();
      queryOverlap(
          queryBeg,
          queryEnd,
          x -> {
            results.add(x);
            return true;
          });
      return results;
    }

    /**
     * Query for all stored intervals overlapping the given interval, optimized for callers that
     * only need the ID of each result (as generated by {@link Builder#add Builder.add()}. Overhead
     * is reduced by avoiding allocation of {@link QueryResult QueryResult} objects.
     *
     * @param queryBeg Query interval begin position (inclusive)
     * @param queryEnd Query interval end position (exclusive). Given a query interval [x,y), stored
     *     intervals [w,x) and [y,z) are abutting, but NOT overlapping the query, so would not be
     *     returned.
     * @param callback Predicate function to be called with each query result ID; it may return true
     *     to continue the query, or false to stop immediately.
     */
    public void queryOverlapId(int queryBeg, int queryEnd, IntPredicate callback) {
      queryOverlapInternal(
          queryBeg, queryEnd, i -> callback.test(permute != null ? permute[i] : i));
    }

    /**
     * Query for any one stored interval overlapping the given interval.
     *
     * @param queryBeg Query interval begin position (inclusive)
     * @param queryEnd Query interval end position (exclusive). Given a query interval [x,y), stored
     *     intervals [w,x) and [y,z) are abutting, but NOT overlapping the query, so would not be
     *     returned.
     * @return null if there are no overlapping intervals stored.
     */
    public QueryResult queryAnyOverlap(int queryBeg, int queryEnd) {
      final QueryResult[] box = {null};
      queryOverlap(
          queryBeg,
          queryEnd,
          x -> {
            box[0] = x;
            return false;
          });
      return box[0];
    }

    /**
     * Query for any one ID of a stored interval overlapping the given interval.
     *
     * @param queryBeg Query interval begin position (inclusive)
     * @param queryEnd Query interval end position (exclusive). Given a query interval [x,y), stored
     *     intervals [w,x) and [y,z) are abutting, but NOT overlapping the query, so would not be
     *     returned.
     * @return -1 if there are no overlapping intervals stored.
     */
    public int queryAnyOverlapId(int queryBeg, int queryEnd) {
      final int[] box = {-1};
      queryOverlapId(
          queryBeg,
          queryEnd,
          i -> {
            box[0] = i;
            return false;
          });
      return box[0];
    }

    /**
     * Query whether there exists any stored interval overlapping the given interval.
     *
     * @param queryBeg Query interval begin position (inclusive)
     * @param queryEnd Query interval end position (exclusive). Given a query interval [x,y), stored
     *     intervals [w,x) and [y,z) are abutting, but NOT overlapping the query, so would not be
     *     returned.
     */
    public boolean queryOverlapExists(int queryBeg, int queryEnd) {
      return queryAnyOverlapId(queryBeg, queryEnd) >= 0;
    }

    /**
     * Query for IDs of all stored intervals exactly equalling the given interval.
     *
     * @param callback Predicate function to be called with each query result ID; it may return true
     *     to continue the query, or false to stop immediately.
     */
    public void queryExactId(int queryBeg, int queryEnd, IntPredicate callback) {
      int p = Arrays.binarySearch(begs, queryBeg);
      if (p >= 0) {
        for (; p > 0 && begs[p - 1] == queryBeg && ends[p - 1] >= queryEnd; --p)
          ;
        for (; p < begs.length && begs[p] == queryBeg && ends[p] <= queryEnd; ++p) {
          if (ends[p] == queryEnd && !callback.test(permute != null ? permute[p] : p)) {
            return;
          }
        }
      }
    }

    /**
     * Query for any one ID of a stored interval exactly equalling the given interval.
     *
     * @return -1 if there are no equal intervals stored.
     */
    public int queryAnyExactId(int queryBeg, int queryEnd) {
      final int[] box = {-1};
      queryExactId(
          queryBeg,
          queryEnd,
          i -> {
            box[0] = i;
            return false;
          });
      return box[0];
    }

    /** Query whether there exists any stored interval exactly equalling the given interval. */
    public boolean queryExactExists(int queryBeg, int queryEnd) {
      return queryAnyExactId(queryBeg, queryEnd) >= 0;
    }

    /**
     * Query for all stored intervals
     *
     * @param callback Predicate function to be called with each query result; it may return true to
     *     continue the query, or false to stop immediately.
     */
    public void queryAll(Predicate<QueryResult> callback) {
      for (int i = 0; i < begs.length; i++) {
        if (!callback.test(new QueryResult(begs[i], ends[i], permute != null ? permute[i] : i))) {
          return;
        }
      }
    }

    private void queryOverlapInternal(int queryBeg, int queryEnd, IntPredicate callback) {
      QueryContext query = null;
      // for each index node
      for (int which_i = 0; which_i < indexNodes.length; ++which_i) {
        final int i = indexNodes[which_i];
        final int[] maxEnd = maxEnds[which_i];
        if (begs[i] >= queryEnd) {
          break; // whole remainder of the beg-sorted array must be irrelevant
        } else if (maxEnd[0] > queryBeg) { // slice has relevant item(s)
          if (ends[i] > queryBeg) { // index node is a hit itself, return it first
            if (!callback.test(i)) {
              return;
            }
          }
          // search the subsequent tree, formed by the slice from (i+1) until the next index
          // node. The root is in the middle at an offset calculable from the tree size (=
          // slice length).
          final int n_i =
              (which_i + 1 < indexNodes.length ? indexNodes[which_i + 1] : begs.length) - i;
          if (n_i > 1) {
            if (query == null) {
              query = new QueryContext(queryBeg, queryEnd, callback);
            }
            query.maxEnd = maxEnd;
            query.ofs = i + 1;
            recurseQuery(query, rootNode(n_i - 1), rootLevel(n_i - 1));
          }
        }
      }
    }

    private class QueryContext {
      // query interval
      public final int beg;
      public final int end;
      // result callback function
      public final IntPredicate callback;
      // maxEnd array for relevant slice of main item array
      public int[] maxEnd = null;
      // offset of leftmost leaf in main item array
      public int ofs = -1;

      public QueryContext(int beg, int end, IntPredicate callback) {
        this.beg = beg;
        this.end = end;
        this.callback = callback;
      }
    }

    private boolean recurseQuery(QueryContext query, int node, int lvl) {
      if (lvl >= 2 && query.maxEnd[node / 4 + 1] <= query.beg) {
        // this subtree can't overlap query
        return true;
      } else if (lvl > 2) {
        // search left subtree
        if (!recurseQuery(query, nodeLeftChild(node, lvl), lvl - 1)) {
          return false;
        }
        final int i = query.ofs + node;
        if (begs[i] < query.end) { // root or right subtree possibly relevant
          if (ends[i] > query.beg) { // current root overlaps
            if (!query.callback.test(i)) {
              return false;
            }
          }
          // search right subtree
          if (!recurseQuery(query, nodeRightChild(node, lvl), lvl - 1)) {
            return false;
          }
        }
      } else {
        // lvl <= 2: once we get down to a subtree of <= 7 items, just scan left-to-right.
        // this isn't going to be significantly slower than the recursion, and elides maxEnd
        // storage for the 75% of nodes on levels 0 & 1.
        final int scanL = query.ofs + nodeLeftmostChild(node, lvl);
        final int scanR = query.ofs + nodeRightmostChild(node, lvl);
        for (int j = scanL; j <= scanR && begs[j] < query.end; ++j) {
          if (ends[j] > query.beg && !query.callback.test(j)) {
            return false;
          }
        }
      }
      return true;
    }

    // cgranges node rank calculations

    private static int nodeLevel(int node) {
      return Integer.numberOfTrailingZeros(~node);
    }

    private static int rootLevel(int treeSize) {
      return 31 - Integer.numberOfLeadingZeros(treeSize); // floor(log2(treeSize))
    }

    private static int rootNode(int treeSize) {
      return (1 << rootLevel(treeSize)) - 1;
    }

    private static int nodeLeftChild(int node, int lvl) {
      return node - (1 << (lvl - 1));
    }

    private static int nodeLeftmostChild(int node, int lvl) {
      return node - (1 << lvl) + 1;
    }

    private static int nodeRightChild(int node, int lvl) {
      return node + (1 << (lvl - 1));
    }

    private static int nodeRightmostChild(int node, int lvl) {
      return node + (1 << lvl) - 1;
    }

    private static int max(int lhs, int rhs) {
      // avoiding Doub1e.max b/c Short.max does not exist for some reason
      return lhs >= rhs ? lhs : rhs;
    }
  }
}
