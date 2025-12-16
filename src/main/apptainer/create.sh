#!/bin/bash
set -euo pipefail

main() {
  if [[ ! -f "../../../target/vip-annotate" ]]; then
    >&2 echo -e "error: 'vip-annotate' does not exist"
    exit 2
  fi

  ln --force "../../../target/vip-annotate" .

  local args=()
  args+=("--mksquashfs-args" "-comp zstd -Xcompression-level 19")

  sudo apptainer build "${args[@]}" vip-annotate.sif vip-annotate.def

  rm vip-annotate
}

main "${@}"