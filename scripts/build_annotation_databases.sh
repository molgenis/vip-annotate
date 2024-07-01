#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"

usage() {
  echo -e "usage: ${SCRIPT_NAME} [arguments]
  -s, --sif              FILE  Input 'vip-annotate-db.sif' path                                               (required)
      --fathmm_mkl       FILE  Input 'GRCh38_FATHMM-MKL_NC.tsv.gz' path                                       (required)
      --gnomad           FILE  Input 'gnomad.total.v4.1.sites.stripped-v3.tsv.gz' path                        (required)
      --ncer             FILE  Input 'GRCh38_ncER_perc.bed.gz' path                                           (required)
      --phylop           FILE  Input 'hg38.phyloP100way.bed.gz' path, see 'preprocess_phylop.sh'              (required)
      --remm             FILE  Input 'ReMM.v0.4.hg38.tsv.gz' path                                             (required)
      --spliceai         FILE  Input 'spliceai_scores.masked.hg38.vcf.gz' path, see 'preprocess_spliceai.sh'  (required)
      --ncbi_gene_index  FILE  Input 'ncbi_gene.tsv' path                                                     (required)
      --reference_index  FILE  Input 'GCA_000001405.15_GRCh38_no_alt_analysis_set.fna.fai' path               (required)
  -o, --output           DIR   Output directory                                                               (required)
  -f, --force                  Override output files if they already exists.                                  (optional)
  -h, --help                   Print this message and exit

  requirements: 'apptainer' available on path"
}

validate() {
  local -r sif="${1}"
  local -r input_fathmm_mkl="${2}"
  local -r input_gnomad="${3}"
  local -r input_ncer="${4}"
  local -r input_phylop="${5}"
  local -r input_remm="${6}"
  local -r input_spliceai="${7}"
  local -r ncbi_gene_index="${8}"
  local -r reference_index="${9}"
  local -r output="${10}"
  local -r force="${11}"

  # validate required args
  if [[ -z "${sif}" ]]; then
    >&2 echo -e "error: missing required -s / --sif"
    usage
    exit 2
  fi
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
  if [[ -z "${ncbi_gene_index}" ]]; then
    >&2 echo -e "error: missing required --ncbi_gene_index"
    usage
    exit 2
  fi
  if [[ -z "${reference_index}" ]]; then
    >&2 echo -e "error: missing required --reference_index"
    usage
    exit 2
  fi
  if [[ -z "${output}" ]]; then
    >&2 echo -e "error: missing required -o / --output"
    usage
    exit 2
  fi

  # validate files exist
  if [[ ! -f "${sif}" ]]; then
    >&2 echo -e "error: '${sif}' does not exist"
    exit 2
  fi
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
  if [[ ! -f "${ncbi_gene_index}" ]]; then
    >&2 echo -e "error: '${ncbi_gene_index}' does not exist"
    exit 2
  fi
  if [[ ! -f "${reference_index}" ]]; then
    >&2 echo -e "error: '${reference_index}' does not exist"
    exit 2
  fi

  # validate apptainer available
  if ! command -v apptainer &>/dev/null; then
    echo "error: 'apptainer' could not be found"
    exit 2
  fi
}

build() {
  local -r sif="${1}"
  local -r input_fathmm_mkl="${2}"
  local -r input_gnomad="${3}"
  local -r input_ncer="${4}"
  local -r input_phylop="${5}"
  local -r input_remm="${6}"
  local -r input_spliceai="${7}"
  local -r ncbi_gene_index="${8}"
  local -r reference_index="${9}"
  local -r output_dir="${10}"
  local -r force="${11}"

  local -r output_fathmm_mkl="${output_dir}/fathmmmkl.zip"
  local -r output_gnomad="${output_dir}/gnomad.zip"
  local -r output_ncer="${output_dir}/ncer.zip"
  local -r output_phylop="${output_dir}/phylop.zip"
  local -r output_remm="${output_dir}/remm.zip"
  local -r output_spliceai="${output_dir}/spliceai.zip"

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

  apptainer exec --no-mount home "${sif}" /vip-annotate-db --debug fathmm_mkl --input "${input_fathmm_mkl}" --reference-index "${reference_index}" --output "${output_fathmm_mkl}"
  apptainer exec --no-mount home "${sif}" /vip-annotate-db --debug gnomad --input "${input_gnomad}" --reference-index "${reference_index}" --output "${output_gnomad}"
  apptainer exec --no-mount home "${sif}" /vip-annotate-db --debug ncer --input "${input_ncer}" --reference-index "${reference_index}" --output "${output_ncer}"
  apptainer exec --no-mount home "${sif}" /vip-annotate-db --debug phylop --input "${input_phylop}" --reference-index "${reference_index}" --output "${output_phylop}"
  apptainer exec --no-mount home "${sif}" /vip-annotate-db --debug remm --input "${input_remm}" --reference-index "${reference_index}" --output "${output_remm}"
  apptainer exec --no-mount home "${sif}" /vip-annotate-db --debug spliceai --input "${input_spliceai}" --reference-index "${reference_index}" --ncbi-gene-index "${ncbi_gene_index}" --output "${output_spliceai}"
}

main() {
  local args
  args=$(getopt -a -n install -o s:o:fh --long sif:,fathmm_mkl:,gnomad:,ncer:,phylop:,remm:,spliceai:,ncbi_gene_index:,reference_index:,output:,force,help -- "$@")

  local sif=""
  local input_fathmm_mkl=""
  local input_gnomad=""
  local input_ncer=""
  local input_phylop=""
  local input_remm=""
  local input_spliceai=""
  local ncbi_gene_index=""
  local reference_index=""
  local output=""
  local force="0"

  eval set -- "${args}"
  while :; do
    case "$1" in
    -s | --sif)
      sif="$2"
      shift 2
      ;;
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
    --ncbi_gene_index)
      ncbi_gene_index="$2"
      shift 2
      ;;
    --reference_index)
      reference_index="$2"
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

  validate "${sif}" "${input_fathmm_mkl}" "${input_gnomad}" "${input_ncer}" "${input_phylop}" "${input_remm}" "${input_spliceai}" "${ncbi_gene_index}" "${reference_index}" "${output}" "${force}"

  build "${sif}" "${input_fathmm_mkl}" "${input_gnomad}" "${input_ncer}" "${input_phylop}" "${input_remm}" "${input_spliceai}" "${ncbi_gene_index}" "${reference_index}" "${output}" "${force}"
}

main "${@}"