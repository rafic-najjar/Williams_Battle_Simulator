package view;

import java.awt.Color;
import javax.swing.JPanel;

import entity.Castle;
import entity.Coin;
import entity.Hill;
import entity.TileEffect;
import entity.Troop;
import entity.Trap;
import entity.Troop.Team;
import entity.InvalidPlacementException;

import entity.Hill;
import entity.TileEffect;

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
    private List<TileEffect> tileEffects;

    private Castle teamACastle;
    private Castle teamBCastle;
    private Troop teamATroop;

    public BattlefieldPanel() {
        setBackground(new Color(235, 245, 235));

        teamACastle = new Castle(3, 0, 100);
        teamBCastle = new Castle(3, 11, 100);
        tileEffects = new ArrayList<>();
        spawnHills();
        spawnCoins();
        spawnTraps();

        teamATroop = new Troop(3, 2, 100, 1, 10, Troop.Team.teamA);
        
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int gridWidth = columns * cellSize;
        int gridHeight = rows * cellSize;

        int offsetX = (getWidth() - gridWidth) / 2;
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
}
