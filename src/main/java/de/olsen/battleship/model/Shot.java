package de.olsen.battleship.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Shot {
  private Turn shooter;
  private boolean hit;
  private int x;
  private int y;
}
