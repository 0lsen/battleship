package de.olsen.battleship.service;

import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import de.olsen.battleship.model.HeatMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HeatMapperImplTest extends HitMapBasedTest {

  private final int MIN_LENGTH = 3;

  private HeatMapperImpl mapper;

  private HeatMap heatMap;

  @Before
  public void setUp() {
    super.setUp();
    mapper = new HeatMapperImpl();
    mapper.setSpaceCalculator(new SpaceCalculatorImpl());
  }

  @Test
  public void all() {
    buildHeatMap();
    assertSamples();
  }

  private void buildHeatMap() {
    try {
      heatMap = mapper.build(hitMap, MIN_LENGTH, MAX_LENGTH);
    } catch (ThisShouldNeverHappenException e) {
      fail();
    }
  }

  private void assertSamples() {
    // 1
    assertEquals(50, getHeat(0, 0, null));

    // 2
    assertEquals(162, getHeat(5, 4, null));

    // 3
    assertEquals(80, getHeat(6, 4, null));

    // 4
    assertEquals(74, getHeat(7, 2, null));

    // 5
    assertEquals(49, getHeat(4, 9, null));

    // 6
    assertEquals(25, getHeat(8, 9, null));

    // 7
    assertEquals(34, getHeat(6, 8, null));

    // 8
    assertEquals(0, getHeat(2, 6, ThisShouldNeverHappenException.class));
  }

  private int getHeat(int x, int y, Class<? extends Exception> expectedException) {
    int heat = 0;
    try {
      heat = heatMap.getHeat().get(heatMap.findIndex(x, y));
      if (expectedException != null) fail();
    } catch (Exception e) {
      if (e.getClass() != expectedException) fail();
    }
    return heat;
  }
}