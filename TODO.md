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
- [ ] fix(annotations): investigate af-is-null-for-source issue in gnomAD, e.g., 21-5029882-CAA-A and 21-5087539-G-A

### test

- [ ] test(annotate): benchmark VEP duration with gnomad/ncer/phylop/remm/spliceai vs without
- [ ] test(annotate): measure how much memory is used including off-heap memory
- [ ] test(annotate+db): unit tests
- [ ] test(annotate+db): system tests

### perf

- [x] perf(build): gnomAD index file
- [ ] perf(build): build annotation database parts separately to allow for parallel building and partial updating e.g.,
  build per contig and zip when done.
- [x] perf(build): gnomAD annotation file with separate files for each score element to get rid of offset index
- [x] perf(build): zstd dictionary for ncer/phylop/remm (wont-fix)
- [ ] perf(build): space-efficient alternative to BigInteger (Java bincode alternative?)
- [ ] perf(annotate): do not batch over partition borders (?)

### refactor

- [ ] refactor(annotate+db): code cleanup
- [ ] refactor(annotate+db): code deduplication
- [ ] refactor(annotate+db): replace commons-compress 1.28.0-SNAPSHOT dependency with something else
- [ ] refactor(db): create gnomad db from source files instead of derived files
- [ ] refactor(annotate+db): add jspecify and nullaway,
  see https://spring.io/blog/2025/03/10/null-safety-in-spring-apps-with-jspecify-and-null-away and https://jspecify.dev/

### build

- [ ] build(annotate+db): Travis
- [ ] build(annotate+db): validate formatting