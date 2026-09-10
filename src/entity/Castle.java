package entity;

public class Castle {
    public Castle(int row, int column, int health) {
        rows = row;
        columns = column;
        m_health = health;
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

    public void takeDamage(int dmg) {
        m_health -= dmg;
    }

    public boolean isDestroyed() {
        return m_health <= 0;
    }

    private int rows;
    private int columns;
    private int m_health;
}