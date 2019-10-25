package de.olsen.battleship.model;

import de.olsen.battleship.exception.CollisionException;
import de.olsen.battleship.exception.DoubleTapException;
import de.olsen.battleship.exception.OutOfBoundsException;
import de.olsen.battleship.exception.SetupException;
import de.olsen.battleship.exception.WrongPhaseException;
import de.olsen.battleship.model.ship.Battleship;
import de.olsen.battleship.model.ship.Carrier;
import de.olsen.battleship.model.ship.Destroyer;
import de.olsen.battleship.service.opponent.Opponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.MockitoAnnotations.initMocks;

public class GameTest {

  @InjectMocks
  private Game game;
  @Mock
  private Opponent opponent;

  private Setup setup;
  private int counter;

  @Before
  public void setUp() {
    initMocks(this);
    createSetup();
    game = new Game(setup);
  }

  private void createSetup() {
    setup = new Setup();
    try {
      setup.addToConfig(Carrier.class, 1);
      setup.addToConfig(Destroyer.class, 2);
    } catch (Exception e) {
      fail();
    };
  }

  @Test
  public void all() {
    mockOpponent();

    assertCleanState();
    assertShot(0, 0, WrongPhaseException.class);
    testPlaceShips();
    assertBasicState(Phase.FIRING, Turn.PLAYER);
    testShots();
    assertWinState(Turn.PLAYER);

    assertTrue(game.isFinished());
    game.reset();
    assertCleanState();
  }

  private void mockOpponent() {
    counter = 0;
    try {
      doAnswer(i -> {
        Board board = i.getArgument(0);
        Setup setup = i.getArgument(1);
        board.addShip(new ShipArrangement(new Carrier(), new Coord(3, 3, Orientation.VERTICAL)));
        board.addShip(new ShipArrangement(new Destroyer(), new Coord(4, 3, Orientation.VERTICAL)));
        board.addShip(new ShipArrangement(new Destroyer(), new Coord(5, 3, Orientation.VERTICAL)));
        return null;
      }).when(opponent).placeShipsOn(any(),any());
    } catch (Exception e) {
      fail();
    }

    try {
      doAnswer(i -> {
        Board board = i.getArgument(0);
        Shot shot = i.getArgument(1);
        shot.setX(counter);
        shot.setY(counter++);
        shot.setHit(board.shoot(shot.getX(), shot.getY()));
        return null;
      }).when(opponent).shootAt(any(),any(), any());
    } catch (Exception e) {
      fail();
    }
  }

  private void testPlaceShips() {
    assertPlaceShip(dataWrongShipType(), SetupException.class);
    assertPlaceShip(dataOutOfBounds(), OutOfBoundsException.class);
    assertPlaceShip(dataColliding() , CollisionException.class);
    assertPlaceShip(dataTooManyShips(), SetupException.class);
    assertPlaceShip(dataTooFewShips(), SetupException.class);
    assertPlaceShip(dataValid(), null);
    assertPlaceShip(new HashSet<>(), WrongPhaseException.class);
  }

  private Set<ShipArrangement> dataWrongShipType() {
    return new HashSet<ShipArrangement>() {{
      add(new ShipArrangement(new Carrier(), new Coord(0,0, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(1,1, Orientation.VERTICAL)));
      add(new ShipArrangement(new Battleship(), new Coord(2,2, Orientation.VERTICAL)));
    }};
  }

  private Set<ShipArrangement> dataOutOfBounds() {
    return new HashSet<ShipArrangement>() {{
      add(new ShipArrangement(new Carrier(), new Coord(0, 7, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(1,1, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(2,2, Orientation.VERTICAL)));
    }};
  }

  private Set<ShipArrangement> dataColliding() {
    return new HashSet<ShipArrangement>() {{
      add(new ShipArrangement(new Carrier(), new Coord(0,0, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(1,1, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(0,2, Orientation.HORIZONTAL)));
    }};
  }

  private Set<ShipArrangement> dataTooManyShips() {
    return new HashSet<ShipArrangement>() {{
      add(new ShipArrangement(new Carrier(), new Coord(0,0, Orientation.VERTICAL)));
      add(new ShipArrangement(new Carrier(), new Coord(4,4, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(1,1, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(2,2, Orientation.VERTICAL)));
    }};
  }

  private Set<ShipArrangement> dataTooFewShips() {
    return new HashSet<ShipArrangement>() {{
      add(new ShipArrangement(new Carrier(), new Coord(0,0, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(1,1, Orientation.VERTICAL)));
    }};
  }

  private Set<ShipArrangement> dataValid() {
    return new HashSet<ShipArrangement>() {{
      add(new ShipArrangement(new Carrier(), new Coord(1,1, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(3,1, Orientation.VERTICAL)));
      add(new ShipArrangement(new Destroyer(), new Coord(6,3, Orientation.HORIZONTAL)));
    }};
  }

  private void testShots() {
    // Player Miss
    assertShot(0, 0, null);
    assertBasicState(Phase.FIRING, Turn.OPPONENT);
    assertShotState(0, 0, false, Turn.PLAYER);

    // Opponent Miss
    assertShot(null, null, null);
    assertBasicState(Phase.FIRING, Turn.PLAYER);
    assertShotState(0, 0, false, Turn.OPPONENT);

    // Exceptions
    assertShot(0, 0, DoubleTapException.class);
    assertShot(10, 0, OutOfBoundsException.class);

    // Player Hit + Miss
    assertShot(3, 3, null);
    assertBasicState(Phase.FIRING, Turn.PLAYER);
    assertShotState(3, 3, true, Turn.PLAYER);
    assertShot(3, 2, null);
    assertBasicState(Phase.FIRING, Turn.OPPONENT);
    assertShotState(3, 2, false, Turn.PLAYER);

    // Opponent Hit + Miss
    assertShot(null, null, null);
    assertBasicState(Phase.FIRING, Turn.OPPONENT);
    assertShotState(1, 1, true, Turn.OPPONENT);
    assertShot(null, null, null);
    assertBasicState(Phase.FIRING, Turn.PLAYER);
    assertShotState(2, 2, false, Turn.OPPONENT);

    // Player Hits and Wins
    assertShot(3, 4, null);
    assertShot(3, 5, null);
    assertShot(3, 6, null);
    assertShot(3, 7, null);
    assertShot(4, 3, null);
    assertShot(4, 4, null);
    assertShot(4, 5, null);
    assertShot(5, 3, null);
    assertShot(5, 4, null);
    assertShot(5, 5, null);
  }

  private void assertPlaceShip(Set<ShipArrangement> ships, Class<? extends Exception> expectedException) {
    try {
      game.placeShips(ships, opponent);
      if (expectedException != null) fail();
    } catch (Exception e) {
      if (e.getClass() != expectedException) fail();
    }
  }

  private void assertShot(Integer x, Integer y, Class<? extends Exception> expectedException) {
    try {
      game.shoot(x, y, opponent);
      if (expectedException != null) fail();
    } catch (Exception e) {
      if (e.getClass() != expectedException) fail();
    }
  }

  private void assertCleanState() {
    State state = game.getState();
    assertBasicState(Phase.SETUP, Turn.PLAYER);
    assertEquals(0, state.getPlayerBoard().getHitMap().getCoordX().size());
    assertEquals(0, state.getPlayerBoard().getShips().size());
    assertEquals(0, state.getOpponentBoard().getHitMap().getCoordX().size());
    assertEquals(0, state.getOpponentBoard().getShips().size());
  }

  private void assertWinState(Turn victor) {
    State state = assertBasicState(Phase.FINISHED, victor);
    assertFalse(victor == Turn.PLAYER ? state.isPlayerBeaten() : state.isOpponentBeaten());
    assertTrue(victor == Turn.OPPONENT ? state.isPlayerBeaten() : state.isOpponentBeaten());
  }

  private State assertBasicState(Phase phase, Turn turn) {
    State state = game.getState();
    assertNull(state.getError());
    assertEquals(phase, state.getPhase());
    assertEquals(turn, state.getTurn());
    return state;
  }

  private void assertShotState(int x, int y, boolean hit, Turn shooter) {
    State state = game.getState();
    assertEquals(x, state.getLastShot().getX());
    assertEquals(y, state.getLastShot().getY());
    assertEquals(hit, state.getLastShot().isHit());
    assertEquals(shooter, state.getLastShot().getShooter());
  }
}