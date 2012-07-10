package com.proch.practicehub.test;

import junit.framework.TestCase;

import com.proch.practicehub.Utility;

public class UtilityTest extends TestCase {

  public void testIntToShortArray() {
    int[] int_array = { 1, 2, 3, 4, -32768, 32767 };
    short[] actual_result = Utility.intToShortArray(int_array);

    short[] expected_result = { 1, 2, 3, 4, -32768, 32767 };

    assertEquals(actual_result.length, expected_result.length);
    for (int i = 0; i < actual_result.length; i++) {
      assertEquals(expected_result[i], actual_result[i]);
    }

  }
}
