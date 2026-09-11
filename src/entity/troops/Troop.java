package entity.troops;

public class Troop {

    /* ---- ENUMS ---- */
    public enum Team {
        teamA,
        teamB
    }

    /* ---- PRIVATE VARIABLES ---- */
    private int row;
    private int column;
    private int health;
    private int speed;
    private int troopSize;
    private Team team;

    /* ---- CONSTRUCTORS ---- */
    public Troop(int row, int column, int health, int speed, int troopSize, Team team) {
        this.row = row;
        this.column = column;
        this.health = health;
        this.speed = speed;
        this.troopSize = troopSize;
        this.team = team;
    }

    /* ---- PUBLIC METHODS ---- */
    public void move() {
        if (team == Team.teamA) {
            column += speed;
        } else {
            column -= speed;
        }
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    /* ---- GETTER METHODS ---- */
    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public int getTroopSize() {
        return troopSize;
    }

    public Team getTeam() {
        return team;
    }

    /* ---- SETTER METHODS ---- */
    public void setPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }
}