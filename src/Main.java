import java.awt.EventQueue;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Game game = new Game("maps/world.map");
                JFrame window = new JFrame();
                window.setSize(1250, 650);
                window.setTitle("AllThoseTerritories");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.add(game.getGameComponent());
                window.setVisible(true);
            }
        });
    }
}