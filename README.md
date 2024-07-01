# vip-annotate

Variant Call Format (VCF) file annotation

## Usage

```
vip-annotate v0.0.0-dev

usage: java -jar vip-annotate.jar [arguments]
  -i, --input           FILE     input VCF file                           (optional, default: stdin       )
  -a, --annotations-dir DIR      annotation database directory            (required                       )
  -o, --output          FILE     output VCF file                          (optional, default: stdout      )
  -O, --output-type     v|z[0-9] uncompressed VCF (v), compressed VCF (z) (optional, default: uncompressed)
                                 with optional compression level 0-9

usage: java -jar vip-annotate.jar [arguments]
  -h, --help                 print this message

usage: java -jar vip-annotate.jar [arguments]
  -v, --version              print version
```