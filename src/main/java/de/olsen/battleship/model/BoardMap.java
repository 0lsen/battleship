package de.olsen.battleship.model;

import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class BoardMap {
  protected List<Integer> coordX = new ArrayList<>();
  protected List<Integer> coordY = new ArrayList<>();

  private List<Integer> findIndices(List<Integer> list, int value) {
    List<Integer> matches = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) == value) {
        matches.add(i);
      }
    }
    return matches;
  }

  public boolean exists(int x, int y) {
    List<Integer> matchesX = findIndices(coordX, x);
    List<Integer> matchesY = findIndices(coordY, y);

    return matchesX.stream().anyMatch(matchesY::contains);
  }

  public int findIndex(int x, int y) throws ThisShouldNeverHappenException {
    List<Integer> matchesX = findIndices(coordX, x);
    List<Integer> matchesY = findIndices(coordY, y);

    Optional<Integer> match = matchesX.stream().filter(matchesY::contains).findFirst();
    if (match.isPresent()) {
      return match.get();
    } else {
      throw new ThisShouldNeverHappenException();
    }
  }
}
