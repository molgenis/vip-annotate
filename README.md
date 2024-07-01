# vip-annotate

Variant Call Format (VCF) file annotation

## Usage

### Annotate

```
Usage:
  vip-annotate [OPTIONS] --annotations DIR --input FILE --output FILE

Options:
  -a, --annotations DIR       Directory containing annotation database  (required)
  -i, --input       FILE      Input VCF file path; use '-' for stdin    (required)
  -o, --output      FILE      Output VCF file path; use '-' for stdout  (required)

  -O, --output-type v|z[0-9]  Output format                             (default: z)
                                Options:
                                  v      Uncompressed VCF
                                  z      Compressed VCF (default compression)
                                  z0-z9  Compressed VCF with compression levels 0-9

  -f, --force                 Overwrite existing output file if it exists
  -d, --debug                 Enable debug logging
  -v, --version               Show version information and exit
  -h, --help                  Show this help message and exit
```

### Build

```
Usage:
  vip-annotate-db [OPTIONS] <command> [ARGS...]

Options:
  -d, --debug       Enable debug logging
  -v, --version     Show version and exit
  -h, --help        Show this help message and exit

Commands:
  fathmm_mkl        Build FATHMM-MKL database
  gnomad            Build gnomAD database
  ncer              Build NCER database
  phylop            Build PhyloP database
  remm              Build ReMM database
  spliceai          Build SpliceAI database
```

## Development

Set Maven property `env=dev` to activate `dev` and `dev-<os>` profiles. 