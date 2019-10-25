package de.olsen.battleship.model;

import de.olsen.battleship.model.ship.Ship;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShipArrangement {
  private Ship ship;
  private Coord coord;
}
