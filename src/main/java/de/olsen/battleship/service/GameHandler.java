package de.olsen.battleship.service;

import de.olsen.battleship.exception.GameNotFoundException;
import de.olsen.battleship.model.Game;
import de.olsen.battleship.model.Status;

import java.util.List;

public interface GameHandler {

  public String createGame();
  public Game findGame(String id) throws GameNotFoundException;
  public List<Status> getStates();
}
