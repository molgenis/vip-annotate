# todo

## 0.0.1-alpha.6

- [ ] fix (db): fix vcf support
- [ ] fix: support I64, U32, U64 writing and reading
- [ ] fix (annotate): SpliceAI can produce multiple annotations for same sequence variant
- [ ] fix (db): build_annotation_databases.sh
- [ ] fix (db): reintroduce xref support (e.g. SpliceAI ncbiGeneId)
- [ ] feat: add BOOL support
- [ ] feat (spec): replace "annotations": { "my_enum": 4 } with { "my_enum":{ "col": 4, mis_val: "."} }
- [ ] feat (spec): automatically determine encoding and storage from logical type
- [ ] feat (spec): only store relevant spec part in db (e.g. do not store input info)
- [ ] feat (spec): do not put file paths in spec (e.g. allow using on chr21 and all)
- [ ] feat (spec): support non-nullable enum_set?
- [ ] feat (db): validate contigs in all preprocessing scripts
- [ ] feat (db): support bit-level packing for ranged values
- [ ] feat (db): determine encoding per-partition (e.g. sharper min-max, enum subsets)
- [ ] feat (db): support ClinVar CLINSIGINCL (e.g. 431417:Pathogenic|585009:Likely_pathogenic)
- [ ] perf (db): improve database building performance (e.g. SpliceAI takes > 24 hours to build)
- [ ] perf (db): ClinVar vdb 135M > source data 43M. why?
- [ ] refactor: bump streamvbyte to v3.0.0
- [ ] refactor: make Annotation a sealed interface

## 0.0.1-alpha.7

- [ ] feat: write max error in output vcf header
- [ ] feat (annotate): write bgzip instead of gzip for compressed VCF,
  see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
  and https://github.com/browning-lab/hap-ibd/blob/master/src/blbutil/BGZIPOutputStream.java. or use native lib?
- [ ] refactor (db): create gnomad db from source files instead of derived files on next gnomAD release
- [ ] fix (db): fathmm annotation resource can contain multiple chr-pos-ref_len-alt annotations
- [ ] refactor (db): create fathmm db from source files instead of derived GREENDB files

## 0.0.1-beta

- [ ] feat: check if output extension is in line with output mode
- [ ] feat: resource versioning
- [ ] fix: resolve reported nullability issues (fix or suppress)
- [ ] fix: resolve FIXME and TODO in code
- [ ] docs: update
- [ ] test: with different real datasets

## 1.0.0

- [ ] feat: install.sh script that downloads resources and container + put data on download server

### after 1.0.0

- [ ] feat (annotate): how to annotate PositionAnnotationDb for SV? SVLEN could be too long (chr2-166299171-A-<DEL>)
- [ ] feat (db): validate that annotation reference is normalized
- [ ] feat (annotate): write gene index to header e.g. ##GENEIDX=<ID=0,SRCID=672,SYMBOL=BRCA1>
- [ ] perf: native-image profile guided optimization
- [ ] feat (annotate): write vcf header with structured metadata (e.g. FORMAT=X|Y TYPE_X=.. NUMBER_X=..)
- [ ] perf: medium 64-bit sequence variant index
- [ ] refactor: move zstd-ffm to separate repository
- [ ] refactor: move streamvbyte-ffm to separate repository
- [ ] refactor: move vdb to separate repository
- [ ] refactor: move vcf to separate repository
- [ ] perf: replace streamvbyte with https://github.com/powturbo/TurboPFor-Integer-Compression which support 8/16/32/64
  bit packing (thought: how far can we get with bit packing only and no zstd?). quality seems low though.

### other

- [ ] create a follow-up story to use vip-annotate v1 in vip
- [ ] create a follow-up story for vip-annotate v2