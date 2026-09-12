[![Build Status](https://app.travis-ci.com/molgenis/vip-annotate.svg?branch=main)](https://app.travis-ci.com/molgenis/vip-annotate)

# vip-annotate

Variant Call Format (VCF) file annotation

## Requirements

- **Operating system:** Linux
- **CPU:** x86-64-v3 compatible
- **Container runtime:** [Apptainer ≥ 1.4.5](https://apptainer.org/)

## Usage

```bash
vip-annotate --help
```

```
Usage:
  vip-annotate [OPTIONS] <command> [ARGS...]
  vip-annotate --version
  vip-annotate --help

Options:
  -d, --debug       Enable debug logging

Commands:
  annotate          Annotate vcf using an annotation database
  database-build    Build annotation database
  database-download Download annotation download

Report bugs and questions at https://github.com/molgenis/vip-annotate/issues
```

### Command: annotate

```bash
vip-annotate annotate --help
```

```
Usage:
  vip-annotate annotate --annotations DIR --input FILE --output FILE [OPTIONS]
  vip-annotate annotate --help

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
```

### Command: database-download

```bash
vip-annotate database-download --help
```

```
Usage:
  vip-annotate database-download --output DIR [OPTIONS]
  vip-annotate database-download --help

Options:
  -o, --output      DIR      Output directory  (required)
  -f, --force                Overwrite existing output files if they exist
```

### Command: database-build

```bash
vip-annotate database-build --help
```

```
Usage:
  vip-annotate database-build --input <FILE> --recipe <FILE> [OPTIONS]
  vip-annotate database-build --help

Options:
  -i, --input         FILE  Input file path; use '-' for stdin  (required)
  -r, --recipe        FILE  Database build recipe (.json)       (required)
  -o, --output-dir    DIR   Output directory
  -f, --force         Overwrite existing output file if it exists
```

#### Recipe

TODO

## Development

### Requirements

- **Container runtime:** [Apptainer ≥ 1.4.5](https://apptainer.org/)
- [GraalVM 25](https://www.graalvm.org/)
- [Maven 3.9.16](https://maven.apache.org/)
- Set Maven property `env=dev` to activate `dev` and `dev-<os>` profiles.