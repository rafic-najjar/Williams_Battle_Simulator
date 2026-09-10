package entity;

import java.util.ArrayList;

import entity.troops.Troop;

public class Team {

    int budget, winCount;
    String name;
    ArrayList<Troop> army; // ! Needs to be changed to group object later on

    public Team(String name) {
        budget = INITIAL_BUDGET;
        winCount = 0;
        army = new ArrayList<>();
        this.name = name;
    }

    public ArrayList<Troop> getArmy() {
        return army;
    }

    public int getBudget() {
        return budget;
    }

    public String getName() {
        return name;
    }

    // Used when a troop collects gold from the battlefield.
    public void addBudget(int amount) {
        budget += amount;
    }

    public void addTroop(Troop troop) {
        army.add(troop);
    }

    private static final int INITIAL_BUDGET = 500;
}