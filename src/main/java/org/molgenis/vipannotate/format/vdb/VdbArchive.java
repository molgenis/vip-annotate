package org.molgenis.vipannotate.format.vdb;

public final class VdbArchive {
  /** "VPK1" little-endian */
  static final int VDB_SIGNATURE = 0x31504B56;

  /**
   * archive data is aligned to 4096 to allow for {@link
   * com.sun.nio.file.ExtendedOpenOption#DIRECT}.
   */
  static final int VDB_BYTE_ALIGNMENT = 4096;

  private VdbArchive() {}
}
