package de.olsen.battleship.model;

import de.olsen.battleship.exception.SetupException;
import de.olsen.battleship.model.ship.Ship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Setup {
  private Map<Class<? extends Ship>, Integer> shipConfig = new HashMap<>();
  private List<Class<?extends Ship>> shipsSet = new ArrayList<>();

  public Setup copy() {
    Setup setup = new Setup();
    shipConfig.forEach(setup::addToConfig);
    return setup;
  }

  public void addToConfig(Class<? extends Ship> ship, int count) {
    shipConfig.put(ship, count);
  }

  public void checkShip(Class<? extends Ship> ship) throws SetupException {
    if (shipConfig.get(ship) == null || shipsSet.stream().filter(s -> s.equals(ship)).count() >= shipConfig.get(ship)) {
      throw new SetupException();
    };
  }

  public void addShip(Class<? extends Ship> ship) {
    shipsSet.add(ship);
  }

  public boolean isComplete() {
    boolean complete = true;
    for (Class<? extends Ship> ship : shipConfig.keySet()) {
      if (shipsSet.stream().filter(s -> s.equals(ship)).count() != shipConfig.get(ship)) {
        complete = false;
      }
    }
    return complete;
  }

  public List<Class<? extends Ship>> getShips() {
    List<Class<? extends Ship>> list = new ArrayList<>();
    shipConfig.forEach((s, i) -> {
      list.addAll(Collections.nCopies(i, s));
    });
    return list;
  }
}
