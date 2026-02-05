package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

//Schermata iniziale
public class StartPanel extends JPanel {
    public final JButton playButton = new JButton("GIOCA");
    public final JButton vehicleButton = new JButton("Veicolo");
    public final JButton settingsButton = new JButton("Impostazioni");
    public final JButton leaderboardButton = new JButton("Leaderboard");

    public final JTextField nameField = new JTextField(12);

    public StartPanel() {
        setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        nameRow.setOpaque(false);
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(UiConstants.UI_FONT);
        nameField.setFont(UiConstants.UI_FONT);
        nameRow.add(nameLabel);
        nameRow.add(nameField);

        JPanel playRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        playButton.setFont(UiConstants.UI_FONT);
        playButton.setPreferredSize(new Dimension(180, 50));
        playRow.add(playButton);
        playRow.setOpaque(false);

        center.add(Box.createVerticalStrut(40));
        center.add(nameRow);
        center.add(playRow);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        bottom.setOpaque(false);
        vehicleButton.setFont(UiConstants.UI_FONT);
        settingsButton.setFont(UiConstants.UI_FONT);
        leaderboardButton.setFont(UiConstants.UI_FONT);
        
        bottom.add(vehicleButton);
        bottom.add(settingsButton);
        bottom.add(leaderboardButton);

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    //metodo per caricare correttamente immagini png
    private BufferedImage readPngFromClasspath(String resourcePath) {
        try {
            if (resourcePath == null) return null;

            return ImageIO.read(
                    getClass().getResourceAsStream(resourcePath)
            );
        } catch (Exception e) {
            return null;
        }
    }

    // sfondo
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage leaderboardBackground =
            readPngFromClasspath("/assets/menu_background.png");

        if (leaderboardBackground != null) {
            g.drawImage(leaderboardBackground,0, 0,
                UiConstants.WINDOW_SIZE, UiConstants.WINDOW_SIZE, this
            );
        }
    }
}
