package view;

import model.LeaderboardEntry;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

// Schermata leaderboard
public class LeaderboardPanel extends JPanel {
    public final JButton backButton = new JButton("← Menu");
    public final JButton clearButton = new JButton("Reset leaderboard");

    private final DefaultTableModel tableModel = new DefaultTableModel(
                                new Object[]{"Pos", "Nome", "Score (s)", "Monete run", "Data"},
                                0
                                );
    
    private final JTable table = new JTable(tableModel);

    private final DateTimeFormatter fmt = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public LeaderboardPanel() {
        setLayout(new BorderLayout());

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setPreferredSize(backButton.getPreferredSize());

        JLabel title = new JLabel("Leaderboard - Top 10", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(backButton);
        top.add(right);
        top.add(title);

        // tabella
        table.setRowHeight(24);
        table.setFont(UiConstants.UI_FONT);
        table.getTableHeader().setFont(UiConstants.UI_FONT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.setPreferredSize(new Dimension(700, 320));
        scrollPane.setMaximumSize(new Dimension(900, 400));

        JPanel south = new JPanel();
        south.setOpaque(false);
        clearButton.setFont(UiConstants.UI_FONT);
        south.add(clearButton);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(Box.createVerticalStrut(120));
        center.add(scrollPane);
        center.add(Box.createVerticalStrut(10));
        center.add(south);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    // aggiorna la tabella con le entry (già ordinate in config.txt)
    public void setEntries(List<LeaderboardEntry> entries) {
        tableModel.setRowCount(0);

        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e = entries.get(i);

            tableModel.addRow(new Object[]{
                    i + 1,          // posizione
                    e.getName(),
                    e.getScore(),
                    e.getCoinsRun(),
                    fmt.format(e.getWhen())
            });
        }
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
            readPngFromClasspath("/assets/leaderboard_background.png");

        if (leaderboardBackground != null) {
            g.drawImage(leaderboardBackground,0, 0,
                UiConstants.WINDOW_SIZE, UiConstants.WINDOW_SIZE, this
            );
        }
    }

}
