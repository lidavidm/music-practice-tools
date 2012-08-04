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
  
  @Test
  public void shouldReturnSameNumberIfAlreadyInRange() {
    float numberInRange = 1.05f;
    float min = 0.7f;
    float max = 1.1f;
    assertThat(Utility.roundToBeInRange(numberInRange, min, max), equalTo(numberInRange));
  }
  
  @Test
  public void shouldReturnMinIfGivenValueLessThanMin() {
    float numberBelowMin = -1.2f;
    float min = -0.5f;
    float max = 0.1f;
    assertThat(Utility.roundToBeInRange(numberBelowMin, min, max), equalTo(min));
  }
  
  @Test
  public void shouldReturnMaxIfGivenValueGreaterThanMax() {
    float numberAboveMax = 1.01f;
    float min = 0f;
    float max = 1.0f;
    assertThat(Utility.roundToBeInRange(numberAboveMax, min, max), equalTo(max));
  }
}
