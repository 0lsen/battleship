/// <reference path="models.ts" />
/// <reference path="interfaces.ts" />

declare var PlainDraggable: any

class Controller {
  private $playerBoard =  $('#playerBoard')
  private $opponentBoard = $('#opponentBoard')
  private $opponentOverlay = $('#opponentOverlay')

  private $overlay = $('#overlay')

  private $resetButton = $('#reset')

  private state: State

  private ajax(route:string, data, callback:Function) {
    $.ajax("/api/"+route,
           {
             method: "post",
             data: JSON.stringify(data),
             contentType: "application/json; charset=utf-8",
             success: response => callback(response)
           }
           )
  }

  init() {
    this.$overlay.hide()
    this.$opponentOverlay.hide()
    this.status((e) => this.drawBoards(e))
    this.$resetButton.on('click', () => this.reset())
    this.$opponentBoard.on('click', (e) => {
      switch (this.state.phase) {
        case Phase.SETUP:
          this.sendShips()
          break;
        case Phase.FIRING:
          this.sendShot(e)
          break;
      }
    })
    this.$overlay.on('click', () => this.$overlay.hide())
    this.$overlay.find('>div').on('click', (e) => e.stopPropagation())
  }

  private status(callback:Function) {
    $.ajax('/api/state',
           {
             method: 'GET',
             success: response => callback(response)
           }
           )
  }

  private sendShips() {
    let ssa = new SetShipAction()
    let list = []
    this.$playerBoard.find('.ship')
        .each((i, s) => {
          list.push(this.parseShipArrangement(s))
        }
    )
    ssa.shipArrangements = list
    this.placeShips(ssa)
  }

  private parseShipArrangement(ship) {
    let $ship = $(ship)
    let type
    if ($ship.hasClass(Ship.CARRIER.toLowerCase())) type = Ship.CARRIER
    if ($ship.hasClass(Ship.BATTLESHIP.toLowerCase())) type = Ship.BATTLESHIP
    if ($ship.hasClass(Ship.DESTROYER.toLowerCase())) type = Ship.DESTROYER
    if ($ship.hasClass(Ship.PATROL.toLowerCase())) type = Ship.PATROL
    let o = $ship.hasClass('horizontal') ? Orientation.HORIZONTAL : Orientation.VERTICAL

    let arrangement = new ShipArrangement()
    let x = Math.round(($ship.offset().left - this.$playerBoard.offset().left)/40)
    let y = Math.round(($ship.offset().top - this.$playerBoard.offset().top)/40)

    arrangement.ship = new Ship(type)
    arrangement.coord = new Coord(x, y, o)
    return arrangement
  }

  private placeShips(ssa:SetShipAction) {
    this.ajax("setShips", ssa, (e) => this.drawBoards(e))
  }

  private reset() {
    $.ajax('/api/reset',
           {
             method: 'GET',
             success: () => this.status((e) => this.drawBoards(e))
           }
    )
  }

  private sendShot(e) {
    if ($(e.target).hasClass('hit')) return
    let x = Math.round((e.offsetX-20)/40)
    let y = Math.round((e.offsetY-20)/40)
    this.shoot(x, y)
  }

  private shoot(x:number, y:number) {
    let a = new ShootAction()
    a.shotX = x
    a.shotY = y

    this.ajax("shoot", a, (e) => this.drawBoards(e))
  }

  private drawBoards(state:State) {
    if (state.error) {
      this.overlay('ERROR: '+state.error)
    } else {
      this.state = state
      this.$playerBoard.find('.ship, .hit').remove()
      this.$opponentBoard.find('.ship, .hit').remove()
    }

    this.opponentBoardOverlay()

    switch (state.phase) {
      case Phase.SETUP:
        this.placeShipSetup(state.setup)
        this.opponentBoardOverlay()
        break;
      case Phase.FINISHED:
        this.overlay('WINNER: ' + (state.opponentBeaten ? Turn.PLAYER : Turn.OPPONENT))
      case Phase.FIRING:
        this.appendShips(this.$playerBoard, state.playerBoard.ships)
        this.appendShips(this.$opponentBoard, state.opponentBoard.ships)

        this.appendHits(this.$playerBoard, state.playerBoard.hitMap)
        this.appendHits(this.$opponentBoard, state.opponentBoard.hitMap)
    }
  }

  private opponentBoardOverlay() {
    let text
    if (this.state.phase == Phase.SETUP) {
      text = 'SET SHIPS'
    }
    if (this.state.phase == Phase.FIRING && this.state.turn == Turn.OPPONENT) {
      text = 'OPPONENT\'S TURN'
    }
    if (text) {
      this.$opponentOverlay.find('div').text(text)
      this.$opponentOverlay.show()
    } else {
      this.$opponentOverlay.hide()
    }
  }

  private placeShipSetup(setup:Setup) {
    setup.ships.forEach((s, i) => {
      let ship = $('<div/>')
      let regex = /\.(\w+)$/.exec(s)
      ship.append('<div><i class="fas fa-sync-alt"></i></div>')
      ship.addClass('ship')
      ship.addClass('horizontal')
      ship.addClass(regex[1].toLowerCase())
      ship.css('top', (i*10)+'%')
      this.$playerBoard.append(ship)
      this.makeDraggable(ship[0])
    })
    $('.ship > div > i').on('click touchstart', (e) => {
      let $ship = $(e.target).closest('.ship')
      $ship.toggleClass('horizontal').toggleClass('vertical')
      this.makeDraggable($ship[0])
    });
  }

  private appendShips($board:JQuery, ships:ShipArrangement[]) {
    ships.forEach(s => $board.append(this.createShip(s.ship, s.coord)))
  }

  private createShip(ship:Ship, coord:Coord): JQuery {
    let s = $('<div/>')
    s.addClass('ship')
    s.addClass(ship.type.toLowerCase())
    s.addClass(coord.o.toLowerCase())
    s.addClass('x'+coord.x)
    s.addClass('y'+coord.y)
    s.append($('<div/>'))
    return s
  }

  private appendHits($board:JQuery, hits:HitMap) {
    for (let i = 0;  i < hits.coordX.length; i++) {
      $board.append(this.createHit(hits.coordX[i], hits.coordY[i], hits.hit[i]));
    }
  }

  private createHit(x:number, y:number, hit:boolean) {
    let h = $('<div/>')
    h.addClass('hit')
    h.addClass('x'+x);
    h.addClass('y'+y);
    h.addClass(hit ? 'success' : 'fail')
    return h
  }

  private overlay(text) {
    this.$overlay.find('>div').text(text)
    this.$overlay.show()
  }

  private makeDraggable(element:HTMLElement) {
    new PlainDraggable(element, {
                         containment: document.getElementById('playerBoard'),
                         snap: {
                           x: {step: 40},
                           y: {step: 40},
                           gravity: 401
                         }
                       }
                       )
  }
}