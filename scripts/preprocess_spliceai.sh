#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"

usage() {
  echo -e "usage: ${SCRIPT_NAME} [arguments]
  -i, --spliceai_indel  FILE  Input 'spliceai_scores.masked.indel.hg38.vcf.gz' path  (required)
  -s, --spliceai_snv    FILE  Input 'spliceai_scores.masked.snv.hg38.vcf.gz' path    (required)
  -o, --output          DIR   Output file path                                       (required)
  -f, --force                 Override the output file if it already exists.         (optional)
  -h, --help                  Print this message and exit

  requirements: 'bcftools' available on path"
}

validate() {
  local -r inputIndel="${1}"
  local -r inputSnv="${2}"
  local -r output="${3}"
  local -r force="${4}"

  # validate required args
  if [[ -z "${inputIndel}" ]]; then
    >&2 echo -e "error: missing required -i / --spliceai_indel"
    usage
    exit 2
  fi
  if [[ -z "${inputSnv}" ]]; then
    >&2 echo -e "error: missing required -s / --spliceai_snv"
    usage
    exit 2
  fi
  if [[ -z "${output}" ]]; then
    >&2 echo -e "error: missing required -o / --output"
    usage
    exit 2
  fi

  # validate inputIndel
  if [[ ! -f "${inputIndel}" ]]; then
    >&2 echo -e "error: input '${inputIndel}' does not exist"
    exit 2
  fi

  # validate inputSnv
  if [[ ! -f "${inputSnv}" ]]; then
    >&2 echo -e "error: input '${inputSnv}' does not exist"
    exit 2
  fi

  # validate output
  if [[ "${force}" == "0" ]] && [[ -f "${output}" ]]; then
    echo -e "error: output ${output} already exists, use -f to overwrite."
    exit 2
  fi

  # validate bcftools available
  if ! command -v bcftools &>/dev/null; then
    echo "error: 'bcftools' could not be found"
    exit 2
  fi
}

preprocess() {
  local -r inputIndel="${1}"
  local -r inputSnv="${2}"
  local -r output="${3}"

  local args=()
  args+=("concat")
  args+=("--output" "${output}")
  args+=("--output-type" "z")
  args+=("--write-index")
  args+=("--no-version")
  args+=("--threads" "4")
  args+=("--allow-overlaps")
  args+=("${inputIndel}")
  args+=("${inputSnv}")

  bcftools "${args[@]}"
}

main() {
  local args
  args=$(getopt -a -n install -o i:s:o:fh --long spliceai_indel:,spliceai_snv:,output:,force,help -- "$@")

  local inputIndel=""
  local inputSnv=""
  local output=""
  local force="0"

  eval set -- "${args}"
  while :; do
    case "$1" in
    -i | --spliceai_indel)
      inputIndel="$2"
      shift 2
      ;;
    -s | --spliceai_snv)
      inputSnv="$2"
      shift 2
      ;;
    -o | --output)
      output="$2"
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

  validate "${inputIndel}" "${inputSnv}" "${output}" "${force}"

  if [[ "${force}" == "1" ]] && [[ -f "${output}" ]]; then
    rm "${output}"
  fi

  preprocess "${inputIndel}" "${inputSnv}" "${output}"
}

main "${@}"