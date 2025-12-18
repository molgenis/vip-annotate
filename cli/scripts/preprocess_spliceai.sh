#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"

usage() {
  cat <<EOF
usage: ${SCRIPT_NAME} [arguments]

  -s, --snv     FILE  input SpliceAI SNV VCF.gz path             (required)
  -i, --indel   FILE  input SpliceAI indel VCF.gz path           (required)
  -n, --ncbi    FILE  input NCBI gene mapping TSV path           (required)
  -o, --output  FILE  output path                                (optional)
  -c, --contig  STR   filter input to this contig                (optional)
  -f, --force         override output file if it already exists (optional)
  -h, --help          print this message and exit

  if --output is not provided, the output path is derived from --snv:
    input.hg38.vcf.gz                 -> input.hg38.preprocessed.tsv.gz
    input.hg38.vcf.gz --contig chr21  -> input.hg38.preprocessed.chr21.tsv.gz

  requirements: 'bcftools', 'bgzip', and 'tabix' available on path
EOF
}

validate() {
  local -r snv="${1}"
  local -r indel="${2}"
  local -r ncbi="${3}"
  local -r output="${4}"
  local -r contig="${5}"
  local -r force="${6}"

  # validate required commands
  local command
  for command in bcftools bgzip tabix; do
    if ! command -v "${command}" &>/dev/null; then
      >&2 echo "error: '${command}' could not be found"
      exit 2
    fi
  done

  # validate required arguments
  if [[ -z "${snv}" ]]; then
    >&2 echo "error: missing required -s / --snv"
    usage
    exit 2
  fi

  if [[ -z "${indel}" ]]; then
    >&2 echo "error: missing required -i / --indel"
    usage
    exit 2
  fi

  if [[ -z "${ncbi}" ]]; then
    >&2 echo "error: missing required -n / --ncbi"
    usage
    exit 2
  fi

  # validate input files
  if [[ ! -f "${snv}" ]]; then
    >&2 echo "error: '${snv}' does not exist"
    exit 2
  fi

  if [[ ! -f "${indel}" ]]; then
    >&2 echo "error: '${indel}' does not exist"
    exit 2
  fi

  if [[ ! -f "${ncbi}" ]]; then
    >&2 echo "error: '${ncbi}' does not exist"
    exit 2
  fi

  # validate tabix indexes when filtering by contig
  if [[ -n "${contig}" ]]; then
    if [[ ! -f "${snv}.tbi" ]]; then
      >&2 echo "error: tabix index '${snv}.tbi' does not exist"
      exit 2
    fi

    if [[ ! -f "${indel}.tbi" ]]; then
      >&2 echo "error: tabix index '${indel}.tbi' does not exist"
      exit 2
    fi
  fi

  # validate output
  if [[ -e "${output}" && "${force}" != "1" ]]; then
    >&2 echo "error: output '${output}' already exists; use -f / --force to overwrite"
    exit 2
  fi

  if [[ -e "${output}.tbi" && "${force}" != "1" ]]; then
    >&2 echo "error: output index '${output}.tbi' already exists; use -f / --force to overwrite"
    exit 2
  fi
}

derive_output() {
  local -r snv="${1}"
  local -r contig="${2}"

  local output="${snv%.vcf.gz}.preprocessed"

  if [[ -n "${contig}" ]]; then
    output="${output}.${contig}"
  fi

  echo "${output}.tsv.gz"
}

build() {
  local -r snv="${1}"
  local -r indel="${2}"
  local -r ncbi="${3}"
  local -r output="${4}"
  local -r contig="${5}"
  local -r force="${6}"

  # create output directory if necessary
  local output_dir
  output_dir="$(dirname "${output}")"
  mkdir -p "${output_dir}"

  # build bcftools concat arguments
  local args=()
  args+=("concat")
  args+=("--output-type" "v")
  args+=("--no-version")
  args+=("--threads" "4")
  args+=("--allow-overlaps")

  if [[ -n "${contig}" ]]; then
    args+=("--regions" "${contig}")
  fi

  args+=("${indel}")
  args+=("${snv}")

  bcftools "${args[@]}" |
  awk -v FS='\t' -v OFS='\t' -v ncbi="${ncbi}" '
  BEGIN {
    # load NCBI symbol -> gene ID mapping
    line_number = 0

    while ((getline line < ncbi) > 0) {
      if (line_number++ == 0)
        continue

      split(line, fields, "\t")
      gene_id[fields[2]] = fields[1]
    }

    close(ncbi)

    print "#[0]CHROM", "[1]POS", "[2]REF", "[3]ALT", "[4]NCBI_GENE_ID",
          "[5]DS_AG", "[6]DS_AL", "[7]DS_DG", "[8]DS_DL",
          "[9]DP_AG", "[10]DP_AL", "[11]DP_DG", "[12]DP_DL"
  }

  /^#/ {
    next
  }

  {
    # print progress every 500,000 input records
    if (NR % 500000 == 0)
      print "processed " NR " records" > "/dev/stderr"

    chrom = $1
    pos = $2
    ref = $4
    alt = $5
    info = $8

    # skip non-ACGT alternate alleles
    if (alt !~ /^[ACGT]+$/)
      next

    # find SpliceAI in INFO
    spliceai = $8
    sub(/^SPLICEAI=/, "", spliceai)
    split(spliceai, s, "|")

    symbol = s[2]

    # keep only symbols with an NCBI gene mapping
    if (!(symbol in gene_id))
      next

    ds_ag = s[3]
    ds_al = s[4]
    ds_dg = s[5]
    ds_dl = s[6]

    dp_ag = s[7]
    dp_al = s[8]
    dp_dg = s[9]
    dp_dl = s[10]

    # remove delta positions when the corresponding score is zero
    if (ds_ag == 0)
      dp_ag = ""

    if (ds_al == 0)
      dp_al = ""

    if (ds_dg == 0)
      dp_dg = ""

    if (ds_dl == 0)
      dp_dl = ""

    # prefix contigs with 'chr'
    print "chr" chrom, pos, ref, alt, gene_id[symbol],
          ds_ag, ds_al, ds_dg, ds_dl,
          dp_ag, dp_al, dp_dg, dp_dl
  }
  ' |
  bgzip -c > "${output}"

  # create tabix index
  if [[ "${force}" == "1" ]]; then
    tabix -f -s 1 -b 2 -e 2 "${output}"
  else
    tabix -s 1 -b 2 -e 2 "${output}"
  fi
}

main() {
  local args
  args=$(getopt -a -n "${SCRIPT_NAME}" -o s:i:n:o:c:fh --long snv:,indel:,ncbi:,output:,contig:,force,help -- "$@")

  local snv=""
  local indel=""
  local ncbi=""
  local output=""
  local contig=""
  local force="0"

  eval set -- "${args}"

  while :; do
    case "$1" in
      -s | --snv)
        snv="$2"
        shift 2
        ;;
      -i | --indel)
        indel="$2"
        shift 2
        ;;
      -n | --ncbi)
        ncbi="$2"
        shift 2
        ;;
      -o | --output)
        output="$2"
        shift 2
        ;;
      -c | --contig)
        contig="$2"
        shift 2
        ;;
      -f | --force)
        force="1"
        shift
        ;;
      -h | --help)
        usage
        exit 0
        ;;
      --)
        shift
        break
        ;;
      *)
        usage
        exit 2
        ;;
    esac
  done

  if [[ -z "${snv}" ]]; then
    >&2 echo "error: missing required -s / --snv"
    usage
    exit 2
  fi

  if [[ -z "${indel}" ]]; then
    >&2 echo "error: missing required -i / --indel"
    usage
    exit 2
  fi

  if [[ -z "${ncbi}" ]]; then
    >&2 echo "error: missing required -n / --ncbi"
    usage
    exit 2
  fi

  if [[ -z "${output}" ]]; then
    output="$(derive_output "${snv}" "${contig}")"
  fi

  validate "${snv}" "${indel}" "${ncbi}" "${output}" "${contig}" "${force}"
  build "${snv}" "${indel}" "${ncbi}" "${output}" "${contig}" "${force}"
}

main "$@"
