# todo

## app: annotate

### feat

- [ ] feat(annotate): write bgzip instead of gzip for compressed VCF,
  see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
  and https://github.com/browning-lab/hap-ibd/blob/master/src/blbutil/BGZIPOutputStream.java.
- [x] feat(db): validate that annotation reference matches `GCA_000001405.15_GRCh38_no_alt_analysis_set` reference
- [ ] feat(db): validate that annotation reference is normalized

### fix

- [ ] fix(annotate+db): native image build broken (com.google.protobuf.* was unintentionally initialized at build time)
- [ ] fix(db): investigate possible bug in gnomAD data e.g. 21-5029882-CAA-A
- [ ] fix(db): investigate possible bug in gnomAD data e.g. 21-5087539-G-A

### test

- [ ] test(annotate): benchmark VEP duration with gnomad/ncer/phylop/remm/spliceai vs without
- [ ] test(annotate): measure how much memory is used including off-heap memory
- [ ] test(annotate+db): unit tests
- [ ] test(annotate+db): system tests

### perf

- [ ] perf(build): build annotation database parts separately to allow for parallel building and partial updating.
- [ ] perf(build): gnomAD annotation file with separate files for each score element to get rid of offset index
- [ ] perf(build): zstd dictionary for ncer/phylop/remm
- [ ] perf(build): space-efficient alternative to BigInteger (Java bincode alternative?)
- [ ] perf(annotate): do not batch over partition borders (?)

### refactor

- [ ] refactor(annotate+db): code cleanup
- [ ] refactor(annotate+db): code deduplication
- [ ] refactor(annotate+db): replace commons-compress 1.28.0-SNAPSHOT dependency with something else
- [ ] refactor(db): create gnomad db from source files instead of derived files

### build

- [ ] build(annotate+db): Travis
- [ ] build(annotate+db): validate formatting