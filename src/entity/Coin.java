package entity;

// gold spawns on the battlefield. The first troop to reach it collects the
// coin for their team.

public class Coin extends TileEffect {
    public Coin(int row, int column, int amount)
    {
        // Gold is neutral until someone picks it up.
        super(row, column, null);
        m_amount = amount;
        m_collectedBy = null;
    }

    public int getAmount() { return m_amount; }
    public Troop.Team getCollectedBy() { return m_collectedBy; }

    @Override
    public void onEnter(Troop troop)
    {
        if (m_collectedBy == null)
        {
            m_collectedBy = troop.getTeam();
        }
    }

    // Unlike a Hill, a collected coin is spent and leaves the board.
    @Override
    public boolean isConsumed()
    {
        return m_collectedBy != null;
    }

    private int m_amount;
    private Troop.Team m_collectedBy;
}