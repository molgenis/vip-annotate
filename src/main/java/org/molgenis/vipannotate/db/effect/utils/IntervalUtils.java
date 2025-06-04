package org.molgenis.vipannotate.db.effect.utils;

import java.util.*;
import lombok.Getter;

// A Java program for merging overlapping intervals
// source based on https://www.geeksforgeeks.org/merging-intervals/
public class IntervalUtils {

  // The main function that takes a set of intervals,
  // merges overlapping intervals and prints the result
  public static List<MutableInterval> mergeIntervals(MutableInterval arr[]) {
    // Test if the given set has at least one interval
    if (arr.length <= 0) return Collections.emptyList();

    // Create an empty stack of intervals
    Stack<MutableInterval> stack = new Stack<>();

    // sort the intervals in increasing order of start
    // time
    Arrays.sort(
        arr,
        new Comparator<MutableInterval>() {
          public int compare(MutableInterval i1, MutableInterval i2) {
            return i1.start - i2.start;
          }
        });

    // push the first interval to stack
    stack.push(arr[0]);

    // Start from the next interval and merge if
    // necessary
    for (int i = 1; i < arr.length; i++) {
      // get interval from stack top
      MutableInterval top = stack.peek();

      // if current interval is not overlapping with
      // stack top, push it to the stack
      if (top.end < arr[i].start) stack.push(arr[i]);

      // Otherwise update the ending time of top if
      // ending of current interval is more
      else if (top.end < arr[i].end) {
        top.end = arr[i].end;
        stack.pop();
        stack.push(top);
      }
    }

    return stack.stream().toList();
  }

  @Getter
  public static class MutableInterval {
    int start, end;

    public MutableInterval(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }
}

// This code is contributed by Gaurav Tiwari
