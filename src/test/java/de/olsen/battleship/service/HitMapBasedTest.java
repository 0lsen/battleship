package de.olsen.battleship.service;

import de.olsen.battleship.model.HitMap;

public abstract class HitMapBasedTest {

  protected final int MAX_LENGTH = 5;

  protected HitMap hitMap;

  public void setUp() {
    buildHitMap();
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
  protected void buildHitMap() {
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

}
