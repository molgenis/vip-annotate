# todo

## app: annotate

### format

- [ ] write annotations to pipe-separated VIP INFO field similar to VEP CSQ.
    - [ ] remove VIP header and annotations if it exists.
    - [ ] persist metadata of individual annotations in VIP header.
    - [ ] persist vip-annotate version in VIP header.

### cli

- [ ] introduce -O / --output-type v|z[0-9] for compressed VCF (z), uncompressed VCF (v) with compression level,
  determine
  the output type from the output file if available.
    - [ ] write bgzip instead of gzip for compressed VCF,
      see https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java.
- [x] introduce -d / --debug to expose stack traces.
- [ ] update -a / --annotations to specify annotations directory instead of the annotation database file.

## app: build

- [ ] build annotation database parts separately to allow for parallel building and partial updating.