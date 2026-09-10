package entity.troops;

// Base class for support troop types.
// Healer and other support classes can extend this later.
public class Support extends Troop {

    private int healAmount;

    public Support(int row, int column, int health, int speed,
            int troopSize, Team team, int healAmount) {
        super(row, column, health, speed, troopSize, team);

        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void heal(Troop troop) {
        // Healing behaviour still needs to be added
    }
}