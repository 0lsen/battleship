package de.olsen.battleship.model;

import de.olsen.battleship.exception.CollisionException;
import de.olsen.battleship.exception.DoubleTapException;
import de.olsen.battleship.exception.OutOfBoundsException;
import de.olsen.battleship.model.ship.Carrier;
import de.olsen.battleship.model.ship.Destroyer;
import de.olsen.battleship.model.ship.Patrol;
import de.olsen.battleship.model.ship.Ship;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BoardTest {

  private Board board;

  @Before
  public void setUp() {
    board = new Board();
  }

  @Test
  public void verticalBounds() {
    assertAddShip(new Carrier(), new Coord(0, 0, Orientation.VERTICAL), null);
    assertAddShip(new Carrier(), new Coord(0, 5, Orientation.VERTICAL), null);
    assertAddShip(new Carrier(), new Coord(9, 0, Orientation.VERTICAL), null);
    assertAddShip(new Carrier(), new Coord(0, 6, Orientation.VERTICAL), OutOfBoundsException.class);
    assertAddShip(new Carrier(), new Coord(0, -1, Orientation.VERTICAL), OutOfBoundsException.class);
  }

  @Test
  public void horizontalBounds() {
     assertAddShip(new Carrier(), new Coord(0 , 0, Orientation.HORIZONTAL), null);
     assertAddShip(new Carrier(), new Coord(5 , 0, Orientation.HORIZONTAL), null);
     assertAddShip(new Carrier(), new Coord(0 , 9, Orientation.HORIZONTAL), null);
     assertAddShip(new Carrier(), new Coord(6 , 0, Orientation.HORIZONTAL), OutOfBoundsException.class);
     assertAddShip(new Carrier(), new Coord(-1 , 0, Orientation.HORIZONTAL), OutOfBoundsException.class);
  }

  @Test
  public void collision() {
    assertAddShip(new Carrier(), new Coord(4, 0, Orientation.VERTICAL), null);
    assertAddShip(new Destroyer(), new Coord(3, 0, Orientation.VERTICAL), null);
    assertAddShip(new Destroyer(), new Coord(4, 0, Orientation.VERTICAL), CollisionException.class);
    assertAddShip(new Destroyer(), new Coord(4, 1, Orientation.VERTICAL), CollisionException.class);
    assertAddShip(new Destroyer(), new Coord(4, 4, Orientation.VERTICAL), CollisionException.class);
    assertAddShip(new Destroyer(), new Coord(4, 5, Orientation.VERTICAL), null);
    assertAddShip(new Destroyer(), new Coord(3, 7, Orientation.HORIZONTAL), CollisionException.class);
    assertAddShip(new Destroyer(), new Coord(3, 8, Orientation.HORIZONTAL), null);
    assertAddShip(new Destroyer(), new Coord(1, 2, Orientation.HORIZONTAL), CollisionException.class);
    assertAddShip(new Destroyer(), new Coord(1, 3, Orientation.HORIZONTAL), null);
  }

  @Test
  public void shoot() {
    assertAddShip(new Carrier(), new Coord(0, 1, Orientation.VERTICAL), null);

    assertShot(0, 1, true, null);
    assertShot(0, 3, true, null);
    assertShot(0, 5, true, null);
    assertShot(0, 0, false, null);
    assertShot(0, 6, false, null);
    assertShot(1, 1, false, null);

    assertShot(0, 0, false, DoubleTapException.class);
    assertShot(0, 1, false, DoubleTapException.class);

    assertShot(-1, 0, false, OutOfBoundsException.class);
    assertShot(0, -1, false, OutOfBoundsException.class);
    assertShot(10, 0, false, OutOfBoundsException.class);
    assertShot(0, 10, false, OutOfBoundsException.class);
  }

  @Test
  public void sink() {
    Ship ship = new Destroyer();
    assertAddShip(ship, new Coord(1, 1, Orientation.HORIZONTAL), null);

    assertFalse(ship.isSunk());
    assertShot(0, 0, false, null);
    assertFalse(ship.isSunk());
    assertShot(1, 1, true, null);
    assertFalse(ship.isSunk());
    assertShot(2, 1, true, null);
    assertFalse(ship.isSunk());
    assertShot(3, 1, true, null);
    assertTrue(ship.isSunk());
  }

  @Test
  public void isBeaten() {
    Ship ship1 = new Carrier();
    Ship ship2 = new Destroyer();

    assertTrue(board.isBeaten());

    assertAddShip(ship1, new Coord(0, 0, Orientation.VERTICAL), null);
    assertAddShip(ship2, new Coord(1, 0, Orientation.VERTICAL), null);

    assertFalse(board.isBeaten());
    ship1.sink();
    assertFalse(board.isBeaten());
    ship2.sink();
    assertTrue(board.isBeaten());
  }

  @Test
  public void construct() {
    assertAddShip(new Carrier(), new Coord(0, 0, Orientation.VERTICAL), null);
    assertAddShip(new Patrol(), new Coord(1, 1, Orientation.VERTICAL), null);
    assertShot(1, 1, true, null);
    assertShot(1, 2, true, null);
    Board copy = new Board(board);
    assertEquals(1, copy.getShips().size());
    assertEquals(Patrol.class, copy.getShips().get(0).getShip().getClass());
  }

  private void assertAddShip(Ship ship, Coord coord,  Class<? extends Exception> expectedException) {
    try {
      board.addShip(new ShipArrangement(ship, coord));
      if (expectedException != null) fail();
    } catch (Exception e) {
      if (e.getClass() != expectedException ) fail();
    }
  }

  private void assertShot(int x, int y, boolean hit, Class<? extends Exception> expectedException) {
    try {
      boolean isHit = board.shoot(x, y);
      assertEquals(hit, isHit);
      if (expectedException != null) fail();
    } catch (Exception e) {
      if (e.getClass() != expectedException ) fail();
    }
  }
}