package model;

import java.awt.Rectangle;
import java.util.*;
import utils.Config;

/**
 * Logica gioco: traffico progressivo, velocità crescente, monete, vite e invulnerabilità.
 * Salva la leaderboard nel file unico di config quando finiscono le vite.
 */
public class GameModel {
    public enum State { READY, RUNNING, GAME_OVER }

    private State state = State.READY;
    
    private final int width;
    private final int height;

    private final Player player;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Coin> coins = new ArrayList<>();
    private final List<Bonus> bonus = new ArrayList<>();
    private final Random rnd = new Random();

    private long startTimeMs;
    private long lastSpawnObstacleMs;
    private long lastSpawnCoinMs;
    private long lastSpawnBonus;
    private long speedBonusUntilMs = 0;
    
    public static final long SPEED_BONUS_DURATION = 5000; // 5 secondi

    private int coinsCollectedThisRun;
    private int scoreSeconds;

    private int baseSpeed = 3;
    private double accelPerSecond = 0.08;
    private int currentFallSpeed;

    public static final int BASE_LIVES = 3;
    public static final int INVULN_MS = 1000;

    private int lives;
    private long invulnerableUntilMs = 0;

    private final Config cfg = Config.getInstance();

    public GameModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.player = new Player(width / 2 - 20, height - 90, 40, 60);
    }

    /**
     * Avvia una nuova run: vite = base + extraLives acquistate dallo store.
     */
    public void start(VehicleCustomizationModel upgrades) {
        obstacles.clear();
        coins.clear();
        bonus.clear();
        state = State.RUNNING;

        startTimeMs = System.currentTimeMillis();
        lastSpawnObstacleMs = startTimeMs;
        lastSpawnCoinMs = startTimeMs;
        lastSpawnBonus = startTimeMs;
        scoreSeconds = 0;
        coinsCollectedThisRun = 0;

        int extra = (upgrades != null) ? upgrades.getExtraLives() : 0;
        lives = BASE_LIVES + extra;

        invulnerableUntilMs = 0;
    }

    /**
     * Tick di update del gioco.
     */
    public void update(VehicleCustomizationModel wallet, PlayerProfileModel profile){
        if (state != State.RUNNING) return;

        //reset bonus velocità
        if (System.currentTimeMillis() > speedBonusUntilMs) {
            player.setSpeedMultiplier(1.0); // torna normale
        }

        long now = System.currentTimeMillis();
        long elapsedMs = now - startTimeMs;
        scoreSeconds = (int) (elapsedMs / 1000);

        // Velocità cresce col tempo
        currentFallSpeed = (int) Math.round(baseSpeed + scoreSeconds * accelPerSecond);
        if (currentFallSpeed < 2) currentFallSpeed = 2;

        // Traffico progressivo
        int obstacleInterval = Math.max(250, 700 - scoreSeconds * 8);
        if (now - lastSpawnObstacleMs >= obstacleInterval) {
            spawnObstacle();
            lastSpawnObstacleMs = now;
        }

        // Spawn monete
        int coinInterval = Math.max(450, 1200 - scoreSeconds * 5);
        if (now - lastSpawnCoinMs >= coinInterval) {
            spawnCoin();
            lastSpawnCoinMs = now;
        }

        // Spawn Bonus
        int bonusInterval = 8000;
        if (now - lastSpawnBonus >= bonusInterval) {
            spawnBonus();
            lastSpawnBonus = now;
        }


        for (Obstacle o : obstacles) o.moveDown(currentFallSpeed);
        for (Coin c : coins) c.moveDown(currentFallSpeed);
        for (Bonus b : bonus) b.moveDown(currentFallSpeed);

        obstacles.removeIf(o -> o.getY() > height + 50);
        coins.removeIf(c -> c.getY() > height + 50);
        bonus.removeIf(c -> c.getY() > height + 50);


        checkCollisions(wallet, profile);
    }

    private void spawnObstacle() {
        int ow = 50, oh = 70;
        int lanes = 5;
        int laneW = width / lanes;
        int lane = rnd.nextInt(lanes);
        int x = lane * laneW + (laneW - ow) / 2;
        obstacles.add(new Obstacle(x, -oh, ow, oh));
    }

    private void spawnCoin() {
        int cw = 26, ch = 26;
        int lanes = 5;
        int laneW = width / lanes;
        int lane = rnd.nextInt(lanes);
        int x = lane * laneW + (laneW - cw) / 2;
        coins.add(new Coin(x, -ch, cw, ch, 1));
    }

    private void spawnBonus() {
        int bw = 26, bh = 26;
        int lanes = 5;
        int laneW = width / lanes;
        int lane = rnd.nextInt(lanes);
        int x = lane * laneW + (laneW - bw) / 2;
        bonus.add(new Bonus(x, -bh, bw, bh));
    }

    /**
     * Collisioni:
     * - Ostacolo: vita-- (se non invulnerabile), rimuove ostacolo, invulnerabilità, GAME OVER se vite=0.
     * - Moneta: aumenta wallet + monete run.
     */
    private void checkCollisions(VehicleCustomizationModel wallet, PlayerProfileModel profile) {
        long now = System.currentTimeMillis();
        Rectangle pb = player.getBounds();

        // Ostacoli
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            if (pb.intersects(o.getBounds())) {
                if (now < invulnerableUntilMs) return;

                lives--;
                obstacles.remove(i);
                invulnerableUntilMs = now + INVULN_MS;

                if (lives <= 0) {
                    state = State.GAME_OVER;

                    String name = (profile != null) ? profile.getPlayerName() : "Player";

                    // Salvataggio leaderboard nel file unico
                    cfg.addLeaderboardEntry(name, scoreSeconds, coinsCollectedThisRun, System.currentTimeMillis());
                    cfg.save();
                }
                return;
            }
        }

        // Monete
        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) {
            Coin c = it.next();
            if (pb.intersects(c.getBounds())) {
                if (wallet != null) wallet.addCoins(c.getValue());
                coinsCollectedThisRun += c.getValue();
                it.remove();
            }
        }
       
        // bonus
        Iterator<Bonus> bon = bonus.iterator();
        while (bon.hasNext()) {
            Bonus b = bon.next();
            if (pb.intersects(b.getBounds())) {
                activateSpeedBonus();
                bon.remove();          // importante: lo togli una volta preso
            }
        }

        
    }

    private void activateSpeedBonus() {
            speedBonusUntilMs = System.currentTimeMillis() + SPEED_BONUS_DURATION;
            player.setSpeedMultiplier(2.0); // raddoppia velocità
        }

    public boolean isInvulnerable() {
        return System.currentTimeMillis() < invulnerableUntilMs;
    }



    // Getters
    public State getState() { return state; }
    public Player getPlayer() { return player; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<Coin> getCoins() { return coins; }
    public List<Bonus> getBonus() { return bonus; }
    public int getScoreSeconds() { return scoreSeconds; }
    public int getCurrentFallSpeed() { return currentFallSpeed; }
    public int getCoinsCollectedThisRun() { return coinsCollectedThisRun; }
    public int getLives() { return lives; }
}

//ciao luchi