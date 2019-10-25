package de.olsen.battleship.model;

import de.olsen.battleship.exception.CollisionException;
import de.olsen.battleship.exception.DoubleTapException;
import de.olsen.battleship.exception.OutOfBoundsException;
import de.olsen.battleship.model.ship.Ship;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Board {

  public static final int size = 10;

  private HitMap hitMap = new HitMap();
  private List<ShipArrangement> ships = new ArrayList<>();

  public Board(Board board) {
    hitMap = board.getHitMap();
    ships = board.getShips().stream()
        .filter(s -> s.getShip().isSunk())
        .collect(Collectors.toList());
  }

  public boolean isBeaten() {
    return ships.stream().allMatch(sa -> sa.getShip().isSunk());
  }

  public void addShip(ShipArrangement arrangement) throws CollisionException, OutOfBoundsException {
    checkBounds(arrangement.getShip().size(), arrangement.getCoord());
    checkCollision(arrangement.getShip().size(), arrangement.getCoord());
    ships.add(arrangement);
  }

  public boolean shoot(int x, int y) throws DoubleTapException, OutOfBoundsException {
    checkBounds(x, y);
    if (hitMap.alreadyHit(x, y)) {
      throw new DoubleTapException();
    }
    boolean hit = isHit(new AbstractMap.SimpleEntry<Integer, Integer>(x, y));
    hitMap.placeHit(x, y, hit);
    return hit;
  }

  private void checkBounds(int length, Coord coord) throws OutOfBoundsException {
    checkBounds(coord.getX() + (coord.getO() == Orientation.HORIZONTAL ? length-1 : 0), coord.getY());
    checkBounds(coord.getX(), coord.getY() + (coord.getO() == Orientation.VERTICAL ? length-1 : 0));
  }

  private void checkBounds(int x, int y) throws OutOfBoundsException {
    if (Math.min(x, y) < 0 || Math.max(x, y) >= size) {
      throw new OutOfBoundsException();
    }
  }

  private boolean isHit(Map.Entry<Integer, Integer> shotCoordinates) {
    for (ShipArrangement sa : ships) {
      List<Map.Entry<Integer, Integer>> shipCoordinates = shipCoordinates(sa.getShip().size(), sa.getCoord());
      if (shipCoordinates.contains(shotCoordinates)) {
        checkSunk(sa.getShip(), shotCoordinates, shipCoordinates);
        return true;
      }
    }
    return false;
  }

  private void checkCollision(int length, Coord coord) throws CollisionException {
    List<Map.Entry<Integer, Integer>> newShipCoordinates = shipCoordinates(length, coord);
    for (ShipArrangement sa : ships) {
      List<Map.Entry<Integer, Integer>> existingShipCoordinates = shipCoordinates(sa.getShip().size(), sa.getCoord());
      if (newShipCoordinates.stream().anyMatch(existingShipCoordinates::contains)) {
        throw new CollisionException();
      }
    }
  }

  public static List<Map.Entry<Integer, Integer>> shipCoordinates(int length, Coord coord) {
    // TODO: all methods using this could surely be improved
    List<Map.Entry<Integer, Integer>> list = new ArrayList<>();
    int x = coord.getX();
    int y = coord.getY();
    for (int i = 0; i < length; i++) {
      list.add(new AbstractMap.SimpleEntry<>(x, y));
      if (coord.getO() == Orientation.VERTICAL)
        y++;
      else
        x++;
    }
    return list;
  }

  private void checkSunk(Ship ship, Map.Entry<Integer, Integer> shotCoordinates, List<Map.Entry<Integer, Integer>> coordinates) {
    for (Map.Entry<Integer, Integer> coord : coordinates) {
      if (!coord.equals(shotCoordinates) && !hitMap.alreadyHit(coord.getKey(), coord.getValue())) {
        return;
      }
    }
    ship.sink();
  }
}
