# todo

in addition to below see >100 TODO items in code

## 0.0.1-alpha.7

- [ ] feat (spec): add annotation type=string
- [ ] feat (spec): add annotation type=floating_point encoding=lossless/lossy_u8/lossy_u16
- [ ] feat (spec): optional map input enum values (e.g. Likely_pathogenic to LP)
- [ ] feat (db): reintroduce dictionary support (e.g. SpliceAI ncbiGeneId)
- [ ] feat (db): null bitmap instead of nullable types (rank1 to determine index)
- [ ] feat (db): determine encoding per-partition instead of per-input (e.g. sharper min-max, enum subsets)
- [ ] perf (db): medium 64-bit sequence variant index
- [ ] perf (db): use https://mvnrepository.com/artifact/ch.randelshofer/fastdoubleparser/2.0.1
- [ ] feat (annotate): write max error in output vcf header
- [ ] refactor: bump streamvbyte to v3.0.0

## 0.0.1-alpha.8

- [ ] feat (spec): input/type=vcf: nested value support
- [ ] feat (db): support nested composite annotations (e.g. CLINSIGINCL 431417:Pathogenic|585009:Likely_pathogenic)
- [ ] feat (db): add 'all_single_nucleotide_variants' indexless-db (e.g. avi)
- [ ] feat (db): use [ALP](https://ir.cwi.nl/pub/33334/33334.pdf) for lossless float storage
- [ ] feat (db): support bit-level packing for ranged values
- [ ] feat (annotate): write bgzip instead of gzip for compressed VCF,
  see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
  and https://github.com/browning-lab/hap-ibd/blob/master/src/blbutil/BGZIPOutputStream.java. or use native lib?
- [ ] refactor (db): create gnomad db from source files instead of derived files on next gnomAD release
- [ ] fix (db): fathmm annotation resource can contain multiple chr-pos-ref_len-alt annotations
- [ ] refactor (db): create fathmm db from source files instead of derived GREENDB files
- [ ] feat (scripts): validate contigs in all preprocessing scripts

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

- [ ] feat: effect prediction
- [ ] feat (annotate): how to annotate PositionAnnotationDb for SV? SVLEN could be too long (chr2-166299171-A-<DEL>)
- [ ] feat (db): validate that annotation reference is normalized
- [ ] feat (annotate): write gene index to header e.g. ##GENEIDX=<ID=0,SRCID=672,SYMBOL=BRCA1>
- [ ] perf: native-image profile guided optimization
- [ ] refactor: move zstd-ffm to separate repository
- [ ] refactor: move streamvbyte-ffm to separate repository
- [ ] refactor: move vdb to separate repository
- [ ] refactor: move vcf to separate repository
- [ ] perf: replace streamvbyte with https://github.com/powturbo/TurboPFor-Integer-Compression which support 8/16/32/64
  bit packing (thought: how far can we get with bit packing only and no zstd?). quality seems low though.

### other

- [ ] create a follow-up story to use vip-annotate v1 in vip
- [ ] create a follow-up story for vip-annotate v2