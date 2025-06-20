package org.molgenis.vipannotate.annotation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

public class VcfRecordAnnotationWriter<T extends Annotation> {
  private final StringBuilder reusableStringBuilder;
  private Map<String, DecimalFormat> decimalFormats;

  public VcfRecordAnnotationWriter() {
    reusableStringBuilder = new StringBuilder();
  }

  public void writeInfoString(
      VcfRecord vcfRecord,
      List<T> annotations,
      String infoId,
      Function<T, String> transformFunction) {
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

  public void writeInfoInteger(
      VcfRecord vcfRecord,
      List<T> annotations,
      String infoId,
      Function<T, Integer> transformFunction) {
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

  public void writeInfoDouble(
      VcfRecord vcfRecord,
      List<T> annotations,
      String infoId,
      Function<T, Double> transformFunction,
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
      decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
      decimalFormat.applyPattern(pattern);
      decimalFormats.put(pattern, decimalFormat);
    }
    return decimalFormat;
  }
}
