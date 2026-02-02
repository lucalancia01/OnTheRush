package model;

import java.time.Instant;
import java.util.*;
import utils.Config;

// Legge le entry dal Config unico, le trasforma in oggetti e ritorna top 10
public class LeaderboardModel {
    private final Config cfg = Config.getInstance();

    public List<LeaderboardEntry> getTop10() {
        List<String> lines = cfg.getLeaderboardLines();
        List<LeaderboardEntry> entries = new ArrayList<>();

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;

            // Formato: nome;score;coinsRun;timestamp
            String[] p = line.split(";");
            if (p.length != 4) continue;

            try {
                String name = p[0];
                int score = Integer.parseInt(p[1]);
                int coinsRun = Integer.parseInt(p[2]);
                long ts = Long.parseLong(p[3]);

                entries.add(new LeaderboardEntry(name, score, coinsRun, Instant.ofEpochMilli(ts)));
            } catch (Exception ignored) {}
        }

        // ordino anche qui
        entries.sort(Comparator.comparingInt(LeaderboardEntry::getScore).reversed());
        return entries.size() > 10 ? entries.subList(0, 10) : entries;
    }
}
