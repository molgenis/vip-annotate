# todo

## app: annotate

### feat

- [ ] feat: write bgzip instead of gzip for compressed VCF,
  see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
  and https://github.com/browning-lab/hap-ibd/blob/master/src/blbutil/BGZIPOutputStream.java.
- [ ] feat: write annotations to pipe-separated VIP 'CSQ' field instead of separate annotations
- [ ] feat(build): validate that annotation reference matches `GCA_000001405.15_GRCh38_no_alt_analysis_set` reference
- [ ] feat(build): validate that annotation reference is normalized
- [x] feat(build): one CLI
- [x] feat: support compressed data on stdin

### fix

- [x] fix: gnomAD annotator
- [x] fix: App*ArgsParser.getVersion()
- [ ] fix: native image build broken (com.google.protobuf.* was unintentionally initialized at build time)
- [ ] fix: remove existing annotations
- [x] fix: build warnings
- [x] fix: read unzipped vcf

### test

- [ ] test: unit tests
- [ ] test: system tests
- [ ] test: benchmark VEP duration with gnomad/ncer/phylop/remm/spliceai vs without
- [ ] test: measure how much memory is used including off-heap memory

### perf

- [x] perf: batch annotate in VcfRecordAnnotatorAggregator
- [ ] perf(build): build annotation database parts separately to allow for parallel building and partial updating.
- [ ] perf(build): gnomAD annotation file with separate files for each score element to get rid of offset index
- [ ] perf(build): zstd dictionary for ncer/phylop/remm

### refactor

- [x] refactor(build): restructure phyloP/ncER/REMM zip folder structure
- [x] refactor: move effect code to branch
- [x] refactor: move splice ai code to branch
- [ ] refactor: code cleanup
- [ ] refactor: replace commons-compress 1.28.0-SNAPSHOT dependency with something else

### build

- [ ] build: remove unused dependencies
- [x] build: JaCoCo code coverage (measure / fail the build when coverage to low)
- [ ] build: Travis 