package de.olsen.battleship.service.opponent;

import de.olsen.battleship.exception.CollisionException;
import de.olsen.battleship.exception.DoubleTapException;
import de.olsen.battleship.exception.OutOfBoundsException;
import de.olsen.battleship.model.Board;
import de.olsen.battleship.model.Coord;
import de.olsen.battleship.model.Orientation;
import de.olsen.battleship.model.Setup;
import de.olsen.battleship.model.ShipArrangement;
import de.olsen.battleship.model.Shot;
import de.olsen.battleship.model.ship.Ship;
import de.olsen.battleship.service.Randomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RandomOpponentImpl implements Opponent {

  @Autowired
  private Randomizer randomizer;

  @Override
  public void placeShipsOn(Board board, Setup setup) {
    setup.getShips().forEach(s -> setShip(s, board));
  }

  @Override
  public void shootAt(Board board, Shot shot, Setup setup) {
    boolean success = false;
    do {
      try {
        shot.setX(randomizer.randomInt(Board.size));
        shot.setY(randomizer.randomInt(Board.size));
        shot.setHit(board.shoot(shot.getX(), shot.getY()));
        success = true;
      } catch (DoubleTapException | OutOfBoundsException e) {}
    } while (!success);
  }

  private void setShip(Class<? extends Ship> clazz, Board board) {
    boolean success = false;
    try {
      Ship ship = clazz.newInstance();
      Coord coord = new Coord();
      do {
        try {
          do {
            coord.setX(randomizer.randomInt(Board.size));
            coord.setY(randomizer.randomInt(Board.size));
          } while (coord.getX()+ship.size() > Board.size && coord.getY()+ship.size() > Board.size);

          coord.setO(
              coord.getX()+ship.size() > Board.size
              ? Orientation.VERTICAL
              : coord.getY()+ship.size() > Board.size
                ? Orientation.HORIZONTAL
                : randomizer.randomEnum(Orientation.class)
          );

          board.addShip(new ShipArrangement(ship, coord));
          success = true;
        } catch (CollisionException | OutOfBoundsException e) {}
      } while (!success);
    } catch (Exception e) {}
  }
}
