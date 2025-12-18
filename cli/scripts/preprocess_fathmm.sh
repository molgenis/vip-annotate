#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"

# TODO base on GRCh37/hg19 source data, see https://github.com/HAShihab/fathmm-MKL 
usage() {
  cat <<EOF
usage: ${SCRIPT_NAME} [arguments]

  -i, --input   FILE  input 'GRCh38_FATHMM-MKL_NC.tsv.gz' path                  (required)
  -o, --output  FILE  output path                                               (optional)
  -c, --contig  STR   filter input to this contig                               (optional)
  -f, --force         override output file if it already exists                 (optional)
  -h, --help          print this message and exit

  if --output is not provided, the output path is derived from --input:
    input.tsv.gz                 -> input.preprocessed.tsv.gz
    input.tsv.gz --contig chr21  -> input.preprocessed.chr21.tsv.gz

  see https://download.molgeniscloud.org/downloads/vip/resources/GRCh38/GRCh38_FATHMM-MKL_NC.tsv.gz
  see https://download.molgeniscloud.org/downloads/vip/resources/GRCh38/GRCh38_FATHMM-MKL_NC.tsv.gz.tbi
  see https://zenodo.org/records/3981121
  
  requirements: 'bgzip', and 'tabix' available on path
EOF
}

validate() {
  local -r input="${1}"
  local -r output="${2}"
  local -r contig="${3}"
  local -r force="${4}"

  # validate commands
  for command in bgzip tabix; do
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

  # validate input extension
  if [[ "${input}" != *.tsv.gz ]]; then
    >&2 echo "error: input file must end with '.tsv.gz': '${input}'"
    exit 2
  fi

  # validate input index when filtering by contig
  if [[ -n "${contig}" && ! -f "${input}.tbi" ]]; then
    >&2 echo "error: tabix index '${input}.tbi' does not exist"
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

# FIXME remove records with invalid ref values, see https://github.com/edg1983/GREEN-VARAN/issues/24
build() {
  local -r input="${1}"
  local -r output="${2}"
  local -r contig="${3}"
  local -r force="${4}"

  # create output directory if necessary
  local output_dir
  output_dir="$(dirname "${output}")"
  mkdir -p "${output_dir}"

  # use tabix for contig-restricted input, otherwise decompress directly
  local input_cmd
  if [[ -n "${contig}" ]]; then
    input_cmd=(tabix "${input}" "${contig}")
  else
    input_cmd=(gzip -dc "${input}")
  fi

  # preprocess and create bgzip-compressed output
  "${input_cmd[@]}" |
  awk -v FS='\t' -v OFS='\t' '
  {
    # print progress every 500,000 records
    if (NR % 500000 == 0)
      print "processed " NR " records" > "/dev/stderr"

    # reject records where score is -99, see see https://github.com/edg1983/GREEN-VARAN/issues/24
    if ($5 == -99)
      next

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

  if [[ -z "${input}" ]]; then
    >&2 echo "error: missing required -i / --input"
    usage
    exit 2
  fi

  if [[ -z "${output}" ]]; then
    output="$(derive_output "${input}" "${contig}")"
  fi

  validate "${input}" "${output}" "${contig}" "${force}"
  build "${input}" "${output}" "${contig}" "${force}"
}

main "$@"
