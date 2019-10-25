package de.olsen.battleship.service;

import de.olsen.battleship.model.HitMap;
import de.olsen.battleship.model.Orientation;

public interface SpaceCalculator {
  public boolean[][] buildFreeMap(HitMap hitMap);
  public int calculateSpace(int x, int y, Orientation orientation, int maxLength, boolean[][]freeMap);
}
