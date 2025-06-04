# todo

## app: annotate

### format

## app: build

- [ ] feat: validate that annotation reference matches GCA_000001405.15_GRCh38_no_alt_analysis_set reference?
- [ ] feat: validate that annotation reference is normalized?
- [ ] build annotation database parts separately to allow for parallel building and partial updating.
- [x] restructure phyloP/ncER/REMM zip folder structure
- [ ] try: gnomAD annotation file with separate files for each score element to get rid of offset index

## app: annotate

- [ ] fix: gnomAD annotator
- [ ] fix: AppArgsParser.getVersion()
- [ ] fix: native image build broken (com.google.protobuf.* was unintentionally initialized at build time)
- [ ] perf: batch annotate in VcfRecordAnnotatorAggregator
- [ ] test: unit tests
- [ ] test: system tests
- [ ] build: remove unused dependencies
- [ ] refactor: code cleanup
- [ ] refactor: replace with commons-compress 1.28.0-SNAPSHOT with something else
- [ ] feat: write bgzip instead of gzip for compressed VCF,
  see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
  and https://github.com/browning-lab/hap-ibd/blob/master/src/blbutil/BGZIPOutputStream.java.
- [ ] feat: write annotations to pipe-separated VIP INFO field similar to VEP CSQ.
    - [ ] remove VIP header and annotations if it exists.
    - [ ] persist metadata of individual annotations in VIP header.
    - [ ] persist vip-annotate version in VIP header.