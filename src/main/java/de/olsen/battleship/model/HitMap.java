package de.olsen.battleship.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HitMap extends BoardMap {
  private List<Boolean> hit = new ArrayList<>();

  public boolean alreadyHit(int x, int y) {
    return exists(x, y);
  }

  public void placeHit(int x, int y, boolean hit) {
    coordX.add(x);
    coordY.add(y);
    this.hit.add(hit);
  }
}
