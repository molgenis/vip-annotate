#!/bin/bash
set -euo pipefail

main() {
  if [[ ! -f "../../../target/vip-annotate" ]]; then
    >&2 echo -e "error: 'vip-annotate' does not exist"
    exit 2
  fi
  if [[ ! -f "../../../target/vip-annotate-db" ]]; then
    >&2 echo -e "error: 'vip-annotate-db' does not exist"
    exit 2
  fi

  ln "../../../target/vip-annotate" .
  ln "../../../target/vip-annotate-db" .

  local args=()
  args+=("--mksquashfs-args" "-comp zstd -Xcompression-level 19")

  sudo apptainer build "${args[@]}" vip-annotate.sif vip-annotate.def
  sudo apptainer build "${args[@]}" vip-annotate-db.sif vip-annotate-db.def

  rm vip-annotate
  rm vip-annotate-db
}

main "${@}"