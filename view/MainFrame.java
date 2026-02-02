package view;

import javax.swing.*;
import java.awt.*;

// Finestra principale contenente tutte le schermate tramite CardLayout
public class MainFrame extends JFrame {
    public static final String START = "START";
    public static final String GAME = "GAME";
    public static final String VEHICLE = "VEHICLE";
    public static final String SETTINGS = "SETTINGS";
    public static final String LEADERBOARD = "LEADERBOARD";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);

    public MainFrame() {
        super("On The Rush");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        setContentPane(root);
        setSize(UiConstants.WINDOW_SIZE, UiConstants.WINDOW_SIZE);
        setLocationRelativeTo(null);
    }

    public void addScreen(String name, JPanel panel) {
        root.add(panel, name);
    }

    public void showScreen(String name) {
        cardLayout.show(root, name);
    }
}
