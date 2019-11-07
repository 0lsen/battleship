# Battleship

Play [battleship](https://en.wikipedia.org/wiki/Battleship_\(game\)) against a machine opponent.

[![pipeline status](https://gitlab.com/0lsen/battleship/badges/master/pipeline.svg)](https://gitlab.com/0lsen/battleship/commits/master)

## Build
- TODO

## Technical
- TODO

## Rules
- 5 ships (of length X): 1 carrier (5), 1 battleship (4), 2 destroyers (3), 1 patrol boat (2)
- The corresponding coordinates of the enemy's sunk ships (all coordinates hit) are revealed. 
If a ship is successfully sunk it will be revealed on the board. (makes "AI" decisions much more trivial)

## "AI" Guidelines
- Places ships randomly (assuming there will always be a possible way to add another ship to the current arrangement of ships).
- Does not know your actual ships' placement. Deals with a map of hits, misses and sunk ships, just like you do.
- Works statelessly, hence will not know about what's been going on before it's turn.
- If a 'non-attributed shot' (not belonging to a known sunk ship) is present: will try to extend a line of other 'non-attributed shots' in a random direction (if there's enough space for the shortest remaining ship) or otherwise try to establish such a line (considering enough space for the shortest remaining ship).
- Else will rate all available fields with a score for possible hit probability and randomly
 choose one based on those scores.
