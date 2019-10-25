package de.olsen.battleship.model.ship;

import lombok.Data;

@Data
public abstract class Ship implements IShip {

  private boolean sunk = false;

  public void sink() {
    sunk = true;
  }
}
