package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import entity.Castle;
import entity.Coin;
import entity.Hill;
import entity.InvalidPlacementException;
import entity.TileEffect;
import entity.Trap;
import entity.Troop;

public class BattlefieldPanel extends JPanel {
    private static final int rows = 10;
    private static final int columns = 16;
    private static final int cellSize = 35;

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
    // Removed the Troop entity here because panel will use the Troops inside teamA and teamB
    private entity.Team teamA;
    private entity.Team teamB;

    private Image troopASprite;
    private Image troopBSprite;
    private Image castleSprite;
    private Image coinSprite;
    private Image hillSprite;
    private Image trapSprite;

    public BattlefieldPanel() {
        setBackground(new Color(235, 245, 235));

        troopASprite = new ImageIcon("assets/Troop_A_Sprite.png").getImage();
        troopBSprite = new ImageIcon("assets/Troop_B_Sprite.png").getImage();
        castleSprite = new ImageIcon("assets/Castle_Sprite.png").getImage();
        coinSprite = new ImageIcon("assets/Coin_Sprite.png").getImage();
        hillSprite = new ImageIcon("assets/Hill_Sprite.png").getImage();
        trapSprite = new ImageIcon("assets/Trap_Sprite.png").getImage();

        teamACastle = new Castle(3, 0, 100);
        teamBCastle = new Castle(3, columns - 1, 100);
        tileEffects = new ArrayList<>();
        spawnHills();
        spawnCoins();
        spawnTraps();

        // Placeholders so the sidebars have something to show. Session will
        // replace these with the real teams once it drives the panel.
        teamA = new entity.Team("Team A");
        teamB = new entity.Team("Team B");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                placeTroopAt(e.getX(), e.getY());
            }
        });
    }

    private void placeTroopAt(int mouseX, int mouseY)
    {
        int offsetX = getGridOffsetX();
        int offsetY = getGridOffsetY();

        int column = (mouseX - offsetX) / cellSize;
        int row = (mouseY - offsetY) / cellSize;

        if (row < 0 || row >= rows || column < 0 || column >= columns)
        {
            return;
        }

        teamATroop.setPosition(row, column);
        repaint();
    }

    private int getGridOffsetX()
    {
        int gridWidth = columns * cellSize;
        int contentWidth = gridWidth + 2 * (sidebarWidth + sidebarGap);
        int contentX = (getWidth() - contentWidth) / 2;
        return contentX + sidebarWidth + sidebarGap;
    }

    private int getGridOffsetY()
    {
        int gridHeight = rows * cellSize;
        return (getHeight() - gridHeight) / 2;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int gridWidth = columns * cellSize;
        int gridHeight = rows * cellSize;

        int offsetX = getGridOffsetX();
        int offsetY = getGridOffsetY();
        int contentX = offsetX - sidebarWidth - sidebarGap;

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
        for (Troop troop : teamA.getArmy())
        {
            drawTroop(g, troop, offsetX, offsetY);
        }
        for (Troop troop : teamB.getArmy())
        {
            drawTroop(g, troop, offsetX, offsetY);
        }


        drawSidebar(g, contentX, offsetY, gridHeight, teamA, "Team A", new Color(60, 90, 190));
        drawSidebar(g, offsetX + gridWidth + sidebarGap, offsetY, gridHeight, teamB, "Team B", new Color(190, 90, 30));
    }

    private void drawCastle(Graphics g, Castle castle, int offsetX, int offsetY)
    {
        int x = castle.getColumn() * cellSize + offsetX;
        int y = castle.getRow() * cellSize + offsetY;

        g.drawImage(
            castleSprite,
            x - 5,
            y - 5,
            cellSize + 10,
            cellSize + 10,
            this
        );
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
            g.drawImage(
                hillSprite,
                x - 4,
                y + 2,
                cellSize + 8,
                cellSize - 4,
                this
            );
        }
        else if (effect instanceof Coin)
        {
            g.drawImage(
                coinSprite,
                x + 8,
                y + 8,
                cellSize - 16,
                cellSize - 16,
                this
            );
        }
        else if (effect instanceof Trap)
        {
            g.drawImage(
                trapSprite,
                x + 5,
                y + 5,
                cellSize - 10,
                cellSize - 10,
                this
            );
        }
    }

    private void drawTroop(Graphics g, Troop troop, int offsetX, int offsetY)
    {
        int x = troop.getColumn() * cellSize + offsetX;
        int y = troop.getRow() * cellSize + offsetY;

        Image sprite;

        if (troop.getTeam() == Troop.Team.teamA)
        {
            sprite = troopASprite;
        }
        else
        {
            sprite = troopBSprite;
        }

        g.drawImage(
            sprite,
            x - 12,
            y - 12,
            cellSize + 24,
            cellSize + 24,
            this
        );
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
