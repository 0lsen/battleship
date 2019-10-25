package de.olsen.battleship.service;

import de.olsen.battleship.model.HitMap;
import de.olsen.battleship.model.Orientation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SpaceCalculatorImplTest {

  private final int MAX_LENGTH = 5;

  private SpaceCalculator calculator;

  private HitMap hitMap;
  private boolean[][] freeMap;

  @Before
  public void setUp() {
    calculator = new SpaceCalculatorImpl();
  }

  @Test
  public void all() {
    buildHitMap();
    assertFreeMap();
    assertSpaceCalcSamples();
  }

  /**
   * HitMap and Sample Points
   *
   *   0  1  2  3  4  5  6  7  8  9
   * |------------------------------|
   * | 1                            | 0
   * |                   X  X       | 1
   * |                      4       | 2
   * |                              | 3
   * |                2  3          | 4
   * |       X                      | 5
   * |    X  8  X        X          | 6
   * |       X                      | 7
   * |             X     7          | 8
   * |             5        X  6    | 9
   * |------------------------------|
   */
  private void buildHitMap() {
    hitMap = new HitMap();
    hitMap.placeHit(6, 1,false);
    hitMap.placeHit(7, 1,false);
    hitMap.placeHit(6, 6,false);
    hitMap.placeHit(4, 8,false);
    hitMap.placeHit(7, 9,false);
    hitMap.placeHit(2, 5,false);
    hitMap.placeHit(2, 7,false);
    hitMap.placeHit(1, 6,false);
    hitMap.placeHit(3, 6,false);
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