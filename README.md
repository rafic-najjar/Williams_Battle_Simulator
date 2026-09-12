# Williams Battle Simulator

A simple Java Swing battle simulator built for our group project. Two teams
face off across a battlefield with gold, troops, and a castle on each side.
Kept intentionally simple — built-in Java (AWT/Swing) only, no external
libraries.

## Running

```
cd src
javac App.java controller/*.java controller/states/*.java entity/*.java view/*.java
java App
```

## Notes

- Battlefield grid: 10x16 tiles.
- Each team's `$` budget is displayed on the battlefield.
- Click-to-place: clicking a tile moves a troop there, with a hover
  highlight on the tile under the cursor before you click.
