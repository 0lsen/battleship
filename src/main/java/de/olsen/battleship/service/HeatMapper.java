package de.olsen.battleship.service;

import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import de.olsen.battleship.model.HeatMap;
import de.olsen.battleship.model.HitMap;

public interface HeatMapper {
  public HeatMap build(HitMap hitMap, int minLength, int maxLength) throws ThisShouldNeverHappenException;
}
