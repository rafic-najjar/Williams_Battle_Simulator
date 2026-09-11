package controller;

import controller.states.*;
import entity.*;
import entity.interactables.*;
import entity.troops.Troop;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
What Round is now responsible for?
- Holding the board: castles, tile effects, grid size
- Holding the two teams for this battle
- Knowing which state is active and passing itself to it
- Not deciding what happens in each phase — that's the states
*/
public class Round {

    /* ---- STATIC VARIABLES ---- */

    /* VARIABLES FOR GRID */
    private static final int ROW_COUNT = 10;
    private static final int COLUMN_COUNT = 16;

    /* VARIABLES FOR TILE EFFECT */
    private static final int HILL_COUNT = 4;
    private static final int HILL_DAMAGE_BONUS = 5;
    private static final int COIN_COUNT = 6;
    private static final int COIN_VALUE = 25;
    private static final int TRAP_COUNT = 4;
    private static final int TRAP_DAMAGE = 20;

    /* ---- PRIVATE VARIABLES ---- */
    private final Team teamA;
    private final Team teamB;
    private final Castle teamACastle;
    private final Castle teamBCastle;
    private final List<TileEffect> tileEffects;
    private final Random random = new Random();
    private GameState currentState;

    /* ---- CONSTRUCTORS ---- */
    public Round(Team teamA, Team teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
        tileEffects = new ArrayList<>();
        teamACastle = new Castle(3, 0, 100);
        teamBCastle = new Castle(3, COLUMN_COUNT - 1, 100);
    }

    /* ---- PUBLIC METHODS ---- */
    public void start() {
        setCurrentState(new AllocateState());
    }

    public void update() {
        currentState.update(this); // State needs access to Round's data
    }

    public void render() {
        currentState.render(this);
    }

    /*
     * Spawn all the tile effect that have been created
     */
    public void spawnTileEffects() {
        tileEffects.clear(); // removes all the tile effects from previous round
        spawnHills();
        spawnCoins();
        spawnTraps();
    }

    /*---- PRIVATE METHODS ---- */

    /*
     * hills spawn at random empty cells at the start of the round
     */
    private void spawnHills() {
        int placed = 0;

        while (placed < HILL_COUNT) {
            int row = random.nextInt(ROW_COUNT);
            int column = random.nextInt(COLUMN_COUNT);

            if (tryAddTileEffect(new Hill(row, column, HILL_DAMAGE_BONUS))) {
                ++placed;
            }
        }
    }

    private void spawnCoins() {
        int placed = 0;

        while (placed < COIN_COUNT) {
            int row = random.nextInt(ROW_COUNT);
            int column = random.nextInt(COLUMN_COUNT);

            if (tryAddTileEffect(new Coin(row, column, COIN_VALUE))) {
                ++placed;
            }
        }
    }

    /*
     * Traps spawn at random for now. A trap belongs to whichever team owns the half
     * it lands on, which is the same rule a placement stage will enforce, so only
     * the choice of cell has to change later.
     */
    private void spawnTraps() {
        int placed = 0;

        while (placed < TRAP_COUNT) {
            int row = random.nextInt(ROW_COUNT);
            int column = random.nextInt(COLUMN_COUNT);

            if (tryAddTileEffect(new Trap(row, column, TRAP_DAMAGE, ownerOfHalf(column)))) {
                ++placed;
            }
        }
    }

    /*
     * Random spawning expects most cells to be taken, so a rejection here is
     * normal rather than an error worth reporting.
     */
    private boolean tryAddTileEffect(TileEffect effect) {
        try {
            addTileEffect(effect);
            return true;
        } catch (InvalidPlacementException e) {
            return false;
        }
    }

    /*
     * The single way anything gets onto the board. Random spawning uses it now;
     * PlaceState will use it later when the player picks the cell.
     */
    private void addTileEffect(TileEffect effect) throws InvalidPlacementException {
        int row = effect.getRow();
        int column = effect.getColumn();

        if (row < 0 || row >= ROW_COUNT || column < 0 || column >= COLUMN_COUNT) {
            throw new InvalidPlacementException(
                    "Cell " + row + "," + column + " is outside the battlefield.");
        }

        if (!isCellFree(row, column)) {
            throw new InvalidPlacementException(
                    "Cell " + row + "," + column + " is already taken.");
        }

        tileEffects.add(effect);
    }

    private Troop.Team ownerOfHalf(int column) {
        return (column < COLUMN_COUNT / 2) ? Troop.Team.teamA : Troop.Team.teamB;
    }

    private boolean isCellFree(int row, int column) {
        if (isSameCell(teamACastle.getRow(), teamACastle.getColumn(), row, column)
                || isSameCell(teamBCastle.getRow(), teamBCastle.getColumn(), row, column)) {
            return false;
        }

        for (TileEffect effect : tileEffects) {
            if (effect.isAt(row, column)) {
                return false;
            }
        }

        return true;
    }

    private boolean isSameCell(int rowA, int columnA, int rowB, int columnB) {
        return rowA == rowB && columnA == columnB;
    }

    /* ---- GETTER METHODS ---- */
    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public Castle getTeamACastle() {
        return teamACastle;
    }

    public Castle getTeamBCastle() {
        return teamBCastle;
    }

    public List<TileEffect> getTileEffects() {
        return tileEffects;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public int getRowCount() {
        return ROW_COUNT;
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    /* ---- SETTER METHODS ---- */
    public void setCurrentState(GameState state) {
        this.currentState = state;

    }

}