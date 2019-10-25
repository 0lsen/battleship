package de.olsen.battleship.model;

import de.olsen.battleship.exception.SetupException;
import de.olsen.battleship.model.ship.Carrier;
import de.olsen.battleship.model.ship.Destroyer;
import de.olsen.battleship.model.ship.Ship;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SetupTest {

  private Setup setup;

  @Before
  public void setUp() {
    setup = new Setup();
  }

  @Test
  public void addShips() {
    setup.addToConfig(Carrier.class, 2);

    assertFalse(setup.isComplete());
    assertAddShip(Carrier.class, null);
    assertFalse(setup.isComplete());
    assertAddShip(Destroyer.class, SetupException.class);
    assertFalse(setup.isComplete());
    assertAddShip(Carrier.class, null);
    assertTrue(setup.isComplete());
    assertAddShip(Carrier.class, SetupException.class);
    assertTrue(setup.isComplete());

    assertEquals(2, setup.getShips().size());
  }

  @Test
  public void getShips() {
    setup.addToConfig(Carrier.class, 1);
    setup.addToConfig(Destroyer.class, 2);

    List<Class<? extends Ship>> ships = setup.getShips();

    assertEquals(3, ships.size());
    assertEquals(1, ships.stream().filter(s -> s.equals(Carrier.class)).count());
    assertEquals(2, ships.stream().filter(s -> s.equals(Destroyer.class)).count());
  }

  private void assertAddShip(Class<? extends Ship> ship, Class<? extends Exception> expectedException) {
    try {
      setup.checkShip(ship);
      setup.addShip(ship);
      if (expectedException != null) fail();
    } catch (Exception e) {
      if (e.getClass() != expectedException) fail();
    }
  }
}