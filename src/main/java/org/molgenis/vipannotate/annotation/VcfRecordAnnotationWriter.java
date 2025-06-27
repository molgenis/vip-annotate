package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

public class VcfRecordAnnotationWriter {
  private final StringBuilder reusableStringBuilder;
  @Nullable private Map<String, DecimalFormat> decimalFormats;

  public VcfRecordAnnotationWriter() {
    reusableStringBuilder = new StringBuilder();
  }

  public <T extends Annotation> void writeInfoString(
      VcfRecord vcfRecord,
      List<@Nullable T> annotations,
      String infoId,
      Function<T, @Nullable String> transformFunction) {
    reusableStringBuilder.setLength(0);

    boolean hasAnnotation = false;
    for (int i = 0, annotationsSize = annotations.size(); i < annotationsSize; i++) {
      if (i > 0) {
        reusableStringBuilder.append(',');
      }

      T annotation = annotations.get(i);
      if (annotation != null) {
        String strValue = transformFunction.apply(annotation);
        if (strValue != null) {
          reusableStringBuilder.append(strValue);
          hasAnnotation = true;
        } else {
          reusableStringBuilder.append('.');
        }
      } else {
        reusableStringBuilder.append('.');
      }
    }

    if (hasAnnotation) {
      vcfRecord.info().put(infoId, reusableStringBuilder.toString());
    } else {
      vcfRecord.info().remove(infoId);
    }
  }

  public <T extends Annotation> void writeInfoInteger(
      VcfRecord vcfRecord,
      List<@Nullable T> annotations,
      String infoId,
      Function<T, @Nullable Integer> transformFunction) {
    reusableStringBuilder.setLength(0);

    boolean hasAnnotation = false;
    for (int i = 0, annotationsSize = annotations.size(); i < annotationsSize; i++) {
      if (i > 0) {
        reusableStringBuilder.append(',');
      }

      T annotation = annotations.get(i);
      if (annotation != null) {
        Integer integerValue = transformFunction.apply(annotation);
        if (integerValue != null) {
          reusableStringBuilder.append(integerValue);
          hasAnnotation = true;
        } else {
          reusableStringBuilder.append('.');
        }
      } else {
        reusableStringBuilder.append('.');
      }
    }

    if (hasAnnotation) {
      vcfRecord.info().put(infoId, reusableStringBuilder.toString());
    } else {
      vcfRecord.info().remove(infoId);
    }
  }

  public <T extends Annotation> void writeInfoDouble(
      VcfRecord vcfRecord,
      List<@Nullable T> annotations,
      String infoId,
      Function<T, @Nullable Double> transformFunction,
      String pattern) {
    reusableStringBuilder.setLength(0);

    DecimalFormat decimalFormat = null;
    boolean hasAnnotation = false;
    for (int i = 0, annotationsSize = annotations.size(); i < annotationsSize; i++) {
      if (i > 0) {
        reusableStringBuilder.append(',');
      }

      T annotation = annotations.get(i);
      if (annotation != null) {
        Double doubleValue = transformFunction.apply(annotation);
        if (doubleValue != null) {
          if (decimalFormat == null) {
            decimalFormat = getDecimalFormat(pattern); // lazy init
          }
          reusableStringBuilder.append(decimalFormat.format(doubleValue));
          hasAnnotation = true;
        } else {
          reusableStringBuilder.append('.');
        }
      } else {
        reusableStringBuilder.append('.');
      }
    }

    if (hasAnnotation) {
      vcfRecord.info().put(infoId, reusableStringBuilder.toString());
    } else {
      vcfRecord.info().remove(infoId);
    }
  }

  private DecimalFormat getDecimalFormat(String pattern) {
    if (decimalFormats == null) {
      decimalFormats = new HashMap<>(); // lazy init
    }

    DecimalFormat decimalFormat = decimalFormats.get(pattern);
    if (decimalFormat == null) {
      decimalFormat = requireNonNull((DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT));
      decimalFormat.applyPattern(pattern);
      decimalFormats.put(pattern, decimalFormat);
    }
    return decimalFormat;
  }
}
