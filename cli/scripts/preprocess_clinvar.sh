#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"

usage() {
  cat <<EOF
usage: ${SCRIPT_NAME} [arguments]

  -i, --input   FILE  input ClinVar TSV.gz path                 (required)
  -o, --output  FILE  output path                               (optional)
  -c, --contig  STR   filter input to this contig               (optional)
  -f, --force         override output file if it already exists (optional)
  -h, --help          print this message and exit

  if --output is not provided, the output path is derived from --input:
    input.tsv.gz                 -> input.preprocessed.tsv.gz
    input.tsv.gz --contig chr21  -> input.preprocessed.chr21.tsv.gz

  see https://download.molgeniscloud.org/downloads/vip/resources/GRCh38/clinvar_20260822_stripped.tsv.gz
  see https://download.molgeniscloud.org/downloads/vip/resources/GRCh38/clinvar_20260822_stripped.tsv.gz.tbi
  see https://github.com/molgenis/vip/blob/v9.3.1/utils/create_clinvar.sh
EOF
}

validate() {
  local -r input="${1}"
  local -r output="${2}"
  local -r contig="${3}"
  local -r force="${4}"

  # validate required commands
  local command
  for command in tabix bgzip; do
    if ! command -v "${command}" &>/dev/null; then
      >&2 echo "error: '${command}' could not be found"
      exit 2
    fi
  done

  # validate required arguments
  if [[ -z "${input}" ]]; then
    >&2 echo "error: missing required -i / --input"
    usage
    exit 2
  fi

  # validate input file
  if [[ ! -f "${input}" ]]; then
    >&2 echo "error: '${input}' does not exist"
    exit 2
  fi

  # validate tabix index
  if [[ ! -f "${input}.tbi" && ! -f "${input}.csi" ]]; then
    >&2 echo "error: tabix index for '${input}' does not exist"
    exit 2
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
  local -r input="${1}"
  local -r contig="${2}"

  local output="${input%.tsv.gz}.preprocessed"

  if [[ -n "${contig}" ]]; then
    output="${output}.${contig}"
  fi

  echo "${output}.tsv.gz"
}

build() {
  local -r input="${1}"
  local -r output="${2}"
  local -r contig="${3}"
  local -r force="${4}"

  # create output directory if necessary
  local output_dir
  output_dir="$(dirname "${output}")"
  mkdir -p "${output_dir}"

  if [[ -n "${contig}" ]]; then
    tabix -h "${input}" "${contig}"
  else
    zcat "${input}"
  fi |
  awk -v FS='\t' -v OFS='\t' '
  BEGIN {
    valid_chrom["chr1"] = 1
    valid_chrom["chr2"] = 1
    valid_chrom["chr3"] = 1
    valid_chrom["chr4"] = 1
    valid_chrom["chr5"] = 1
    valid_chrom["chr6"] = 1
    valid_chrom["chr7"] = 1
    valid_chrom["chr8"] = 1
    valid_chrom["chr9"] = 1
    valid_chrom["chr10"] = 1
    valid_chrom["chr11"] = 1
    valid_chrom["chr12"] = 1
    valid_chrom["chr13"] = 1
    valid_chrom["chr14"] = 1
    valid_chrom["chr15"] = 1
    valid_chrom["chr16"] = 1
    valid_chrom["chr17"] = 1
    valid_chrom["chr18"] = 1
    valid_chrom["chr19"] = 1
    valid_chrom["chr20"] = 1
    valid_chrom["chr21"] = 1
    valid_chrom["chr22"] = 1
    valid_chrom["chrX"] = 1
    valid_chrom["chrY"] = 1
    valid_chrom["chrM"] = 1
  }
  
  /^#/ {
    print
    next
  }
  
  {
    # print progress every 500,000 input records
      if (NR % 500000 == 0)
        print "processed " NR " records" > "/dev/stderr"
        
    # skip records with unsupported chromosome
    if (!($1 in valid_chrom))
      next
        
    # skip records with empty alternate allele
    if ($5 == ".")
      next

    # skip non-ACGT alternate alleles
    if ($5 !~ /^[ACGT]+$/)
      next

    # replace dot values with empty string
    for (i = 1; i <= NF; i++)
      if ($i == ".")
        $i = ""

    # replace pipe with comma in CLNSIG column
    gsub(/\|/, ",", $6)
    
    print
  }' |
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
  args=$(getopt -a -n "${SCRIPT_NAME}" -o i:o:c:fh --long input:,output:,contig:,force,help -- "$@")

  local input=""
  local output=""
  local contig=""
  local force="0"

  eval set -- "${args}"

  while :; do
    case "$1" in
      -i | --input)
        input="$2"
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

  if [[ -z "${output}" ]]; then
    output="$(derive_output "${input}" "${contig}")"
  fi

  validate "${input}" "${output}" "${contig}" "${force}"
  build "${input}" "${output}" "${contig}" "${force}"
}

main "$@"
