package de.olsen.battleship.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HitMapTest {

  private HitMap map;

  @Before
  public void setUp() {
    map = new HitMap();
  }

  @Test
  public void hits() {
    assertFalse(map.alreadyHit(1, 1));

    map.placeHit(1, 1, true);
    map.placeHit(1,2, true);
    map.placeHit(2, 1, true);

    assertTrue(map.alreadyHit(1, 1));
    assertTrue(map.alreadyHit(1, 2));
    assertTrue(map.alreadyHit(2, 1));
    assertFalse(map.alreadyHit(2, 2));
    assertFalse(map.alreadyHit(3, 3));
  }
}