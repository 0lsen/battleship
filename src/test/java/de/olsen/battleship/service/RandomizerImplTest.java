package de.olsen.battleship.service;

import de.olsen.battleship.model.Orientation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomizerImplTest {

  Randomizer randomizer;

  @Before
  public void setUp() {
    randomizer = new RandomizerImpl();
  }

  @Test
  public void randomEnumDeliversCorrectEnumType() {
    assertEquals(Orientation.class, randomizer.randomEnum(Orientation.class).getClass());
  }
}