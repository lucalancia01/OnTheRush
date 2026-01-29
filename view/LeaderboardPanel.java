package view;

import model.LeaderboardEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Schermata leaderboard: mostra Top 10 ordinata.
 */
public class LeaderboardPanel extends JPanel {
    public final JButton backButton = new JButton("← Menu");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Pos", "Nome", "Score (s)", "Monete run", "Data"}, 0
    );
    private final JTable table = new JTable(tableModel);

    private final DateTimeFormatter fmt = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public LeaderboardPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Leaderboard - Top 10", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(backButton);

        table.setRowHeight(24);
        table.setFont(UiConstants.UI_FONT);
        table.getTableHeader().setFont(UiConstants.UI_FONT);

        add(top, BorderLayout.NORTH);
        add(title, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);
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
}
