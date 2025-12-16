[![Build Status](https://app.travis-ci.com/molgenis/vip-annotate.svg?branch=main)](https://app.travis-ci.com/molgenis/vip-annotate)

# vip-annotate

Variant Call Format (VCF) file annotation

## Requirements

- **Operating system:** Linux
- **Container runtime:** [Apptainer ≥ 1.4.5](https://apptainer.org/)
- **CPU:** x86-64-v3 compatible

## Usage

### Annotate

```
Usage:
  apptainer run vip-annotate.sif [OPTIONS] --annotations DIR --input FILE --output FILE

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
  apptainer run vip-annotate-db.sif [OPTIONS] <command> [ARGS...]
  
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

## Development

### Requirements

- [GraalVM 25](https://www.graalvm.org/)
- [Maven 3.9.9](https://maven.apache.org/)
- Set Maven property `env=dev` to activate `dev` and `dev-<os>` profiles. 