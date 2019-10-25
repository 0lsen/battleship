package de.olsen.battleship.service.opponent;

import de.olsen.battleship.exception.CollisionException;
import de.olsen.battleship.exception.DoubleTapException;
import de.olsen.battleship.exception.OutOfBoundsException;
import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import de.olsen.battleship.model.Board;
import de.olsen.battleship.model.HeatMap;
import de.olsen.battleship.model.Orientation;
import de.olsen.battleship.model.Setup;
import de.olsen.battleship.model.ShipArrangement;
import de.olsen.battleship.model.Shot;
import de.olsen.battleship.model.ship.Ship;
import de.olsen.battleship.service.HeatMapper;
import de.olsen.battleship.service.Randomizer;
import de.olsen.battleship.service.SpaceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProbabilityBasedOpponent implements Opponent {

  @Autowired
  private Randomizer randomizer;
  @Autowired
  private HeatMapper heatMapper;
  @Autowired
  private SpaceCalculator spaceCalculator;

  @Override
  public void placeShipsOn(Board board, Setup setup) throws CollisionException {
    // don't use it. I deem the RandomOpponent best for now.
    throw new CollisionException();
  }

  @Override
  public void shootAt(Board board, Shot shot, Setup setup) throws DoubleTapException, ThisShouldNeverHappenException {
    new StatefulImplementation(board, shot, setup).shoot();
  }

  private class StatefulImplementation {
    private final Board board;
    private final Board boardView;
    private final Shot shot;
    private final Setup setup;

    private boolean[][] freeMap;
    private int minLength;
    private int maxLength;

    private List<Map.Entry<Integer, Integer>> hitsOnUnsunkShips;

    private StatefulImplementation(Board board, Shot shot, Setup setup) {
      this.board = board;
      this.shot = shot;
      this.setup = setup;
      this.boardView = new Board(board);
    }

    private void shoot() throws ThisShouldNeverHappenException {
      setMinMaxLength();
      if (noHitsOnUnsunkShips()) {
        shootAtNewTarget();
      } else {
        shootAtKnownTarget();
      }
    }

    private void setMinMaxLength() throws ThisShouldNeverHappenException {
      List<Ship> remainingShips = remainingShips();

      Optional<Ship> shortestRemainingShip = remainingShips.stream().min(Comparator.comparing(Ship::size));
      Optional<Ship> longestRemainingShip = remainingShips.stream().max(Comparator.comparing(Ship::size));
      if (shortestRemainingShip.isPresent() && longestRemainingShip.isPresent()) {
        minLength = shortestRemainingShip.get().size();
        maxLength = longestRemainingShip.get().size();
      } else {
        throw new ThisShouldNeverHappenException();
      }
    }

    private List<Ship> remainingShips() {
      List<Ship> remainingShips = setup.getShips().stream().map(this::createShip).collect(Collectors.toList());
      List<Ship> sunkShips = boardView.getShips().stream().map(ShipArrangement::getShip).collect(Collectors.toList());
      for (Ship sunk : sunkShips) {
        for (int i = 0; i < remainingShips.size(); i++) {
          if (sunk.getClass().equals(remainingShips.get(i).getClass())) {
            remainingShips.remove(i);
            break;
          }
        }
      }
      return remainingShips;
    }

    private Ship createShip(Class<? extends Ship> clazz) {
      try {
        return clazz.newInstance();
      } catch (Exception e) {
        return null;
      }
    }

    private boolean noHitsOnUnsunkShips() {
      List<Map.Entry<Integer, Integer>> sunkShipsCoordinates = new ArrayList<>();
      boardView.getShips().forEach(s -> sunkShipsCoordinates.addAll(Board.shipCoordinates(s.getShip().size(), s.getCoord())));

      List<Map.Entry<Integer, Integer>> allHitCoordinates = IntStream.range(0, boardView.getHitMap().getHit().size())
          .filter(i -> boardView.getHitMap().getHit().get(i))
          .mapToObj(i -> new AbstractMap.SimpleEntry<Integer, Integer>(boardView.getHitMap().getCoordX().get(i), boardView.getHitMap().getCoordY().get(i)))
          .collect(Collectors.toList());

      hitsOnUnsunkShips = allHitCoordinates.stream()
          .filter(h -> !sunkShipsCoordinates.contains(h))
          .collect(Collectors.toList());

      return hitsOnUnsunkShips.isEmpty();
    }

    private void shootAtNewTarget() throws ThisShouldNeverHappenException {
      HeatMap heatMap = heatMapper.build(boardView.getHitMap(), minLength, maxLength);
      int sum = heatMap.getHeat().stream().mapToInt(Integer::intValue).sum();
      int rand = randomizer.randomInt(sum);
      for (int i = 0; i < heatMap.getHeat().size(); i++) {
        rand = rand-heatMap.getHeat().get(i);
        if (rand < 1) {
          shot.setX(heatMap.getCoordX().get(i));
          shot.setY(heatMap.getCoordY().get(i));
          try {
            shot.setHit(board.shoot(shot.getX(), shot.getY()));
          } catch (DoubleTapException | OutOfBoundsException e) {
            throw new ThisShouldNeverHappenException();
          }
          break;
        }
      }
    }

    private void shootAtKnownTarget() throws ThisShouldNeverHappenException {
      Orientation orientation = hitsOnUnsunkShips.size() > 1 ? determineLikelyOrientation() : null;
      shootAtAdjacentField(orientation);
    }

    private Orientation determineLikelyOrientation() throws ThisShouldNeverHappenException {
      if (hitsOnUnsunkShips.get(0).getKey().equals(hitsOnUnsunkShips.get(1).getKey())) {
        return Orientation.VERTICAL;
      }
      if (hitsOnUnsunkShips.get(0).getValue().equals(hitsOnUnsunkShips.get(1).getValue())) {
        return Orientation.HORIZONTAL;
      }
      throw new ThisShouldNeverHappenException();
    }

    private void shootAtAdjacentField(Orientation preferredOrientation) throws ThisShouldNeverHappenException {
      buildFreeMap();
      int x = hitsOnUnsunkShips.get(0).getKey();
      int y = hitsOnUnsunkShips.get(0).getValue();
      boolean success;
      if (preferredOrientation == null) {
        preferredOrientation = randomizer.randomEnum(Orientation.class);
      }
      boolean direction = randomizer.randomBool();
      if (isThereEnoughSpace(x, y, preferredOrientation)) {
        success = attemptShot(x, y, direction, preferredOrientation);
        if (success) {
          return;
        }
        direction = !direction;
        success = attemptShot(x, y, direction, preferredOrientation);
        if (success) {
          return;
        }
      }
      direction = randomizer.randomBool();
      preferredOrientation = preferredOrientation.equals(Orientation.HORIZONTAL) ? Orientation.VERTICAL : Orientation.HORIZONTAL;
      success = attemptShot(x, y, direction, preferredOrientation);
      if (success) {
        return;
      }
      direction = !direction;
      success = attemptShot(x, y, direction, preferredOrientation);
      if (!success) {
        throw new ThisShouldNeverHappenException();
      }
    }

    private void buildFreeMap() {
      freeMap = spaceCalculator.buildFreeMap(boardView.getHitMap());
      hitsOnUnsunkShips.forEach(s -> freeMap[s.getKey()][s.getValue()] = true);
    }

    private boolean attemptShot(int x, int y, boolean direction, Orientation orientation) throws ThisShouldNeverHappenException {
      int i = x;
      int j = y;
      while (isUnsunkShipHit(i, j) && isInBounds(i, j, direction, orientation)) {
        switch (orientation) {
          case HORIZONTAL:
            i = direction ? i+1 : i-1;
            break;
          case VERTICAL:
            j = direction ? j+1 : j-1;
            break;
        }
      }
      if (!boardView.getHitMap().alreadyHit(i, j)) {
        placeShot(i, j);
        return true;
      } else {
        return false;
      }
    }

    private boolean isThereEnoughSpace(int x, int y, Orientation orientation) {
      return spaceCalculator.calculateSpace(x, y, orientation, maxLength, freeMap) >= minLength;
    }

    private boolean isUnsunkShipHit(int x, int y) {
      return hitsOnUnsunkShips.stream().anyMatch(e -> e.getKey() == x && e.getValue() == y);
    }

    private boolean isInBounds(int x, int y, boolean direction, Orientation orientation) {
      if (orientation.equals(Orientation.HORIZONTAL)) {
        x = direction ? x+1 : x-1;
      } else {
        y = direction ? y+1 : y-1;
      }
      return x < Board.size && y < Board.size && x >= 0 && y >= 0;
    }

    private void placeShot(int x, int y) throws ThisShouldNeverHappenException {
      shot.setX(x);
      shot.setY(y);
      try {
        shot.setHit(board.shoot(x, y));
      } catch (OutOfBoundsException | DoubleTapException e) {
        throw new ThisShouldNeverHappenException();
      }
    }
  }

  public void setRandomizer(Randomizer randomizer) {
    this.randomizer = randomizer;
  }

  public void setHeatMapper(HeatMapper heatMapper) {
    this.heatMapper = heatMapper;
  }
}
