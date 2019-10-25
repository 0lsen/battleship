package de.olsen.battleship.service;

import de.olsen.battleship.exception.GameNotFoundException;
import de.olsen.battleship.model.Game;
import de.olsen.battleship.model.Phase;
import de.olsen.battleship.model.Status;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class GameHandlerImplTest {

  private GameHandlerImpl gameHandler;

  @Before
  public void setUp() {
    gameHandler = new GameHandlerImpl();
  }

  @Test
  public void all() {
    String id = "id";
    Game game = assertFindGame(id, GameNotFoundException.class);
    assertNull(game);

    id = gameHandler.createGame();
    game = assertFindGame(id, null);
    assertNotNull(game);

    List<Status> gameStates = gameHandler.getStates();
    assertEquals(1, gameStates.size());
    assertEquals(Phase.SETUP, gameStates.get(0).getPhase());
    assertEquals(LocalDate.now(), gameStates.get(0).getDate());
  }

  private Game assertFindGame(String id, Class<? extends Exception> expectedException) {
    try {
      Game game = gameHandler.findGame(id);
      if (expectedException != null) fail();
      return game;
    } catch (Exception e) {
      if (e.getClass() != expectedException) fail();
      return null;
    }
  }
}