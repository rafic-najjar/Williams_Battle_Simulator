package view;

import controller.*;
import entity.Castle;
import entity.Team;
import entity.interactables.Coin;
import entity.interactables.Hill;
import entity.interactables.TileEffect;
import entity.interactables.Trap;
import entity.troops.Troop;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BattlefieldPanel extends JPanel {

    private static final int CELL_SIZE = 35;

    private static final int SIDEBAR_WIDTH = 120;
    private static final int SIDEBAR_GAP = 16;
    private static final int SIDEBAR_PADDING = 12;

    private Round round;

    private final Image troopASprite;
    private final Image troopBSprite;
    private final Image castleSprite;
    private final Image coinSprite;
    private final Image hillSprite;
    private final Image trapSprite;

    public BattlefieldPanel() {
        setBackground(new Color(235, 245, 235));

        troopASprite = new ImageIcon("assets/Troop_A_Sprite.png").getImage();
        troopBSprite = new ImageIcon("assets/Troop_B_Sprite.png").getImage();
        castleSprite = new ImageIcon("assets/Castle_Sprite.png").getImage();
        coinSprite = new ImageIcon("assets/Coin_Sprite.png").getImage();
        hillSprite = new ImageIcon("assets/Hill_Sprite.png").getImage();
        trapSprite = new ImageIcon("assets/Trap_Sprite.png").getImage();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                placeTroopAt(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = Round.getRowCount();
        int columns = Round.getColumnCount();
        int gridWidth = columns * CELL_SIZE;
        int gridHeight = rows * CELL_SIZE;

        int offsetX = getGridOffsetX();
        int offsetY = getGridOffsetY();
        int contentX = offsetX - SIDEBAR_WIDTH - SIDEBAR_GAP;

        g.setColor(Color.gray);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                int x = j * CELL_SIZE + offsetX;
                int y = i * CELL_SIZE + offsetY;
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // effects draw first so troops and stuff stay on top of them.
        for (TileEffect effect : round.getTileEffects()) {
            drawTileEffect(g, effect, offsetX, offsetY);
        }

        drawCastle(g, round.getTeamACastle(), offsetX, offsetY);
        drawCastle(g, round.getTeamBCastle(), offsetX, offsetY);
        for (Troop troop : teamA().getArmy()) {
            drawTroop(g, troop, offsetX, offsetY);
        }
        for (Troop troop : teamB().getArmy()) {
            drawTroop(g, troop, offsetX, offsetY);
        }

        drawSidebar(g, contentX, offsetY, gridHeight, teamA(), "Team A", new Color(60, 90, 190));
        drawSidebar(g, offsetX + gridWidth + SIDEBAR_GAP, offsetY, gridHeight, teamB(), "Team B",
                new Color(190, 90, 30));
    }

    private void placeTroopAt(int mouseX, int mouseY) {
        int rows = Round.getRowCount();
        int columns = Round.getColumnCount();
        int offsetX = getGridOffsetX();
        int offsetY = getGridOffsetY();

        int column = (mouseX - offsetX) / CELL_SIZE;
        int row = (mouseY - offsetY) / CELL_SIZE;

        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return;
        }

        if (teamA().getArmy().isEmpty()) {
            return;
        }

        // Placeholder placement logic: moves the first troop in Team A's
        // army. Not tied to a selection UI yet.
        teamA().getArmy().get(0).setPosition(row, column);
        repaint();
    }

    private int getGridOffsetX() {
        int columns = Round.getColumnCount();
        int gridWidth = columns * CELL_SIZE;
        int contentWidth = gridWidth + 2 * (SIDEBAR_WIDTH + SIDEBAR_GAP);
        int contentX = (getWidth() - contentWidth) / 2;
        return contentX + SIDEBAR_WIDTH + SIDEBAR_GAP;
    }

    private int getGridOffsetY() {
        int rows = Round.getRowCount();
        int gridHeight = rows * CELL_SIZE;
        return (getHeight() - gridHeight) / 2;
    }

    private void drawCastle(Graphics g, Castle castle, int offsetX, int offsetY) {
        int x = castle.getColumn() * CELL_SIZE + offsetX;
        int y = castle.getRow() * CELL_SIZE + offsetY;

        g.drawImage(
                castleSprite,
                x - 5,
                y - 5,
                CELL_SIZE + 10,
                CELL_SIZE + 10,
                this);
    }

    private void drawTileEffect(Graphics g, TileEffect effect, int offsetX, int offsetY) {
        int x = effect.getColumn() * CELL_SIZE + offsetX;
        int y = effect.getRow() * CELL_SIZE + offsetY;

        if (effect instanceof Hill) {
            g.drawImage(
                    hillSprite,
                    x - 4,
                    y + 2,
                    CELL_SIZE + 8,
                    CELL_SIZE - 4,
                    this);
        } else if (effect instanceof Coin) {
            g.drawImage(
                    coinSprite,
                    x + 8,
                    y + 8,
                    CELL_SIZE - 16,
                    CELL_SIZE - 16,
                    this);
        } else if (effect instanceof Trap) {
            g.drawImage(
                    trapSprite,
                    x + 5,
                    y + 5,
                    CELL_SIZE - 10,
                    CELL_SIZE - 10,
                    this);
        }
    }

    private void drawTroop(Graphics g, Troop troop, int offsetX, int offsetY) {
        int x = troop.getColumn() * CELL_SIZE + offsetX;
        int y = troop.getRow() * CELL_SIZE + offsetY;

        Image sprite;

        if (troop.getTeam() == Troop.Team.teamA) {
            sprite = troopASprite;
        } else {
            sprite = troopBSprite;
        }

        g.drawImage(
                sprite,
                x - 12,
                y - 12,
                CELL_SIZE + 24,
                CELL_SIZE + 24,
                this);
    }

    // One teams column. The header and budget sit at the top and
    // the rest is left empty on purpose, for the stuff later on
    private void drawSidebar(Graphics g, int x, int y, int height,
            entity.Team team, String title, Color accent) {
        g.setColor(new Color(250, 252, 250));
        g.fillRect(x, y, SIDEBAR_WIDTH, height);

        g.setColor(new Color(200, 210, 200));
        g.drawRect(x, y, SIDEBAR_WIDTH, height);

        int headerHeight = 34;
        g.setColor(accent);
        g.fillRect(x, y, SIDEBAR_WIDTH, headerHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        drawCentred(g, title, x, y + 22);

        int budgetY = y + headerHeight + 30;

        g.setColor(new Color(120, 125, 120));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        drawCentred(g, "BUDGET", x, budgetY);

        g.setColor(new Color(50, 55, 50));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        drawCentred(g, "$" + budgetOf(team), x, budgetY + 28);

        // Divider marking where future rows will start.
        g.setColor(new Color(225, 230, 225));
        g.drawLine(x + SIDEBAR_PADDING, budgetY + 48,
                x + SIDEBAR_WIDTH - SIDEBAR_PADDING, budgetY + 48);
    }

    private int budgetOf(entity.Team team) {
        return (team == null) ? 0 : team.getBudget();
    }

    private void drawCentred(Graphics g, String text, int x, int baselineY) {
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (SIDEBAR_WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, textX, baselineY);
    }

    public void setRound(Round round) {
        this.round = round;
    }

    /*
     * Both these functions gets the team instance from round (isolates the teams to
     * round
     */
    private Team teamA() {
        return round.getTeamA();
    }

    private Team teamB() {
        return round.getTeamB();
    }
}
