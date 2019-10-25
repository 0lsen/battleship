class SetShipAction {
  shipArrangements: ShipArrangement[]
}

class ShipArrangement {
  ship: Ship
  coord: Coord
}

class ShootAction {
  shotX: number
  shotY: number
}

class Coord {
  constructor(x: number, y: number, o: string) {
    this.x = x;
    this.y = y;
    this.o = o;
  }

  x: number
  y: number
  o: string
}

class State {
  error: string
  phase: string
  turn: string
  setup: Setup
  playerBoard: Board
  opponentBoard: Board
  lastShot: Shot
  playerBeaten: boolean
  opponentBeaten: boolean
}

class Setup {
  ships: string[]
}

class Board {
  hitMap: HitMap
  ships: ShipArrangement[]
}

class HitMap {
  coordX: number[]
  coordY: number[]
  hit: boolean[]
}

class Phase {
  static SETUP = "SETUP"
  static FIRING = "FIRING"
  static FINISHED = "FINISHED"
}

class Turn {
  static PLAYER = "PLAYER"
  static OPPONENT = "OPPONENT"
}

class Ship {
  constructor(type: string) {
    this.type = type;
  }

  type: string
  static CARRIER = "CARRIER"
  static BATTLESHIP = "BATTLESHIP"
  static DESTROYER = "DESTROYER"
  static PATROL = "PATROL"
}

class Orientation {
  static HORIZONTAL = "HORIZONTAL"
  static VERTICAL = "VERTICAL"
}

class Shot {
  shooter: string
  hit: boolean
  x: number
  y: number
}