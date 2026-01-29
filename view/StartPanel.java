package view;

import javax.swing.*;
import java.awt.*;

/**
 * Schermata iniziale:
 * - input nome giocatore
 * - bottone centrale "GIOCA"
 * - in basso: veicolo, impostazioni, leaderboard
 */
public class StartPanel extends JPanel {
    public final JButton playButton = new JButton("GIOCA");
    public final JButton vehicleButton = new JButton("Veicolo");
    public final JButton settingsButton = new JButton("Impostazioni");
    public final JButton leaderboardButton = new JButton("Leaderboard");

    public final JTextField nameField = new JTextField(12);

    public StartPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("ON THE RUSH", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setFont(UiConstants.UI_FONT);
        nameField.setFont(UiConstants.UI_FONT);
        nameRow.add(nameLabel);
        nameRow.add(nameField);

        JPanel playRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        playButton.setFont(UiConstants.UI_FONT);
        playButton.setPreferredSize(new Dimension(180, 50));
        playRow.add(playButton);

        center.add(Box.createVerticalStrut(40));
        center.add(nameRow);
        center.add(playRow);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        vehicleButton.setFont(UiConstants.UI_FONT);
        settingsButton.setFont(UiConstants.UI_FONT);
        leaderboardButton.setFont(UiConstants.UI_FONT);

        bottom.add(vehicleButton);
        bottom.add(settingsButton);
        bottom.add(leaderboardButton);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}
