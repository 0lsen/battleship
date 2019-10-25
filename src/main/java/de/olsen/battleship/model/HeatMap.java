package de.olsen.battleship.model;

import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HeatMap extends BoardMap {
  private List<Integer> heat = new ArrayList<>();

  public void applyHeat(int x, int y, int amount) throws ThisShouldNeverHappenException {
    int index = findOrCreate(x, y);
    heat.set(index, heat.get(index)+amount*amount);
  }

  private int findOrCreate(int x, int y) throws ThisShouldNeverHappenException {
    if (!exists(x, y)) {
      coordX.add(x);
      coordY.add(y);
      heat.add(0);
    }
    return findIndex(x,y);
  }
}
