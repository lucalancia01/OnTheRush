package model;

import java.time.Instant;

// Gestione singola entry della leaderboard
public class LeaderboardEntry {
    private final String name;
    private final int score;
    private final int coinsRun;
    private final Instant when;

    public LeaderboardEntry(String name, int score, int coinsRun, Instant when) {
        this.name = name;
        this.score = score;
        this.coinsRun = coinsRun;
        this.when = when;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public int getCoinsRun() { return coinsRun; }
    public Instant getWhen() { return when; }
}
