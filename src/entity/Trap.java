package entity;

// Damages the first enemy troop to enter the cell, then is spent.

// Traps are spawned at random for now, but they carry an owning team from the
// start so that a placement stage can create them the same way later
public class Trap extends TileEffect {
    public Trap(int row, int column, int damage, Troop.Team owner)
    {
        super(row, column, owner);
        m_damage = damage;
        m_triggered = false;
    }

    public int getDamage() { return m_damage; }

    @Override
    public void onEnter(Troop troop)
    {
        if (m_triggered)
        {
            return;
        }

        // Friendly troops walk over their own team's traps safely.
        if (troop.getTeam() == getOwner())
        {
            return;
        }

        troop.takeDamage(m_damage);
        m_triggered = true;
    }

    @Override
    public boolean isConsumed()
    {
        return m_triggered;
    }

    private int m_damage;
    private boolean m_triggered;
}