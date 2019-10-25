package de.olsen.battleship.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class Status {
  private LocalDate date;
  private Phase phase;
}
