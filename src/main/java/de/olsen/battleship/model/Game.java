package de.olsen.battleship.model;

import de.olsen.battleship.exception.CollisionException;
import de.olsen.battleship.exception.DoubleTapException;
import de.olsen.battleship.exception.OutOfBoundsException;
import de.olsen.battleship.exception.SetupException;
import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import de.olsen.battleship.exception.WrongPhaseException;
import de.olsen.battleship.service.opponent.Opponent;

import java.util.Set;

public class Game {

  public Game(Setup setup) {
    this.setup = setup;
  }

  private Setup setup;

  private Board playerBoard = new Board();
  private Board opponentBoard = new Board();

  private Phase phase = Phase.SETUP;
  private Turn turn = Turn.PLAYER;

  private Shot shot;

  public void placeShips(Set<ShipArrangement> arrangements, Opponent opponent) throws WrongPhaseException, SetupException, OutOfBoundsException, CollisionException {
    if (phase != Phase.SETUP) {
      throw new WrongPhaseException();
    }
    for (ShipArrangement arrangement : arrangements) {
      try {
        setup.checkShip(arrangement.getShip().getClass());
        playerBoard.addShip(arrangement);
        setup.addShip(arrangement.getShip().getClass());
      } catch (Exception e) {
        reset();
        throw e;
      }
    }
    if (setup.isComplete()) {
      opponent.placeShipsOn(opponentBoard, setup);
      phase = Phase.FIRING;
    } else {
      reset();
      throw new SetupException();
    }
  }

  public void shoot(Integer x, Integer y, Opponent opponent) throws WrongPhaseException, DoubleTapException, OutOfBoundsException, ThisShouldNeverHappenException {
    if (phase != Phase.FIRING) {
      throw new WrongPhaseException();
    }
    shot = new Shot();
    shot.setShooter(turn);
    if (turn == Turn.PLAYER) {
      shot.setX(x);
      shot.setY(y);
      shot.setHit(opponentBoard.shoot(x, y));
      if (!shot.isHit()) {
        turn = Turn.OPPONENT;
      }
    } else {
      opponent.shootAt(playerBoard, shot, setup);
      if (!shot.isHit()) {
        turn = Turn.PLAYER;
      }
    }

    if (opponentBoard.isBeaten()) {
      phase = Phase.FINISHED;
    }
    if (playerBoard.isBeaten()) {
      phase = Phase.FINISHED;
    }
  }

  public boolean isFinished() {
    return phase.equals(Phase.FINISHED);
  }

  public void reset() {
    playerBoard = new Board();
    opponentBoard = new Board();
    phase = Phase.SETUP;
    turn = Turn.PLAYER;
    setup = setup.copy();
  }

  public State getState() {
    State state = new State();
    state.setPhase(phase);
    state.setTurn(turn);
    state.setSetup(setup);
    state.setPlayerBoard(playerBoard);
    state.setOpponentBoard(new Board(opponentBoard));
    state.setLastShot(shot);
    state.setPlayerBeaten(playerBoard.isBeaten());
    state.setOpponentBeaten(opponentBoard.isBeaten());
    return state;
  }
}
