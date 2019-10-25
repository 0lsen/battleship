package de.olsen.battleship.service;

import de.olsen.battleship.exception.ThisShouldNeverHappenException;
import de.olsen.battleship.model.Board;
import de.olsen.battleship.model.HeatMap;
import de.olsen.battleship.model.HitMap;
import de.olsen.battleship.model.Orientation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeatMapperImpl implements HeatMapper {

  @Autowired
  private SpaceCalculator spaceCalculator;

  @Override
  public HeatMap build(HitMap hitMap, int minLength, int maxLength) throws ThisShouldNeverHappenException {
    return new StatefulImplementation(hitMap, minLength, maxLength).build();
  }

  private class StatefulImplementation {
    private final HitMap hitMap;
    private final int minLength;
    private final int maxLength;

    private boolean[][] freeMap;
    private HeatMap heatMap;

    private StatefulImplementation(HitMap hitMap, int minLength, int maxLength) {
      this.hitMap = hitMap;
      this.minLength = minLength;
      this.maxLength = maxLength;
    }

    private HeatMap build() throws ThisShouldNeverHappenException {
      freeMap = spaceCalculator.buildFreeMap(hitMap);
      buildHeatMap();
      return heatMap;
    }

    private void buildHeatMap() throws ThisShouldNeverHappenException {
      heatMap = new HeatMap();
      for (int i = 0; i < Board.size; i++) {
        for (int j = 0; j < Board.size; j++) {
          if (freeMap[i][j]) {
            int availableSpaceHorizontal = spaceCalculator.calculateSpace(i, j, Orientation.HORIZONTAL, maxLength, freeMap);
            int availableSpaceVertical = spaceCalculator.calculateSpace(i, j, Orientation.VERTICAL, maxLength, freeMap);
            if (availableSpaceHorizontal >= minLength) {
              heatMap.applyHeat(i, j, availableSpaceHorizontal);
            }
            if (availableSpaceVertical >= minLength) {
              heatMap.applyHeat(i, j, availableSpaceVertical);
            }
          }
        }
      }
    }
  }

  public void setSpaceCalculator(SpaceCalculator spaceCalculator) {
    this.spaceCalculator = spaceCalculator;
  }
}
