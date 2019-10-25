package de.olsen.battleship.service;

import de.olsen.battleship.model.Board;
import de.olsen.battleship.model.HitMap;
import de.olsen.battleship.model.Orientation;
import org.springframework.stereotype.Service;

@Service
public class SpaceCalculatorImpl implements SpaceCalculator {

  @Override
  public boolean[][] buildFreeMap(HitMap hitMap) {
    boolean[][] map = new boolean[Board.size][Board.size];
    for (int i = 0; i < Board.size; i++) {
      for (int j = 0; j < Board.size; j++) {
        map[i][j] = !hitMap.alreadyHit(i, j);
      }
    }
    return map;
  }

  @Override
  public int calculateSpace(int x, int y, Orientation orientation, int maxLength, boolean[][] freeMap) {
    int space = 1; int i = x; int j = y;
    while (
        (orientation == Orientation.HORIZONTAL ? i : j) < Board.size-1 &&
        (orientation == Orientation.HORIZONTAL ? i-x+1 : j-y+1) < maxLength &&
        freeMap[orientation == Orientation.HORIZONTAL ? i+1 : i][orientation == Orientation.HORIZONTAL ? j : j+1]
    ) {
      if (orientation == Orientation.HORIZONTAL) {
        i++;
      } else {
        j++;
      }
      space++;
    }
    i = x; j = y;
    while (
        (orientation == Orientation.HORIZONTAL ? i : j) > 0 &&
        (orientation == Orientation.HORIZONTAL ? x-i+1 : y-j+1) < maxLength &&
        freeMap[orientation == Orientation.HORIZONTAL ? i-1 : i][orientation == Orientation.HORIZONTAL ? j : j-1]
    ) {
      if (orientation == Orientation.HORIZONTAL) {
        i--;
      } else {
        j--;
      }
      space++;
    }
    return space;
  }
}
