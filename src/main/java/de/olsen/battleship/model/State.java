package de.olsen.battleship.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class State {

  public State(Exception e) {
    error = e.getClass().getSimpleName();
  }

  private String error;
  private Phase phase;
  private Turn turn;
  private Setup setup;
  private Board playerBoard;
  private Board opponentBoard;
  private Shot lastShot;
  private boolean playerBeaten;
  private boolean opponentBeaten;
}
