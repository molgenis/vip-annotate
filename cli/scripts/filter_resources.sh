#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"

usage() {
  echo -e "usage: ${SCRIPT_NAME} [arguments]
      --fathmm_mkl  FILE             Input 'GRCh38_FATHMM-MKL_NC.tsv.gz' path                                       (required)
      --gnomad      FILE             Input 'gnomad.total.v4.1.sites.stripped-v3.tsv.gz' path                        (required)
      --ncer        FILE             Input 'GRCh38_ncER_perc.bed.gz' path                                           (required)
      --phylop      FILE             Input 'hg38.phyloP100way.bed.gz' path, see 'preprocess_phylop.sh'              (required)
      --remm        FILE             Input 'ReMM.v0.4.hg38.tsv.gz' path                                             (required)
      --spliceai    FILE             Input 'spliceai_scores.masked.hg38.vcf.gz' path, see 'preprocess_spliceai.sh'  (required)
  -r, --region      chr|chr:beg-end  Region specified in the format 'chr:beginPos-endPos' (inclusive, 1-based)      (required)
  -o, --output      DIR              Output directory                                                               (required)
  -f, --force                        Override output files if they already exists.                                  (optional)
  -h, --help                         Print this message and exit

  requirements: 'bcftools', 'bgzip' and 'tabix' available on path"
}

validate() {
  local -r input_fathmm_mkl="${1}"
  local -r input_gnomad="${2}"
  local -r input_ncer="${3}"
  local -r input_phylop="${4}"
  local -r input_remm="${5}"
  local -r input_spliceai="${6}"
  local -r region="${7}"
  local -r output_dir="${8}"
  local -r force="${9}"

  # validate required args
  if [[ -z "${input_fathmm_mkl}" ]]; then
    >&2 echo -e "error: missing required --fathmm_mkl"
    usage
    exit 2
  fi
  if [[ -z "${input_gnomad}" ]]; then
    >&2 echo -e "error: missing required --gnomad"
    usage
    exit 2
  fi
  if [[ -z "${input_ncer}" ]]; then
    >&2 echo -e "error: missing required --ncer"
    usage
    exit 2
  fi
  if [[ -z "${input_phylop}" ]]; then
    >&2 echo -e "error: missing required --phylop"
    usage
    exit 2
  fi
  if [[ -z "${input_remm}" ]]; then
    >&2 echo -e "error: missing required --remm"
    usage
    exit 2
  fi
  if [[ -z "${input_spliceai}" ]]; then
    >&2 echo -e "error: missing required --spliceai"
    usage
    exit 2
  fi
  if [[ -z "${region}" ]]; then
    >&2 echo -e "error: missing required -r / --region"
    usage
    exit 2
  fi
  if [[ -z "${output}" ]]; then
    >&2 echo -e "error: missing required -o / --output"
    usage
    exit 2
  fi

  # validate files exist
  if [[ ! -f "${input_fathmm_mkl}" ]]; then
    >&2 echo -e "error: '${input_fathmm_mkl}' does not exist"
    exit 2
  fi
  if [[ ! -f "${input_gnomad}" ]]; then
    >&2 echo -e "error: '${input_gnomad}' does not exist"
    exit 2
  fi
  if [[ ! -f "${input_ncer}" ]]; then
    >&2 echo -e "error: '${input_ncer}' does not exist"
    exit 2
  fi
  if [[ ! -f "${input_phylop}" ]]; then
    >&2 echo -e "error: '${input_phylop}' does not exist"
    exit 2
  fi
  if [[ ! -f "${input_remm}" ]]; then
    >&2 echo -e "error: '${input_remm}' does not exist"
    exit 2
  fi
  if [[ ! -f "${input_spliceai}" ]]; then
    >&2 echo -e "error: '${input_spliceai}' does not exist"
    exit 2
  fi

  # validate output
  if [[ "${force}" == "0" ]] && [[ -f "${output}" ]]; then
    echo -e "error: output ${output} already exists, use -f to overwrite."
    exit 2
  fi

  # validate commands available
  if ! command -v bcftools &>/dev/null; then
    echo "error: 'bcftools' could not be found"
    exit 2
  fi
  if ! command -v tabix &>/dev/null; then
    echo "error: 'tabix' could not be found"
    exit 2
  fi
  if ! command -v bgzip &>/dev/null; then
    echo "error: 'bgzip' could not be found"
    exit 2
  fi
}

filter() {
  local -r input_fathmm_mkl="${1}"
  local -r input_gnomad="${2}"
  local -r input_ncer="${3}"
  local -r input_phylop="${4}"
  local -r input_remm="${5}"
  local -r input_spliceai="${6}"
  local -r region="${7}"
  local -r output_dir="${8}"
  local -r force="${9}"

  local -r output_fathmm_mkl="${output_dir}/$(basename "${input_fathmm_mkl}")"
  local -r output_gnomad="${output_dir}/$(basename "${input_gnomad}")"
  local -r output_ncer="${output_dir}/$(basename "${input_ncer}")"
  local -r output_phylop="${output_dir}/$(basename "${input_phylop}")"
  local -r output_remm="${output_dir}/$(basename "${input_remm}")"
  local -r output_spliceai="${output_dir}/$(basename "${input_spliceai}")"

  if [[ -d "${output_dir}" ]]; then
    if [[ "${force}" == "1" ]] && [[ -f "${output_fathmm_mkl}" ]]; then
      rm "${output_fathmm_mkl}"
    fi
    if [[ "${force}" == "1" ]] && [[ -f "${output_gnomad}" ]]; then
      rm "${output_gnomad}"
    fi
    if [[ "${force}" == "1" ]] && [[ -f "${output_ncer}" ]]; then
      rm "${output_ncer}"
    fi
    if [[ "${force}" == "1" ]] && [[ -f "${output_phylop}" ]]; then
      rm "${output_phylop}"
    fi
    if [[ "${force}" == "1" ]] && [[ -f "${output_remm}" ]]; then
      rm "${output_remm}"
    fi
    if [[ "${force}" == "1" ]] && [[ -f "${output_spliceai}" ]]; then
      rm "${output_spliceai}"
    fi
  else
    mkdir -p "${output_dir}"
  fi

  # fathmm
  cat <(zcat "${input_fathmm_mkl}" | head -n 1) <(tabix "${input_fathmm_mkl}" "${region}") | bgzip --compress-level 9 > "${output_fathmm_mkl}"
  # gnomad
  cat <(zcat "${input_gnomad}" | head -n 1) <(tabix "${input_gnomad}" "${region}") | bgzip --compress-level 9 > "${output_gnomad}"
  # ncer
  tabix "${input_ncer}" "${region}" | bgzip --compress-level 9 > "${output_ncer}"
  # phylop
  tabix "${input_phylop}" "${region}" | bgzip --compress-level 9 > "${output_phylop}"
  # remm
  cat <(zcat "${input_remm}" | head -n 4) <(tabix "${input_remm}" "${region}") | bgzip --compress-level 9 > "${output_remm}"
  # spliceai
  #TODO rename contigs to correct reference in preprocess_spliceai.sh
  local -r region_spliceai="${region#chr}"
  cat <(bcftools view --header-only --no-version "${input_spliceai}") <(tabix "${input_spliceai}" "${region_spliceai}") | bgzip --compress-level 9 > "${output_spliceai}"
}

main() {
  local args
  args=$(getopt -a -n install -o r:o:fh --long fathmm_mkl:,gnomad:,ncer:,phylop:,remm:,spliceai:,region:,output:,force,help -- "$@")

  local input_fathmm_mkl=""
  local input_gnomad=""
  local input_ncer=""
  local input_phylop=""
  local input_remm=""
  local input_spliceai=""
  local region=""
  local output=""
  local force="0"

  eval set -- "${args}"
  while :; do
    case "$1" in
    --fathmm_mkl)
      input_fathmm_mkl="$2"
      shift 2
      ;;
    --gnomad)
      input_gnomad="$2"
      shift 2
      ;;
    --ncer)
      input_ncer="$2"
      shift 2
      ;;
    --phylop)
      input_phylop="$2"
      shift 2
      ;;
    --remm)
      input_remm="$2"
      shift 2
      ;;
    --spliceai)
      input_spliceai="$2"
      shift 2
      ;;
    -r | --region)
      region="$2"
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

  validate "${input_fathmm_mkl}" "${input_gnomad}" "${input_ncer}" "${input_phylop}" "${input_remm}" "${input_spliceai}" "${region}" "${output}" "${force}"
  filter "${input_fathmm_mkl}" "${input_gnomad}" "${input_ncer}" "${input_phylop}" "${input_remm}" "${input_spliceai}" "${region}" "${output}" "${force}"
}

main "${@}"