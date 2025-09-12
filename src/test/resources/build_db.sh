#!/bin/bash
set -euo pipefail

SCRIPT_DIR=$(dirname "$(realpath "$0")")

main() {
  module load Java/21.0.7

  local -r resource_dir="/apps/data/vip/resources/GRCh38"
  local -r resource_external_dir="${SCRIPT_DIR}/resources"

  local -r output_dir="${SCRIPT_DIR}/output"
  rm -rf "${output_dir}"
  mkdir -p "${output_dir}"

  local -r input_fathmm_mkl="${resource_dir}/GRCh38_FATHMM-MKL_NC.tsv.gz"
  local -r input_gnomad="${resource_dir}/gnomad.total.v4.1.sites.stripped-v3.tsv.gz"
  local -r input_ncer="${resource_dir}/GRCh38_ncER_perc.bed.gz"
  local -r input_phylop="${resource_external_dir}/hg38.phyloP100way.bed.gz"
  local -r input_remm="${resource_dir}/ReMM.v0.4.hg38.tsv.gz"
  local -r input_spliceai_indel="${resource_dir}/spliceai_scores.masked.indel.hg38.vcf.gz"
  local -r input_spliceai_snv="${resource_dir}/spliceai_scores.masked.snv.hg38.vcf.gz"

  local -r output_fathmm_mkl="${output_dir}/fathmm_mkl.zip"
  local -r output_gnomad="${output_dir}/gnomad.zip"
  local -r output_ncer="${output_dir}/ncer.zip"
  local -r output_phylop="${output_dir}/phylop.zip"
  local -r output_remm="${output_dir}/remm.zip"
  local -r output_spliceai_indel="${output_dir}/spliceai_indel.zip"
  local -r output_spliceai_snv="${output_dir}/spliceai_snv.zip"

  local -r ncbi_gene_index="${resource_external_dir}/ncbi_gene.tsv"
  local -r reference_index="${resource_dir}/GCA_000001405.15_GRCh38_no_alt_analysis_set.fna.fai"

  local args=()
  args+=("-Xms2g" "-Xmx2g" "-XX:+UseSerialGC")
  args+=("-jar" "${SCRIPT_DIR}/vip-annotate-0.0.1-SNAPSHOT-jar-with-dependencies.jar")

echo   java "${args[@]}" fathmm_mkl --input "${input_fathmm_mkl}" --reference-index "${reference_index}" --output "${output_fathmm_mkl}"
echo   java "${args[@]}" gnomad --input "${input_gnomad}" --reference-index "${reference_index}" --output "${output_gnomad}"
echo   java "${args[@]}" ncer --input "${input_ncer}" --reference-index "${reference_index}" --output "${output_ncer}"
echo   java "${args[@]}" phylop --input "${input_phylop}" --reference-index "${reference_index}" --output "${output_phylop}"
echo   java "${args[@]}" remm --input "${input_remm}" --reference-index "${reference_index}" --output "${output_remm}"
echo   java "${args[@]}" spliceai --input "${input_spliceai_indel}" --reference-index "${reference_index}" --output "${output_spliceai_indel}" --ncbi-gene-index "${ncbi_gene_index}"
echo   java "${args[@]}" spliceai --input "${input_spliceai_snv}" --reference-index "${reference_index}" --output "${output_spliceai_snv}" --ncbi-gene-index "${ncbi_gene_index}"
}

main "${@}"
