package view;

import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.FontMetrics;

import entity.Castle;
import entity.Coin;
import entity.Hill;
import entity.TileEffect;
import entity.Troop;
import entity.Trap;
import entity.Troop.Team;
import entity.InvalidPlacementException;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattlefieldPanel extends JPanel {
    private static final int rows = 8;
    private static final int columns = 12;
    private static final int cellSize = 50;

    private static final int hillCount = 4;
    private static final int hillDamageBonus = 5;

    private static final int coinCount = 6;
    private static final int coinValue = 25;

    private static final int trapCount = 4;
    private static final int trapDamage = 20;
    
    private static final int sidebarWidth = 120;
    private static final int sidebarGap = 16;
    private static final int sidebarPadding = 12;

    private List<TileEffect> tileEffects;

    private Castle teamACastle;
    private Castle teamBCastle;
    private Troop teamATroop;
    private entity.Team teamA;
    private entity.Team teamB;

    public BattlefieldPanel() {
        setBackground(new Color(235, 245, 235));

        teamACastle = new Castle(3, 0, 100);
        teamBCastle = new Castle(3, 11, 100);
        tileEffects = new ArrayList<>();
        spawnHills();
        spawnCoins();
        spawnTraps();

        teamATroop = new Troop(3, 2, 100, 1, 10, Troop.Team.teamA);

        // Placeholders so the sidebars have something to show. Session will
        // replace these with the real teams once it drives the panel.
        teamA = new entity.Team("Team A");
        teamB = new entity.Team("Team B");
        
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int gridWidth = columns * cellSize;
        int gridHeight = rows * cellSize;

       int contentWidth = gridWidth + 2 * (sidebarWidth + sidebarGap);
        int contentX = (getWidth() - contentWidth) / 2;
        int offsetX = contentX + sidebarWidth + sidebarGap;

        int offsetY = (getHeight() - gridHeight) / 2;

        g.setColor(Color.gray);
        for (int i = 0; i < rows; ++i)
        {
            for (int j = 0; j < columns; ++j)
            {
                int x = j * cellSize + offsetX;
                int y = i * cellSize + offsetY;
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
                // effects draw first so troops and stuff stay on top of them.
        for (TileEffect effect : tileEffects)
        {
            drawTileEffect(g, effect, offsetX, offsetY);
        }

        drawCastle(g, teamACastle, offsetX, offsetY);
        drawCastle(g, teamBCastle, offsetX, offsetY);
        drawTroop(g, teamATroop, offsetX, offsetY);

        drawSidebar(g, contentX, offsetY, gridHeight, teamA, "Team A", new Color(60, 90, 190));
        drawSidebar(g, offsetX + gridWidth + sidebarGap, offsetY, gridHeight, teamB, "Team B", new Color(190, 90, 30));
    }

    private void drawCastle(Graphics g, Castle castle, int offsetX, int offsetY)
    {
        int x = castle.getColumn() * cellSize + offsetX;
        int y = castle.getRow() * cellSize + offsetY;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, cellSize, cellSize);
    }

        // hills spawn at random empty cells at the start of the round rather than
    private void spawnHills()
    {
        Random random = new Random();
        int placed = 0;

        while (placed < hillCount)
        {
            int row = random.nextInt(rows);
            int column = random.nextInt(columns);

            if (isCellFree(row, column))
            {
                tileEffects.add(new Hill(row, column, hillDamageBonus));
                ++placed;
            }
        }
    }

    private void spawnCoins()
    {
        Random random = new Random();
        int placed = 0;

        while (placed < coinCount)
        {
            int row = random.nextInt(rows);
            int column = random.nextInt(columns);

            if (tryAddEffect(new Coin(row, column, coinValue)))
            {
                ++placed;
            }
        }
    }

    // Traps spawn at random for now. A trap belongs to whichever team owns the
    // half it lands on, which is the same rule a placement stage will enforce,
    // so only the choice of cell has to change later.
    private void spawnTraps()
    {
        Random random = new Random();
        int placed = 0;

        while (placed < trapCount)
        {
            int row = random.nextInt(rows);
            int column = random.nextInt(columns);

            if (tryAddEffect(new Trap(row, column, trapDamage, ownerOfHalf(column))))
            {
                ++placed;
            }
        }
    }

   private Troop.Team ownerOfHalf(int column)
    {
        return (column < columns / 2) ? Troop.Team.teamA : Troop.Team.teamB;
    }

    // The single way anything gets onto the board. Random spawning uses it now;
    // PlaceState will use it later when the player picks the cell.
    public void addEffect(TileEffect effect) throws InvalidPlacementException
    {
        int row = effect.getRow();
        int column = effect.getColumn();

        if (row < 0 || row >= rows || column < 0 || column >= columns)
        {
            throw new InvalidPlacementException(
                "Cell " + row + "," + column + " is outside the battlefield.");
        }

        if (!isCellFree(row, column))
        {
            throw new InvalidPlacementException(
                "Cell " + row + "," + column + " is already taken.");
        }

        tileEffects.add(effect);
    }

    // Random spawning expects most cells to be taken, so a rejection here is
    // normal rather than an error worth reporting.
    private boolean tryAddEffect(TileEffect effect)
    {
        try
        {
            addEffect(effect);
            return true;
        }
        catch (InvalidPlacementException e)
        {
            return false;
        }
    }

    private boolean isCellFree(int row, int column)
    {
        if (isSameCell(teamACastle.getRow(), teamACastle.getColumn(), row, column)
         || isSameCell(teamBCastle.getRow(), teamBCastle.getColumn(), row, column))
        {
            return false;
        }

        for (TileEffect effect : tileEffects)
        {
            if (effect.isAt(row, column))
            {
                return false;
            }
        }

        return true;
    }

    private boolean isSameCell(int rowA, int columnA, int rowB, int columnB)
    {
        return rowA == rowB && columnA == columnB;
    }

    private void drawTileEffect(Graphics g, TileEffect effect, int offsetX, int offsetY)
    {
        int x = effect.getColumn() * cellSize + offsetX;
        int y = effect.getRow() * cellSize + offsetY;

        if (effect instanceof Hill)
        {
            g.setColor(new Color(120, 155, 95));
            int[] xs = { x + 6, x + cellSize / 2, x + cellSize - 6 };
            int[] ys = { y + cellSize - 8, y + 8, y + cellSize - 8 };
            g.fillPolygon(xs, ys, 3);
        }
        else if (effect instanceof Coin)
        {
            int inset = cellSize / 3;
            g.setColor(new Color(214, 174, 54));
            g.fillOval(x + inset, y + inset, cellSize - 2 * inset, cellSize - 2 * inset);
        }
        else if (effect instanceof Trap)
        {
            int inset = cellSize / 4;
            g.setColor(new Color(150, 40, 40));
            g.fillRect(x + inset, y + inset, cellSize - 2 * inset, cellSize - 2 * inset);
        }
    }

    private void drawTroop(Graphics g, Troop troop, int offsetX, int offsetY)
    {
        int x = troop.getColumn() * cellSize + offsetX;
        int y = troop.getRow() * cellSize + offsetY;

        g.setColor(Color.BLUE);
        g.fillOval(x, y, cellSize, cellSize);
    }

    
    // One teams column. The header and budget sit at the top and
    // the rest is left empty on purpose, for the stuff later on 
    private void drawSidebar(Graphics g, int x, int y, int height,
                             entity.Team team, String title, Color accent)
    {
        g.setColor(new Color(250, 252, 250));
        g.fillRect(x, y, sidebarWidth, height);

        g.setColor(new Color(200, 210, 200));
        g.drawRect(x, y, sidebarWidth, height);

        int headerHeight = 34;
        g.setColor(accent);
        g.fillRect(x, y, sidebarWidth, headerHeight);

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
        g.drawLine(x + sidebarPadding, budgetY + 48,
                   x + sidebarWidth - sidebarPadding, budgetY + 48);
    }

    private int budgetOf(entity.Team team)
    {
        return (team == null) ? 0 : team.getBudget();
    }

    private void drawCentred(Graphics g, String text, int x, int baselineY)
    {
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (sidebarWidth - metrics.stringWidth(text)) / 2;
        g.drawString(text, textX, baselineY);
    }

    // Lets Session hand the panel the real teams once it is wired up.
    public void setTeams(entity.Team teamA, entity.Team teamB)
    {
        this.teamA = teamA;
        this.teamB = teamB;
    }

}
