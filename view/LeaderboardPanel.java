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

/**
 * Schermata leaderboard: mostra Top 10 ordinata.
 */
public class LeaderboardPanel extends JPanel {
    public final JButton backButton = new JButton("← Menu");
    public final JButton clearButton = new JButton("Reset leaderboard");


    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Pos", "Nome", "Score (s)", "Monete run", "Data"}, 0
    );
    private final JTable table = new JTable(tableModel);

    private final DateTimeFormatter fmt = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public LeaderboardPanel() {
        setLayout(new BorderLayout());

        // destra: pannello vuoto (bilancia il layout)
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setPreferredSize(backButton.getPreferredSize());
        
        JLabel title = new JLabel("Leaderboard - Top 10", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(backButton);
        top.add(right);
        top.add(title);


        table.setRowHeight(24);
        table.setFont(UiConstants.UI_FONT);
        table.getTableHeader().setFont(UiConstants.UI_FONT);

        
        JPanel south = new JPanel();
        south.setOpaque(false);
        clearButton.setFont(UiConstants.UI_FONT);
        south.add(clearButton, BorderLayout.EAST); // accanto a backButton


        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Aggiorna la tabella con le entry già ordinate (Top 10).
     */
    public void setEntries(List<LeaderboardEntry> entries) {
        tableModel.setRowCount(0);
        int pos = 1;
        for (LeaderboardEntry e : entries) {
            tableModel.addRow(new Object[]{
                    pos++,
                    e.getName(),
                    e.getScore(),
                    e.getCoinsRun(),
                    fmt.format(e.getWhen())
            });
        }
    }

    //metodo per caricare correttamente immagini png
    private BufferedImage safeReadPng(String path) {
    try {
        if (path == null) return null;
        File f = new File(path);
        if (!f.exists()) return null;
        return ImageIO.read(f);
    } catch (IOException e) {
        return null;
        }
    }

    //restituisce il cammino assoluto
    private String getAbsolutePath(String relativePath) {
        Properties props = System.getProperties();
        String userDir = props.getProperty("user.dir");

        if (relativePath == null || relativePath.isEmpty()) {
            return userDir;
        }

        return userDir + relativePath;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage leaderboardBackground = safeReadPng(getAbsolutePath ("/resources/leaderboard_background.png"));
        if (leaderboardBackground != null) {
                 g.drawImage(leaderboardBackground, 0, 0, UiConstants.WINDOW_W, UiConstants.WINDOW_H, this);
            }
    }
}
