package org.molgenis.vipannotate.annotator.spliceai;

import java.util.List;

public class Utils {
  public static float[][][] mean(List<float[][][]> arrays) {
    int nrArrays = arrays.size();

    float[][][] firstArray = arrays.getFirst();
    int xLength = firstArray.length;
    int yLength = firstArray[0].length;
    int zLength = firstArray[0][0].length;

    float[][][] mean = new float[xLength][yLength][zLength];

    for (int xIndex = 0; xIndex < xLength; xIndex++) {
      for (int yIndex = 0; yIndex < yLength; yIndex++) {
        for (int zIndex = 0; zIndex < zLength; zIndex++) {
          float sum = 0;
          for (float[][][] prediction : arrays) {
            sum += prediction[xIndex][yIndex][zIndex];
          }
          mean[xIndex][yIndex][zIndex] = sum / nrArrays;
        }
      }
    }

    return mean;
  }

  public static int argmax(float[][] arrayThis, float[][] arrayThat, int zIndex) {
    float maxDiff = Float.MIN_VALUE;
    int indexMaxDiff = -1;
    for (int i = 0; i < arrayThis.length; i++) {
      float diff = arrayThis[i][zIndex] - arrayThat[i][zIndex];
      if (diff > maxDiff) {
        indexMaxDiff = i;
        maxDiff = diff;
      }
    }
    return indexMaxDiff;
  }

  public static void reverse(float[][] arr) {
    for (int i = 0; i < arr.length / 2; i++) {
      float[] t = arr[i];
      arr[i] = arr[arr.length - 1 - i];
      arr[arr.length - 1 - i] = t;
    }
  }
}
