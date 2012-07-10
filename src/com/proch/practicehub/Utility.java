/**
 * Contains utility methods that are not specific to a part of the application or this application
 * at all.
 */
package com.proch.practicehub;

public class Utility {
  /**
   * Converts an array of ints to an array of shorts. Assumes int_array contains only ints that
   * are within the range of a short [-32768, 32767]
   * 
   * @param int_array Array of ints to be converted
   * @return Array of shorts with same values as int_array
   */
  public static short[] intToShortArray(int[] int_array) {
    short[] result = new short[int_array.length];
    for (int i = 0; i < int_array.length; i++) {
      result[i] = (short) int_array[i];
    }
    return result;
  }
}
