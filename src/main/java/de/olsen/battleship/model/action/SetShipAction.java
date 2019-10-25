package de.olsen.battleship.model.action;

import de.olsen.battleship.model.ShipArrangement;
import lombok.Data;

import java.util.Set;

@Data
public class SetShipAction {
  private Set<ShipArrangement> shipArrangements;
}
