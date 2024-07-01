# todo

## 0.0.1-alpha

- [ ] build: add Travis deploy

## 0.0.1

- [ ] feat: install.sh script that downloads resources and container (similar to VIP)
- [ ] feat: config to build/use new annotation dbs without exe change (see draft_db_annotation_schema.json)
    - [ ] fix(annotations): fathmm annotation resource can contain multiple chr-pos-ref_len-alt annotations -->
      preprocessing script to remove duplicates and invalid -99 scores
    - [ ] fix(annotations): investigate af-is-null-for-source issue in gnomAD, e.g., 21-5029882-CAA-A and
      21-5087539-G-A -->
      preprocessing script to remove these cases
    - [ ] refactor(db): create gnomad db from source files instead of derived files on next gnomAD release
- [ ] feat(annotate): write bgzip instead of gzip for compressed VCF,
  see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
  and https://github.com/browning-lab/hap-ibd/blob/master/src/blbutil/BGZIPOutputStream.java. or use native lib?
    - [ ] feat(db): byte reproducible database zips
    - [ ] perf: use own archive format instead of zip for efficient dict reading or write own dic reader
- [ ] fix: resolve reported nullability issues (fix or suppress)
- [ ] fix: resolve FIXME and TODO in code
- [ ] docs: update

### after 0.0.1

- [ ] feat(annotate): how to annotate PositionAnnotationDb for SV? SVLEN could be too long (chr2-166299171-A-<DEL>)
- [ ] feat(db): validate that annotation reference is normalized
- [ ] feat(annotate): write gene index to header e.g. ##GENEIDX=<ID=0,SRCID=672,SYMBOL=BRCA1>
- [ ] perf: native-image profile guided optimization
- [ ] feat(annotate): write vcf header with structured metadata (e.g. FORMAT=X|Y TYPE_X=.. NUMBER_X=..)
- [ ] perf(build): build annotation database parts separately to allow for parallel building and partial updating e.g.,
  build per contig and zip when done.
- [ ] perf: medium 64-bit sequence variant index
- [ ] refactor: move zstd-ffm to separate repository
- [ ] refactor: move streamvbyte-ffm to separate repository
- [ ] perf: replace streamvbyte with https://github.com/powturbo/TurboPFor-Integer-Compression which support 8/16/32/64
  bit packing (thought: how far can we get with bit packing only and no zstd?). quality seems low though.
- [ ] refactor: use canonical
  layouts https://www.graalvm.org/latest/reference-manual/native-image/native-code-interoperability/ffm-api/

### other

- [ ] create a follow-up story to use vip-annotate v1 in vip
- [ ] create a follow-up story for vip-annotate v2