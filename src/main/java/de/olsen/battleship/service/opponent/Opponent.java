package de.olsen.battleship.service.opponent;

import de.olsen.battleship.exception.CollisionException;
import de.olsen.battleship.exception.DoubleTapException;
import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import de.olsen.battleship.model.Board;
import de.olsen.battleship.model.Setup;
import de.olsen.battleship.model.Shot;

public interface Opponent {
  public void placeShipsOn(Board board, Setup setup) throws CollisionException;
  public void shootAt(Board board, Shot shot, Setup setup) throws DoubleTapException, ThisShouldNeverHappenException;
}
