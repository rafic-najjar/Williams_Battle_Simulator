package entity.troops;

// Base class for attacking troop types.
// Melee and ranged troop classes can extend this later.
public class Attacker extends Troop {

    private int attack;
    private int armour;

    public Attacker(int row, int column, int health, int speed,
            int troopSize, Team team, int attack, int armour) {
        super(row, column, health, speed, troopSize, team);

        this.attack = attack;
        this.armour = armour;
    }

    public int getAttack() {
        return attack;
    }

    public int getArmour() {
        return armour;
    }
}
