package entity.troops;

public class Troop {
    public enum Team {
        teamA,
        teamB,
    };

    public Troop(int row, int column, int health, int speed, int troopSize, Team team) {
        rows = row;
        columns = column;
        m_health = health;
        m_speed = speed;
        m_troopSize = troopSize;
        m_team = team;
    }

    public int getRow() {
        return rows;
    }

    public int getColumn() {
        return columns;
    }

    public int getHealth() {
        return m_health;
    }

    public int getSpeed() {
        return m_speed;
    }

    public int getTroopSize() {
        return m_troopSize;
    }

    public Team getTeam() {
        return m_team;
    }

    public void setPosition(int row, int column) {
        rows = row;
        columns = column;
    }

    public void move() {
        if (m_team == Team.teamA) {
            columns += m_speed;
        } else {
            columns -= m_speed;
        }
    }

    public void takeDamage(int dmg) {
        m_health -= dmg;
    }

    public boolean isDestroyed() {
        return m_health <= 0;
    }

    private int rows;
    private int columns;
    private int m_health;
    private int m_speed;
    private int m_troopSize;
    private Team m_team;
}