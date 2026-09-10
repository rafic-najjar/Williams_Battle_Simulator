package controller;

import entity.*;
import entity.troops.Troop;
import javax.swing.Timer;
import view.*;

public class Session {
    private static final int TICK_DURATION = 300;

    Team teamA, teamB;
    Round round;
    Timer timer;
    BattlefieldPanel panel;

    public Session() {
        teamA = new Team("teamA");
        teamB = new Team("teamB");
        teamA.addTroop(new Troop(3, 2, 100, 1, 10, Troop.Team.teamA)); // Adding troops to each team for testing
                                                                       // temporarily
        teamB.addTroop(new Troop(3, 9, 100, 1, 10, Troop.Team.teamB));
        teamA.addTroop(new Troop(3, 2, 100, 1, 10, Troop.Team.teamA));
        teamB.addTroop(new Troop(3, 9, 100, 1, 10, Troop.Team.teamB));

        round = new Round(teamA, teamB);

    }

    /*
     * starts the session by creating the two teams and initiate the first round
     */
    public void start() {

        // Creating a new round
        round.start();
        panel.setRound(round);

        // Start the timer
        timer = new Timer(TICK_DURATION, e -> {
            round.update();
            panel.repaint(); // triggers paintComponent to run again and show updated positions
        });
        timer.start();

    }

    public void setPanel(BattlefieldPanel panel) {
        this.panel = panel;
    }
}
