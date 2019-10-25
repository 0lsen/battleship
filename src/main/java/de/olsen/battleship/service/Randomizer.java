package de.olsen.battleship.service;

public interface Randomizer {
  public int randomInt(int max);
  public boolean randomBool();
  public <T extends Enum<?>> T randomEnum(Class<T> e);
}
