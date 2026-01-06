[![Build Status](https://app.travis-ci.com/molgenis/vip-annotate.svg?branch=main)](https://app.travis-ci.com/molgenis/vip-annotate)

# vip-annotate

Variant Call Format (VCF) file annotation

## Requirements

- **Operating system:** Linux
- **Container runtime:** [Apptainer ≥ 1.4.5](https://apptainer.org/)
- **CPU:** x86-64-v3 compatible

## Usage

```bash
apptainer run vip-annotate.sif --help
```

```
Usage:
  apptainer run vip-annotate.sif [OPTIONS] <command> [ARGS...]
  apptainer run vip-annotate.sif --version
  apptainer run vip-annotate.sif --help

Options:
  -d, --debug       Enable debug logging

Commands:
  annotate          Annotate vcf using an annotation database
  database-build    Build annotation database"""
```

### Command: annotate

```bash
apptainer run vip-annotate.sif annotate--help
```

```
Usage:
  apptainer run vip-annotate.sif annotate --annotations DIR --input FILE --output FILE [OPTIONS]
  apptainer run vip-annotate.sif annotate --help

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

### Command: download-database

```bash
apptainer run vip-annotate.sif database-download --help
```

```
Usage:
  apptainer run vip-annotate.sif database-download --output DIR [OPTIONS]
  apptainer run vip-annotate.sif database-download --help

Options:
  -o, --output      DIR      Output directory  (required)
  -f, --force                Overwrite existing output files if they exist
```

### Command: build-database

```bash
apptainer run vip-annotate.sif database-build --help
```

```
Usage:
  apptainer run vip-annotate.sif database-build <command> [ARGS...]
  apptainer run vip-annotate.sif database-build --help

Commands:
  fathmm_mkl        Build FATHMM-MKL database
  gnomad            Build gnomAD database
  ncer              Build NCER database
  phylop            Build PhyloP database
  remm              Build ReMM database
  spliceai          Build SpliceAI database
```

## Development

### Requirements

- [GraalVM 25](https://www.graalvm.org/)
- [Maven 3.9.11](https://maven.apache.org/)
- Set Maven property `env=dev` to activate `dev` and `dev-<os>` profiles. 