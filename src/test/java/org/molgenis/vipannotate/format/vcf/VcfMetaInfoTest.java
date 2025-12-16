package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class VcfMetaInfoTest {

  @Test
  void addOrUpdateInfoAdd() {
    VcfMetaInfo vcfMetaInfo = new VcfMetaInfo();
    vcfMetaInfo.addOrUpdateInfo("ncER", "A", "Float", "ncER score", null, null);
    assertEquals(
        List.of(
            new VcfMetaInfo.Structured(
                "INFO",
                Map.of(
                    "ID",
                    "ncER",
                    "NUMBER",
                    "A",
                    "TYPE",
                    "Float",
                    "DESCRIPTION",
                    "\"ncER score\""))),
        vcfMetaInfo.lines());
  }

  @Test
  void addOrUpdateInfoAddWithSourceAndVersion() {
    VcfMetaInfo vcfMetaInfo = new VcfMetaInfo();
    vcfMetaInfo.addOrUpdateInfo("ncER", "A", "Float", "ncER score", "vip-annotate", "0.0.0-dev");
    assertEquals(
        List.of(
            new VcfMetaInfo.Structured(
                "INFO",
                Map.of(
                    "ID",
                    "ncER",
                    "NUMBER",
                    "A",
                    "TYPE",
                    "Float",
                    "DESCRIPTION",
                    "\"ncER score\"",
                    "SOURCE",
                    "\"vip-annotate\"",
                    "VERSION",
                    "\"0.0.0-dev\""))),
        vcfMetaInfo.lines());
  }

  @Test
  void addOrUpdateInfoUpdate() {
    List<VcfMetaInfo.Line> vcfMetaInfoLines = new ArrayList<>();
    VcfMetaInfo.Unstructured unstructured = new VcfMetaInfo.Unstructured("key", "value");
    VcfMetaInfo.Structured structuredDifferentKey =
        new VcfMetaInfo.Structured(
            "FORMAT",
            Map.of("ID", "ncER", "NUMBER", "A", "TYPE", "Float", "DESCRIPTION", "\"ncER score\""));
    VcfMetaInfo.Structured structuredDifferentId =
        new VcfMetaInfo.Structured(
            "INFO",
            Map.of(
                "ID", "notNcER", "NUMBER", "A", "TYPE", "Float", "DESCRIPTION", "\"ncER score\""));
    vcfMetaInfoLines.add(unstructured);
    vcfMetaInfoLines.add(structuredDifferentKey);
    vcfMetaInfoLines.add(structuredDifferentId);
    vcfMetaInfoLines.add(
        new VcfMetaInfo.Structured(
            "INFO",
            Map.of("ID", "ncER", "NUMBER", "A", "TYPE", "Float", "DESCRIPTION", "\"ncER score\"")));

    VcfMetaInfo vcfMetaInfo = new VcfMetaInfo(vcfMetaInfoLines);
    vcfMetaInfo.addOrUpdateInfo("ncER", "R", "Integer", "ncER score updated", null, null);

    assertEquals(
        List.of(
            unstructured,
            structuredDifferentKey,
            structuredDifferentId,
            new VcfMetaInfo.Structured(
                "INFO",
                Map.of(
                    "ID",
                    "ncER",
                    "NUMBER",
                    "R",
                    "TYPE",
                    "Integer",
                    "DESCRIPTION",
                    "\"ncER score updated\""))),
        vcfMetaInfo.lines());
  }

  @Test
  void addOrUpdateInfoUpdateSourceMismatch() {
    List<VcfMetaInfo.Line> vcfMetaInfoLines = new ArrayList<>();
    vcfMetaInfoLines.add(
        new VcfMetaInfo.Structured(
            "INFO",
            Map.of(
                "ID",
                "gnomAD",
                "NUMBER",
                "A",
                "TYPE",
                "Float",
                "DESCRIPTION",
                "\"gnomAD frequency\"")));

    VcfMetaInfo vcfMetaInfo = new VcfMetaInfo(vcfMetaInfoLines);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            vcfMetaInfo.addOrUpdateInfo(
                "gnomAD", "A", "Float", "gnomAD frequency", "\"vip-annotate\"", null));
  }

  @Test
  void write() {
    List<VcfMetaInfo.Line> vcfMetaInfoLines = new ArrayList<>();
    vcfMetaInfoLines.add(new VcfMetaInfo.Unstructured("key0", "value0"));
    vcfMetaInfoLines.add(new VcfMetaInfo.Unstructured("key1", "value1"));
    VcfMetaInfo vcfMetaInfo = new VcfMetaInfo(vcfMetaInfoLines);
    StringWriter stringWriter = new StringWriter();
    vcfMetaInfo.write(stringWriter);
    assertEquals("##key0=value0\n##key1=value1\n", stringWriter.toString());
  }
}
