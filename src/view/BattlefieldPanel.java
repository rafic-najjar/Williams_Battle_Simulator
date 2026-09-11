package view;

import controller.*;
import entity.*;
import entity.interactables.*;
import entity.troops.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BattlefieldPanel extends JPanel {

    /* ---- STATIC VARIABLES ---- */

    /* VARIABLES FOR GRID */
    private static final int CELL_SIZE = 35;

    /* VARIABLES FOR SIDEBAR */
    private static final int SIDEBAR_WIDTH = 120;
    private static final int SIDEBAR_GAP = 16;
    private static final int SIDEBAR_PADDING = 12;
    private static final int SIDEBAR_HEADER_HEIGHT = 34;

    /* VARIABLES FOR COLOURS */
    private static final Color BACKGROUND_COLOUR = new Color(235, 245, 235);
    private static final Color SIDEBAR_FILL = new Color(250, 252, 250);
    private static final Color SIDEBAR_BORDER = new Color(200, 210, 200);
    private static final Color SIDEBAR_DIVIDER = new Color(225, 230, 225);
    private static final Color LABEL_COLOUR = new Color(120, 125, 120);
    private static final Color VALUE_COLOUR = new Color(50, 55, 50);
    private static final Color TEAM_A_ACCENT = new Color(60, 90, 190);
    private static final Color TEAM_B_ACCENT = new Color(190, 90, 30);

    /* ---- PRIVATE VARIABLES ---- */
    private Image troopASprite;
    private Image troopBSprite;
    private Image castleSprite;
    private Image coinSprite;
    private Image hillSprite;
    private Image trapSprite;

    private Round round;

    /* ---- CONSTRUCTORS ---- */
    public BattlefieldPanel() {
        setBackground(BACKGROUND_COLOUR);
        setSprites();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                placeTroopAt(e.getX(), e.getY());
            }
        });
    }

    /* ---- OVERRIDES ---- */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (round == null)
            return;// ensures nth is drawn until round is created

        int offsetX = getGridOffsetX();
        int offsetY = getGridOffsetY();

        drawGrid(g, offsetX, offsetY);
        drawTileEffects(g, offsetX, offsetY);
        drawCastles(g, offsetX, offsetY);
        drawArmies(g, offsetX, offsetY);
        drawSidebars(g, offsetX, offsetY);

        round.render();
    }

    /* ---- PRIVATE METHODS ---- */
    private void setSprites() {
        troopASprite = new ImageIcon("assets/Troop_A_Sprite.png").getImage();
        troopBSprite = new ImageIcon("assets/Troop_B_Sprite.png").getImage();
        castleSprite = new ImageIcon("assets/Castle_Sprite.png").getImage();
        coinSprite = new ImageIcon("assets/Coin_Sprite.png").getImage();
        hillSprite = new ImageIcon("assets/Hill_Sprite.png").getImage();
        trapSprite = new ImageIcon("assets/Trap_Sprite.png").getImage();

    }

    /* METHODS FOR TROOP PLACEMENT */
    private void placeTroopAt(int mouseX, int mouseY) {
        int column = (mouseX - getGridOffsetX()) / CELL_SIZE;
        int row = (mouseY - getGridOffsetY()) / CELL_SIZE;

        if (!isOnGrid(row, column))
            return;// if mouse is outside the grid then exists the method

        if (teamA().getArmy().isEmpty()) {
            return;
        }

        /*
         * Placeholder placement logic: moves the first troop in Team A's army. Not tied
         * to a selection UI yet.
         */
        teamA().getArmy().get(0).setPosition(row, column);
        repaint();
    }

    private boolean isOnGrid(int row, int column) {
        return row >= 0 && row < round.getRowCount()
                && column >= 0 && column < round.getColumnCount();
    }

    /* METHODS FOR GRID LAYOUT */
    private int getGridWidth() {
        return round.getColumnCount() * CELL_SIZE;
    }

    private int getGridHeight() {
        return round.getRowCount() * CELL_SIZE;
    }

    private int getGridOffsetX() {
        int contentWidth = getGridWidth() + 2 * (SIDEBAR_WIDTH + SIDEBAR_GAP);
        int contentX = (getWidth() - contentWidth) / 2;
        return contentX + SIDEBAR_WIDTH + SIDEBAR_GAP;
    }

    private int getGridOffsetY() {
        return (getHeight() - getGridHeight()) / 2;
    }

    /* METHODS FOR DRAWING THE BOARD */
    private void drawGrid(Graphics g, int offsetX, int offsetY) {
        g.setColor(Color.gray);

        for (int row = 0; row < round.getRowCount(); ++row) {
            for (int column = 0; column < round.getColumnCount(); ++column) {
                int x = column * CELL_SIZE + offsetX;
                int y = row * CELL_SIZE + offsetY;
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    // effects draw first so troops stay on top of them.
    private void drawTileEffects(Graphics g, int offsetX, int offsetY) {
        for (TileEffect effect : round.getTileEffects()) {
            drawTileEffect(g, effect, offsetX, offsetY);
        }
    }

    private void drawCastles(Graphics g, int offsetX, int offsetY) {
        drawCastle(g, round.getTeamACastle(), offsetX, offsetY);
        drawCastle(g, round.getTeamBCastle(), offsetX, offsetY);
    }

    private void drawArmies(Graphics g, int offsetX, int offsetY) {
        for (Troop troop : teamA().getArmy()) {
            drawTroop(g, troop, offsetX, offsetY);
        }
        for (Troop troop : teamB().getArmy()) {
            drawTroop(g, troop, offsetX, offsetY);
        }
    }

    private void drawSidebars(Graphics g, int offsetX, int offsetY) {
        int leftX = offsetX - SIDEBAR_WIDTH - SIDEBAR_GAP;
        int rightX = offsetX + getGridWidth() + SIDEBAR_GAP;
        int height = getGridHeight();

        drawSidebar(g, leftX, offsetY, height, teamA(), "Team A", TEAM_A_ACCENT);
        drawSidebar(g, rightX, offsetY, height, teamB(), "Team B", TEAM_B_ACCENT);
    }

    /* METHODS FOR DRAWING INDIVIDUAL ENTITY */
    private void drawCastle(Graphics g, Castle castle, int offsetX, int offsetY) {
        int x = castle.getColumn() * CELL_SIZE + offsetX;
        int y = castle.getRow() * CELL_SIZE + offsetY;

        g.drawImage(castleSprite, x - 5, y - 5, CELL_SIZE + 10, CELL_SIZE + 10, this);
    }

    private void drawTileEffect(Graphics g, TileEffect effect, int offsetX, int offsetY) {
        int x = effect.getColumn() * CELL_SIZE + offsetX;
        int y = effect.getRow() * CELL_SIZE + offsetY;

        if (effect instanceof Hill) {
            g.drawImage(hillSprite, x - 4, y + 2, CELL_SIZE + 8, CELL_SIZE - 4, this);
        } else if (effect instanceof Coin) {
            g.drawImage(coinSprite, x + 8, y + 8, CELL_SIZE - 16, CELL_SIZE - 16, this);
        } else if (effect instanceof Trap) {
            g.drawImage(trapSprite, x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10, this);
        }
    }

    private void drawTroop(Graphics g, Troop troop, int offsetX, int offsetY) {
        int x = troop.getColumn() * CELL_SIZE + offsetX;
        int y = troop.getRow() * CELL_SIZE + offsetY;

        Image sprite = (troop.getTeam() == Troop.Team.teamA) ? troopASprite : troopBSprite;

        g.drawImage(sprite, x - 12, y - 12, CELL_SIZE + 24, CELL_SIZE + 24, this);
    }

    /*
     * One teams column. The header and budget sit at the top and
     * the rest is left empty on purpose, for the stuff later on
     */
    private void drawSidebar(Graphics g, int x, int y, int height,
            entity.Team team, String title, Color accent) {
        g.setColor(SIDEBAR_FILL);
        g.fillRect(x, y, SIDEBAR_WIDTH, height);

        g.setColor(SIDEBAR_BORDER);
        g.drawRect(x, y, SIDEBAR_WIDTH, height);

        g.setColor(accent);
        g.fillRect(x, y, SIDEBAR_WIDTH, SIDEBAR_HEADER_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        drawCentred(g, title, x, y + 22);

        int budgetY = y + SIDEBAR_HEADER_HEIGHT + 30;

        g.setColor(LABEL_COLOUR);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        drawCentred(g, "BUDGET", x, budgetY);

        g.setColor(VALUE_COLOUR);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        drawCentred(g, "$" + team.getBudget(), x, budgetY + 28);

        // Divider marking where future rows will start.
        g.setColor(SIDEBAR_DIVIDER);
        g.drawLine(x + SIDEBAR_PADDING, budgetY + 48,
                x + SIDEBAR_WIDTH - SIDEBAR_PADDING, budgetY + 48);
    }

    private void drawCentred(Graphics g, String text, int x, int baselineY) {
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (SIDEBAR_WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, textX, baselineY);
    }

    /* METHODS FOR ROUND ACCESS */

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

    private int budgetOf(entity.Team team) {
        return (team == null) ? 0 : team.getBudget();
    }

    /* ---- SETTER METHODS ---- */

    public void setRound(Round round) {
        this.round = round;
    }
}