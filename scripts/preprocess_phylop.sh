#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"

usage() {
  echo -e "usage: ${SCRIPT_NAME} [arguments]
  -i, --input   FILE  Input 'hg38.phyloP100way.bw' path              (required)
  -o, --output  DIR   Output 'hg38.phyloP100way.bed.gz' path         (required)
  -f, --force         Override the output file if it already exists  (optional)
  -h, --help          Print this message and exit

  requirements: 'bgzip' and 'tabix' available on path"
}

validate() {
  local -r input="${1}"
  local -r output="${2}"
  local -r force="${3}"

  # validate required args
  if [[ -z "${input}" ]]; then
    >&2 echo -e "error: missing required -i / --input"
    usage
    exit 2
  fi
  if [[ -z "${output}" ]]; then
    >&2 echo -e "error: missing required -o / --output"
    usage
    exit 2
  fi

  # validate input
  if [[ ! -f "${input}" ]]; then
    >&2 echo -e "error: input '${input}' does not exist"
    exit 2
  fi

  # validate output
  if [[ "${force}" == "0" ]] && [[ -f "${output}" ]]; then
    echo -e "error: output ${output} already exists, use -f to overwrite."
    exit 2
  fi

  # validate commands available
  if ! command -v bgzip &>/dev/null; then
    echo "error: 'bgzip' could not be found"
    exit 2
  fi
  if ! command -v tabix &>/dev/null; then
    echo "error: 'tabix' could not be found"
    exit 2
  fi
}

preprocess() {
  local -r input="${1}"
  local -r output="${2}"

  local -r output_tmp_wig="tmp.wig"
  local -r output_tmp_bed="${output%.*}"

  # convert big wig to wig
  wget --quiet --continue https://hgdownload.cse.ucsc.edu/admin/exe/linux.x86_64/bigWigToWig
  chmod +x bigWigToWig
  ./bigWigToWig "${input}" "${output_tmp_wig}"

  # convert wig to bed
  wget --quiet --continue https://github.com/bedops/bedops/releases/download/v2.4.41/bedops_linux_x86_64-v2.4.41.tar.bz2
  tar jxf bedops_linux_x86_64-v2.4.41.tar.bz2
  PATH=./bin:$PATH wig2bed --max-mem 4G < "${output_tmp_wig}" > "${output_tmp_bed}"
  rm "${output_tmp_wig}"

  # convert bed to bed.gz and index
  bgzip "${output_tmp_bed}"
  tabix "${output}"
}

main() {
  local args
  args=$(getopt -a -n install -o i:o:fh --long input:,output:,force,help -- "$@")

  local input=""
  local output=""
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

  validate "${input}" "${output}" "${force}"

  if [[ "${force}" == "1" ]] && [[ -f "${output}" ]]; then
    rm "${output}"
  fi

  preprocess "${input}" "${output}"
}

main "${@}"