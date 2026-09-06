import controller.*;
import javax.swing.JFrame;
import view.BattlefieldPanel;

public class App {
    public static void main(String[] args) {
        BattlefieldPanel panel = new BattlefieldPanel();
        // Frame and session use the same panel so session can update what's on the screen.

        JFrame frame = new JFrame("The Williams - Battle Simulator");
        frame.add(panel);
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Session session = new Session();
        session.setPanel(panel);
        session.start();
    }
}
