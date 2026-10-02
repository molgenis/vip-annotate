package org.molgenis.vipannotate.annotation.resolved;

import java.util.Map;
import java.util.function.BiConsumer;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record ResolvedAnnotationSpecs(Map<String, ResolvedAnnotationSpec> annotationSpecMap) {
  public boolean isEmpty() {
    return annotationSpecMap.isEmpty();
  }

  public int size() {
    return annotationSpecMap.size();
  }

  public void forEach(BiConsumer<? super String, ? super ResolvedAnnotationSpec> action) {
    annotationSpecMap.forEach(action);
  }

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeMap(
        annotationSpecMap, BinaryWriter::writeString, ResolvedAnnotationSpec::writeTo);
  }

  public static ResolvedAnnotationSpecs readFrom(BinaryReader binaryReader) {
    Map<String, ResolvedAnnotationSpec> annotationSpecMap =
        binaryReader.readMap(BinaryReader::readString, ResolvedAnnotationSpec::readFrom);
    return new ResolvedAnnotationSpecs(annotationSpecMap);
  }
}
