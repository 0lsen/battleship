package de.olsen.battleship.model.ship;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Carrier.class, name = "CARRIER"),
    @JsonSubTypes.Type(value = Battleship.class, name = "BATTLESHIP"),
    @JsonSubTypes.Type(value = Destroyer.class, name = "DESTROYER"),
    @JsonSubTypes.Type(value = Patrol.class, name = "PATROL"),
})
public interface IShip {
  public int size();
  public boolean isSunk();
}
