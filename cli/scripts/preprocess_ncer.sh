#!/bin/bash
set -euo pipefail

postprocess() {
  local -r output_dir="$1"
  local -r input_dir="$2"

  echo -e "postprocessing ncer data..."
  local -r output_file="${output_dir}/ncER_perc_coordSorted_grch38.bed.gz"
  if [ ! -f "${output_file}" ]; then
    mkdir -p "${output_dir}"

    module purge
    module load HTSlib/1.22.1-GCCcore-13.3.0

    # streaming
    # 0. decompress
    # 1. sort (order can still differ from reference order)
    # 2. dedup (for dups keep first record with max score)
    # 3. compress
    gunzip --stdout "${input_dir}/ncER_perc_coordSorted_grch38.bed.gz" | \
    LC_ALL=C sort --buffer-size=60G --parallel=2 --temporary-directory=. --key=1,1V --key=2,2n --key=3,3n --key=4,4nr | \
    awk 'BEGIN { FS=OFS="\t" } { key=$1 FS $2 FS $3; if( key!=prev_key ){ print; prev_key=key } }' | \
    bgzip --threads 2 --compress-level 9 > "${output_file}"

    # index
    tabix --preset bed --csi "${output_file}"

    module purge
  fi
  echo -e "postprocessing ncer data done"
}

liftover () {
  local -r output_dir="${1}"
  local -r preprocessed_dir="${2}"
  local -r rawdata_dir="${3}"

  echo -e "liftover..."
  local -r output_file="${output_dir}/ncER_perc_coordSorted_grch38.bed"
  local -r output_unmapped_file="${output_dir}/ncER_perc_coordSorted_grch38_unmapped.bed"
  if [ ! -f "${output_file}.gz" ]; then
    mkdir -p "${output_dir}"

    module purge
    module load HTSlib/1.22.1-GCCcore-13.3.0

    "${rawdata_dir}/grch37/liftOver" "${preprocessed_dir}/ncer/ncER_perc_coordSorted.txt.gz" "${rawdata_dir}/grch37/hg19ToHg38.over.chain.gz" "${output_file}" "${output_unmapped_file}"
    bgzip --threads 2 --compress-level 9 "${output_file}"
    bgzip --threads 2 --compress-level 9 "${output_unmapped_file}"

    module purge
  fi
  echo -e "liftover done"
}

preprocess_ncer() {
  local -r output_dir="$1"; shift

  echo -e "preprocessing ncer data..."
  mkdir -p "${output_dir}"

  local -r output_concat_file="${output_dir}/ncER_perc_coordSorted.txt.gz"
  if [ ! -f "${output_concat_file}" ]; then
    cat "$@" > "${output_concat_file}"
  fi
  echo -e "preprocessing ncer data done"
}

preprocess_grch37() {
  local -r output_dir="${1}"
  local -r input_dir="${2}"

  echo -e "preprocessing grch37..."
  mkdir -p "${output_dir}"
  local -r fasta_gz="${input_dir}/human_g1k_v37.fasta.gz"
  local -r fasta="${output_dir}/$(basename "${fasta_gz}" .gz)"

  # decompress
  if [ ! -f "${fasta}" ]; then
    gunzip -c "${fasta_gz}" > "${fasta}"
  fi

  # index
  if [ ! -f "${fasta}.fai" ]; then
    module purge
    module load SAMtools/1.21-GCC-13.3.0
    samtools faidx "${fasta}"
    module purge
  fi

  # exe
  chmod +x "${input_dir}/liftOver"

  echo -e "preprocessing grch37 done"
}

preprocess() {
  local -r output_dir="${1}"
  local -r input_dir="${2}"

  mkdir -p "${output_dir}"
  preprocess_grch37 "${output_dir}/grch37" "${input_dir}/grch37"
  preprocess_ncer "${output_dir}/ncer" $(ls -v "${input_dir}"/ncer/ncER_perc_*_coordSorted.txt.gz)
}

download_grch37() {
  local -r output_dir="${1}"

  echo -e "downloading grch37 data..."
  mkdir -p "${output_dir}"

  # fasta
  local -r fasta_gz="${output_dir}/human_g1k_v37.fasta.gz"
  if [ ! -f "${fasta_gz}" ]; then
    wget --quiet --continue --directory-prefix="${output_dir}" "ftp://ftp-trace.ncbi.nih.gov/1000genomes/ftp/technical/reference/human_g1k_v37.fasta.gz"
  fi

  # liftover
  local -r liftover="${output_dir}/liftOver"
  if [ ! -f "${liftover}" ]; then
    wget --quiet --continue --directory-prefix="${output_dir}" "http://hgdownload.soe.ucsc.edu/admin/exe/linux.x86_64/liftOver"
  fi

  # chain
  local -r chain="${output_dir}/hg19ToHg38.over.chain.gz"
  if [ ! -f "${chain}" ]; then
    wget --quiet --continue --directory-prefix="${output_dir}" "http://hgdownload.cse.ucsc.edu/goldenPath/hg19/liftOver/hg19ToHg38.over.chain.gz"
  fi
  echo -e "downloading grch37 data done"
}

download_ncer() {
  local -r output_dir="${1}"

  local -r chromosomes=( {1..22} X )
  local -r ncer_base_url="https://telentilab-dataset.s3.amazonaws.com/ncER/v2/Bin_1bp"

  echo -e "downloading ncer data..."
  mkdir -p "${output_dir}"
  for chromosome in "${chromosomes[@]}"; do
    local filename="ncER_perc_chr${chromosome}_coordSorted.txt.gz"
    if [ ! -f "${output_dir}/${filename}" ]; then
      wget --quiet --continue --directory-prefix="${output_dir}" "${ncer_base_url}/${filename}"
    fi
  done
  echo -e "downloading ncer data done"
}

download() {
  local -r output_dir="${1}"

  download_grch37 "${output_dir}/grch37"
  download_ncer "${output_dir}/ncer"
}

main() {
  download "${PWD}/rawdata"
  preprocess "${PWD}/preprocessed" "${PWD}/rawdata"
  liftover "${PWD}/liftover" "${PWD}/preprocessed" "${PWD}/rawdata"
  postprocess "${PWD}/output" "${PWD}/liftover"
}

main "$@"
