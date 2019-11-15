package de.olsen.battleship.service;

import de.olsen.battleship.model.Orientation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SpaceCalculatorImplTest extends HitMapBasedTest {

  private SpaceCalculator calculator;

  private boolean[][] freeMap;

  @Before
  public void setUp() {
    super.setUp();
    calculator = new SpaceCalculatorImpl();
  }

  @Test
  public void all() {
    buildHitMap();
    assertFreeMap();
    assertSpaceCalcSamples();
  }

  private void assertFreeMap() {
    freeMap = calculator.buildFreeMap(hitMap);
    assertEquals(new boolean[][]{
        {true, true, true, true, true, true, true, true, true, true},
        {true, true, true, true, true, true, false, true, true, true},
        {true, true, true, true, true, false, true, false, true, true},
        {true, true, true, true, true, true, false, true, true, true},
        {true, true, true, true, true, true, true, true, false, true},
        {true, true, true, true, true, true, true, true, true, true},
        {true, false, true, true, true, true, false, true, true, true},
        {true, false, true, true, true, true, true, true, true, false},
        {true, true, true, true, true, true, true, true, true, true},
        {true, true, true, true, true, true, true, true, true, true},
    }, freeMap);
  }

  private void assertSpaceCalcSamples() {
    // 1
    assertArrayEquals(new int[]{5,5}, calcSpace(0,0));

    // 2
    assertArrayEquals(new int[]{9,9}, calcSpace(5,4));

    // 3
    assertArrayEquals(new int[]{8,4}, calcSpace(6,4));

    // 4
    assertArrayEquals(new int[]{7,5}, calcSpace(7,2 ));

    // 5
    assertArrayEquals(new int[]{7,1}, calcSpace(4,9));

    // 6
    assertArrayEquals(new int[]{2,5}, calcSpace(8,9));

    // 7
    assertArrayEquals(new int[]{5,3}, calcSpace(6,8));

    // 8
    assertArrayEquals(new int[]{1,1}, calcSpace(2,6));
  }

  private int[] calcSpace(int x, int y) {
    int horizontal = calculator.calculateSpace(x, y, Orientation.HORIZONTAL, MAX_LENGTH, freeMap);
    int vertical = calculator.calculateSpace(x, y, Orientation.VERTICAL, MAX_LENGTH, freeMap);
    return new int[]{horizontal, vertical};
  }
}