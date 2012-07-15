package com.proch.practicehub.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.proch.practicehub.Utility;


public class UtilityTest {

  @Test
  public void shouldConvertIntArrayToShortArrayWithSameValues() {
    int[] int_array = { 1, 2, 3, 4, -32768, 32767 };
    short[] expected_result = { 1, 2, 3, 4, -32768, 32767 };
    short[] actual_result = Utility.intToShortArray(int_array);
    
    assertThat(actual_result, equalTo(expected_result));
  }
}
