# todo

## v1

- [ ] fix(annotate+db): resolve reported nullability issues (fix or suppress)
- [ ] fix(db): CLI validation for individual build commands
- [ ] refactor(annotate+db): replace commons-compress 1.28.0-SNAPSHOT dependency with something else
- [ ] perf(build): space-efficient alternative to BigInteger (Java bincode alternative?), will break db format
- [ ] test(annotate): benchmark VEP duration with gnomad/ncer/phylop/remm/spliceai vs without
- [ ] test(annotate): measure how much memory is used including off-heap memory
- [ ] test(annotate+db): unit tests
- [ ] test(annotate+db): system tests
- [ ] build(annotate+db): Travis
- [ ] build(annotate+db): validate formatting

### other

- create a follow-up story to use vip-annotate v1 in vip

### discuss

- [ ] feat(annotate+db): add SpliceAI
- [ ] fix(annotate+db): native image build broken (com.google.protobuf.* was unintentionally initialized at build time)
- [ ] fix(annotations): investigate af-is-null-for-source issue in gnomAD, e.g., 21-5029882-CAA-A and 21-5087539-G-A
- [ ] refactor(db): create gnomad db from source files instead of derived files

## > v1

- [ ] feat(annotate): write bgzip instead of gzip for compressed VCF,
  see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
  and https://github.com/browning-lab/hap-ibd/blob/master/src/blbutil/BGZIPOutputStream.java.
- [ ] feat(db): validate that annotation reference is normalized
- [ ] perf(build): build annotation database parts separately to allow for parallel building and partial updating e.g.,
  build per contig and zip when done.
- [ ] refactor(annotate+db): add nullaway,
  see https://spring.io/blog/2025/03/10/null-safety-in-spring-apps-with-jspecify-and-null-away and https://jspecify.dev/
