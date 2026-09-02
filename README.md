# Williams Battle Simulator

A simple Java Swing battle simulator built for our group project. Two teams
face off across a battlefield with gold, troops, and a castle on each side.
Kept intentionally simple — built-in Java (AWT/Swing) only, no external
libraries.

## Repo setup

- Original/shared repo: `xenosM/Williams_Battle_Simulator` (`upstream`)
- Rafic forked it to `rafic-najjar/Williams_Battle_Simulator` (`origin`) to
  develop on and push changes without needing direct write access to the
  shared repo. Work gets merged back via pull request from the fork.
- Rafic's working branch: `jpanel-scaffold` (kept in sync with `main`)

## Running

```
cd src
javac App.java controller/*.java controller/states/*.java entity/*.java view/*.java
java App
```

## Known issues

- **Build currently broken**: `Round.update()` calls `currentState.update()`,
  but `GameState`'s `update()`/`render()` methods are commented out in the
  interface. Needs to be uncommented (and implemented by the state classes)
  before the project compiles.

## Progress log

- **2026-08-28** — Added `.gitignore` for a Java project (compiled output,
  packaged archives, logs, IDE files).
- **2026-08-28** — Added the basic Swing window scaffold: `App.java` opens
  a `JFrame` and adds an empty `BattlefieldPanel` (a blank `JPanel`) as the
  starting point for the game window. No simulation logic yet — just the
  window itself.
- **2026-08-30** — Rafic's part: denser battlefield grid (8x12 -> 10x16),
  each team's `$budget` drawn in the corners (added `getBudget()`/`getName()`
  to `Team`), and click-to-place for the placeholder troop (added
  `Troop.setPosition()`, clicking a tile moves the existing blue-circle troop
  there). No new troop types/objects yet — just the placement logic.
