#!/bin/bash
set -euo pipefail

main() {
  # validate apptainer available
  if ! command -v apptainer &>/dev/null; then
    echo "error: 'apptainer' could not be found"
    exit 2
  fi

  # build containers
  local args=()
  args+=("--mksquashfs-args" "-comp zstd -Xcompression-level 19")

  sudo apptainer build "${args[@]}" ubuntu-24.04.sif ubuntu-24.04.def
  sudo apptainer build "${args[@]}" streamvbyte-2.0.0.sif streamvbyte-2.0.0.def
  sudo apptainer build "${args[@]}" zstd-1.5.7.sif zstd-1.5.7.def
  sudo apptainer build "${args[@]}" native-image-builder.sif native-image-builder.def
}

main "${@}"