package de.olsen.battleship.service.opponent;

import de.olsen.battleship.model.Board;
import de.olsen.battleship.model.Coord;
import de.olsen.battleship.model.Orientation;
import de.olsen.battleship.model.Setup;
import de.olsen.battleship.model.ShipArrangement;
import de.olsen.battleship.model.Shot;
import de.olsen.battleship.model.ship.Carrier;
import de.olsen.battleship.model.ship.Destroyer;
import de.olsen.battleship.service.Randomizer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RandomOpponentImplTest {

  @InjectMocks
  private RandomOpponentImpl opponent;
  @Mock
  private Randomizer randomizerMock;

  private Setup setup;

  @Before
  public void setUp() {
    initMocks(this);
    createSetup();
  }

  @Test
  public void all() {
    mockRandomizer();

    Board opponentBoard = new Board();
    Board playerBoard = createPlayerBoard();

    opponent.placeShipsOn(opponentBoard, setup);
    assertEquals(3, opponentBoard.getShips().size());

    Shot shot = new Shot();
    shot.setX(1);
    shot.setY(1);

    opponent.shootAt(playerBoard, shot, setup);
    assertTrue(shot.isHit());
  }

  private void mockRandomizer() {
    when(randomizerMock.randomInt(anyInt()))
        // Player board coords
        .thenReturn(1)
        .thenReturn(1)
        .thenReturn(2)
        .thenReturn(1)
        .thenReturn(3)
        .thenReturn(1)
        // Opponent board coords
        .thenReturn(1)
        .thenReturn(1)
        .thenReturn(2)
        .thenReturn(1)
        .thenReturn(3)
        .thenReturn(1);
    when(randomizerMock.randomEnum(Orientation.class))
        .thenReturn(Orientation.VERTICAL);
  }

  private void createSetup() {
    setup = new Setup();
    setup.addToConfig(Carrier.class, 1);
    setup.addToConfig(Destroyer.class, 2);
  }

  private Board createPlayerBoard() {
    Board board = new Board();
    setup.getShips().forEach(s -> {
      try {
        board.addShip(new ShipArrangement(s.newInstance(), new Coord(randomizerMock.randomInt(Board.size),
                                                                     randomizerMock.randomInt(Board.size),
                                                                     randomizerMock.randomEnum(Orientation.class))));
      } catch (Exception e) {
        fail();
      }
    });
    return board;
  }
}