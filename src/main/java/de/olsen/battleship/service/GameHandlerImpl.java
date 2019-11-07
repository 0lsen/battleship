package de.olsen.battleship.service;

import de.olsen.battleship.exception.GameNotFoundException;
import de.olsen.battleship.model.Game;
import de.olsen.battleship.model.Save;
import de.olsen.battleship.model.Setup;
import de.olsen.battleship.model.Status;
import de.olsen.battleship.model.ship.Battleship;
import de.olsen.battleship.model.ship.Carrier;
import de.olsen.battleship.model.ship.Destroyer;
import de.olsen.battleship.model.ship.Patrol;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameHandlerImpl implements GameHandler {

  private final int MAXMIUM_AGE_IN_DAYS = 1;

  private Setup standardSetup = new Setup();

  private Map<String, Save> games = new HashMap<>();

  public GameHandlerImpl() {
    this.standardSetup.addToConfig(Carrier.class, 1);
    this.standardSetup.addToConfig(Battleship.class, 1);
    this.standardSetup.addToConfig(Destroyer.class, 2);
    this.standardSetup.addToConfig(Patrol.class, 1);
  }

  @Override
  public String createGame() {
    String id = createId();
    games.put(id, new Save(LocalDate.now(), new Game(standardSetup.copy())));
    return id;
  }

  @Override
  public Game findGame(String id) throws GameNotFoundException {
    Save save = games.get(id);
    if (save == null) {
      throw new GameNotFoundException();
    }
    save.setDate(LocalDate.now());
    cleanUp();
    return save.getGame();
  }

  @Override
  public List<Status> getStates() {
    return games.values().stream()
        .map(save -> new Status(save.getDate(), save.getGame().getState().getPhase()))
        .collect(Collectors.toList());
  }

  private String createId() {
    String id;
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      do {
        id = String.format("%032x", new BigInteger(1, md.digest(LocalTime.now().toString().getBytes())));
      } while (games.containsKey(id));
      return id;
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }

  private void cleanUp() {
    games.forEach((id, save) -> {
      if (Period.between(save.getDate(), LocalDate.now()).getDays() > MAXMIUM_AGE_IN_DAYS) {
        games.remove(id);
      }
    });
  }
}
