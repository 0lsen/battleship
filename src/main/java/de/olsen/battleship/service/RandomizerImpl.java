package de.olsen.battleship.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomizerImpl implements Randomizer {

  private Random r = new Random();

  @Override
  public int randomInt(int max) {
    return r.nextInt(max);
  }

  @Override
  public boolean randomBool() {
    return r.nextBoolean();
  }

  @Override
  public <T extends Enum<?>> T randomEnum(Class<T> e) {
    return e.getEnumConstants()[r.nextInt(e.getEnumConstants().length)];
  }
}
