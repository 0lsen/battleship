package de.olsen.battleship.controller;

import de.olsen.battleship.exception.GameNotFoundException;
import de.olsen.battleship.model.Game;
import de.olsen.battleship.model.State;
import de.olsen.battleship.model.Status;
import de.olsen.battleship.model.action.SetShipAction;
import de.olsen.battleship.model.action.ShootAction;
import de.olsen.battleship.model.ship.Battleship;
import de.olsen.battleship.model.ship.Carrier;
import de.olsen.battleship.model.ship.Destroyer;
import de.olsen.battleship.model.ship.Patrol;
import de.olsen.battleship.model.ship.Ship;
import de.olsen.battleship.model.ship.ShipEnum;
import de.olsen.battleship.service.GameHandler;
import de.olsen.battleship.service.opponent.ProbabilityBasedOpponent;
import de.olsen.battleship.service.opponent.RandomOpponentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

  private final String cookieName = "session";
  private final String indexUri = "/index.html";
  private final Map<ShipEnum, Class<? extends Ship>> shipMap = new HashMap<ShipEnum, Class<? extends Ship>>(){{
    put(ShipEnum.CARRIER, Carrier.class);
    put(ShipEnum.BATTLESHIP, Battleship.class);
    put(ShipEnum.DESTROYER, Destroyer.class);
    put(ShipEnum.PATROL, Patrol.class);
  }};

  @Autowired
  private GameHandler handler;
  @Autowired
  private RandomOpponentImpl randomOpponent;
  @Autowired
  private ProbabilityBasedOpponent probabilityBasedOpponent;

  @GetMapping(value = "/**")
  public ResponseEntity<Resource> staticAssets(HttpServletRequest request) {
    String uri = request.getRequestURI();
    if (uri.equals("/")) {
      uri = indexUri;
    }
    Resource resource = new ClassPathResource("static"+uri);
    if (uri.equals(indexUri)) {
      return resourceWithCookie(request, resource);
    } else {
      return ResponseEntity.ok().body(resource);
    }
  }

  @GetMapping(value = "/api/state", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public State status(@CookieValue(name = cookieName) String session) {
    try {
      Game game = getGame(session);
      return game.getState();
    } catch (Exception e) {
      return new State(e);
    }
  }

  @PostMapping(value = "/api/setShips", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public State setShips(@RequestBody SetShipAction action, @CookieValue(name = cookieName) String session) {
    try {
      Game game = getGame(session);
      game.placeShips(action.getShipArrangements(), randomOpponent);
      return game.getState();
    } catch (Exception e) {
      return new State(e);
    }
  }

  @PostMapping(value = "/api/shoot", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public State shoot(@RequestBody ShootAction action, @CookieValue(name = cookieName) String session) {
    try {
      Game game = getGame(session);
      game.shoot(action.getShotX(), action.getShotY(), probabilityBasedOpponent);
      return game.getState();
    } catch (Exception e) {
      return new State(e);
    }
  }

  @GetMapping(value = "/api/reset")
  public ResponseEntity reset(@CookieValue(name = cookieName) String session) {
    try {
      Game game = getGame(session);
      game.reset();
      return ResponseEntity.ok().build();
    } catch (GameNotFoundException e){
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }

  @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<Status> status() {
    return handler.getStates();
  }

  private ResponseEntity<Resource> resourceWithCookie(HttpServletRequest request, Resource resource) {
    String id = getId(request);
    if (id == null) {
      id = handler.createGame();
    } else {
      try {
        handler.findGame(id);
      } catch (GameNotFoundException e) {
        id = handler.createGame();
      }
    }

    return ResponseEntity.ok().header("Set-Cookie",cookieName+"="+id).body(resource);
  }

  private String getId(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals(cookieName)) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private Game getGame(String session) throws GameNotFoundException {
    return handler.findGame(session);
  }
}