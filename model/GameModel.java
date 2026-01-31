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
    private final List<Multiplier> multiplier = new ArrayList<>();
    private final Random rnd = new Random();

    private long startTimeMs;
    private long lastSpawnObstacleMs;
    private long lastSpawnCoinMs;
    private long lastSpawnBonusMs;
    private long lastSpawnMultiplierMs;

    private long bonusUntilMs = 0;        // bonus speed
    private long scoreBonusUntilMs = 0;   // bonus score x2

    private static final int SCORE_MULTIPLIER = 2;
    public static final long BONUS_DURATION = 5000; // 5 secondi

    private long lastScoreUpdateMs = 0;
    private long scoreMsAccumulator = 0;

    private int coinsCollectedThisRun;
    private int scoreSeconds = 0;

    private int baseSpeed = 3;
    private double accelPerSecond = 0.08;
    private int currentFallSpeed;

    public static final int BASE_LIVES = 3;
    public static final int INVULN_MS = 1000;

    private int lives;
    private long invulnerableUntilMs = 0;

    // flags HUD (si spengono a fine timer)
    private boolean isX2Active = false;
    private boolean isBonusActive = false;

    // ===== Anti-overlap spawn (lanes) =====
    private static final int LANES = 5;
    private static final int MAX_SPAWN_TRIES = 12;
    private static final int MIN_VERTICAL_GAP = 60; // aumenta/diminuisci a gusto

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
        multiplier.clear();
        state = State.RUNNING;

        startTimeMs = System.currentTimeMillis();
        lastSpawnObstacleMs = startTimeMs;
        lastSpawnCoinMs = startTimeMs;
        lastSpawnBonusMs = startTimeMs;
        lastSpawnMultiplierMs = startTimeMs;

        lastScoreUpdateMs = startTimeMs;
        scoreMsAccumulator = 0;
        scoreSeconds = 0;

        coinsCollectedThisRun = 0;

        // reset bonus
        bonusUntilMs = 0;
        scoreBonusUntilMs = 0;
        isBonusActive = false;
        isX2Active = false;
        player.setSpeedMultiplier(1.0);

        int extra = (upgrades != null) ? upgrades.getExtraLives() : 0;
        lives = BASE_LIVES + extra;

        invulnerableUntilMs = 0;
    }

    /**
     * Tick di update del gioco.
     */
    public void update(VehicleCustomizationModel wallet, PlayerProfileModel profile) {
        if (state != State.RUNNING) return;

        long now = System.currentTimeMillis();

        // ===== Gestione scadenza bonus (spegne anche scritte HUD) =====
        if (now > bonusUntilMs) {
            player.setSpeedMultiplier(1.0);
            isBonusActive = false;
        }
        if (now > scoreBonusUntilMs) {
            isX2Active = false;
        }

        // ===== Score per tempo =====
        long deltaMs = now - lastScoreUpdateMs;
        lastScoreUpdateMs = now;

        scoreMsAccumulator += deltaMs;
        while (scoreMsAccumulator >= 1000) {
            scoreMsAccumulator -= 1000;

            // se bonus score attivo, +2 invece di +1
            if (now <= scoreBonusUntilMs) scoreSeconds += SCORE_MULTIPLIER;
            else scoreSeconds += 1;
        }

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
        int coinInterval = 2000;
        if (now - lastSpawnCoinMs >= coinInterval) {
            spawnCoin();
            lastSpawnCoinMs = now;
        }

        // Spawn Bonus speed
        int bonusInterval = 8000;
        if (now - lastSpawnBonusMs >= bonusInterval) {
            spawnBonus();
            lastSpawnBonusMs = now;
        }

        // Spawn Multiplier x2
        int multiplierInterval = 8000;
        if (now - lastSpawnMultiplierMs >= multiplierInterval) {
            spawnMultiplier();
            lastSpawnMultiplierMs = now;
        }

        // Movimento oggetti
        for (Obstacle o : obstacles) o.moveDown(currentFallSpeed);
        for (Coin c : coins) c.moveDown(currentFallSpeed);
        for (Bonus b : bonus) b.moveDown(currentFallSpeed);
        for (Multiplier m : multiplier) m.moveDown(currentFallSpeed);

        // Cleanup
        obstacles.removeIf(o -> o.getY() > height + 50);
        coins.removeIf(c -> c.getY() > height + 50);
        bonus.removeIf(b -> b.getY() > height + 50);
        multiplier.removeIf(m -> m.getY() > height + 50);

        checkCollisions(wallet, profile);
    }

    // =========================================================
    // =============== SPAWN SENZA SOVRAPPOSIZIONI ===============
    // =========================================================

    private int laneWidth() {
        return width / LANES;
    }

    private int laneCenterX(int lane, int objW) {
        int lw = laneWidth();
        return lane * lw + (lw - objW) / 2;
    }

    private boolean overlapsSomething(Rectangle r) {
        for (Obstacle o : obstacles)
            if (r.intersects(new Rectangle(o.getX(), o.getY(), o.getW(), o.getH()))) return true;

        for (Coin c : coins)
            if (r.intersects(new Rectangle(c.getX(), c.getY(), c.getW(), c.getH()))) return true;

        for (Bonus b : bonus)
            if (r.intersects(new Rectangle(b.getX(), b.getY(), b.getW(), b.getH()))) return true;

        for (Multiplier m : multiplier)
            if (r.intersects(new Rectangle(m.getX(), m.getY(), m.getW(), m.getH()))) return true;

        return false;
    }

    /**
     * Evita oggetti troppo vicini in verticale nella stessa corsia.
     */
    private boolean laneTooCrowded(Rectangle r) {
        int rx = r.x;
        int lw = laneWidth();

        // stesso "colonna/corsia" se x è simile
        java.util.function.Predicate<Rectangle> near = other -> {
            boolean sameLane = Math.abs(other.x - rx) < lw / 2;
            boolean tooCloseY = Math.abs(other.y - r.y) < MIN_VERTICAL_GAP;
            return sameLane && tooCloseY;
        };

        for (Obstacle o : obstacles)
            if (near.test(new Rectangle(o.getX(), o.getY(), o.getW(), o.getH()))) return true;

        for (Coin c : coins)
            if (near.test(new Rectangle(c.getX(), c.getY(), c.getW(), c.getH()))) return true;

        for (Bonus b : bonus)
            if (near.test(new Rectangle(b.getX(), b.getY(), b.getW(), b.getH()))) return true;

        for (Multiplier m : multiplier)
            if (near.test(new Rectangle(m.getX(), m.getY(), m.getW(), m.getH()))) return true;

        return false;
    }

    private void spawnObstacle() {
        int ow = 50, oh = 70;

        for (int t = 0; t < MAX_SPAWN_TRIES; t++) {
            int lane = rnd.nextInt(LANES);
            int x = laneCenterX(lane, ow);
            int y = -oh;

            Rectangle r = new Rectangle(x, y, ow, oh);
            if (!overlapsSomething(r) && !laneTooCrowded(r)) {
                obstacles.add(new Obstacle(x, y, ow, oh));
                return;
            }
        }
        // se non trova spazio, salta questo spawn
    }

    private void spawnCoin() {
        int cw = 26, ch = 26;

        for (int t = 0; t < MAX_SPAWN_TRIES; t++) {
            int lane = rnd.nextInt(LANES);
            int x = laneCenterX(lane, cw);
            int y = -ch;

            Rectangle r = new Rectangle(x, y, cw, ch);
            if (!overlapsSomething(r) && !laneTooCrowded(r)) {
                coins.add(new Coin(x, y, cw, ch, 1));
                return;
            }
        }
    }

    private void spawnBonus() {
        int bw = 26, bh = 26;

        for (int t = 0; t < MAX_SPAWN_TRIES; t++) {
            int lane = rnd.nextInt(LANES);
            int x = laneCenterX(lane, bw);
            int y = -bh;

            Rectangle r = new Rectangle(x, y, bw, bh);
            if (!overlapsSomething(r) && !laneTooCrowded(r)) {
                bonus.add(new Bonus(x, y, bw, bh));
                return;
            }
        }
    }

    private void spawnMultiplier() {
        int mw = 26, mh = 26;

        for (int t = 0; t < MAX_SPAWN_TRIES; t++) {
            int lane = rnd.nextInt(LANES);
            int x = laneCenterX(lane, mw);
            int y = -mh;

            Rectangle r = new Rectangle(x, y, mw, mh);
            if (!overlapsSomething(r) && !laneTooCrowded(r)) {
                multiplier.add(new Multiplier(x, y, mw, mh)); // ✅ GIUSTO: lista multiplier
                return;
            }
        }
    }

    // =========================================================
    // =================== COLLISIONI & BONUS ===================
    // =========================================================

    private void checkCollisions(VehicleCustomizationModel wallet, PlayerProfileModel profile) {
        long now = System.currentTimeMillis();
        Rectangle pb = player.getBounds();

        // Ostacoli
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            Rectangle ob = new Rectangle(o.getX(), o.getY(), o.getW(), o.getH());

            if (pb.intersects(ob)) {
                if (now < invulnerableUntilMs) return;

                lives--;
                obstacles.remove(i);
                invulnerableUntilMs = now + INVULN_MS;

                if (lives <= 0) {
                    state = State.GAME_OVER;
                    String name = (profile != null) ? profile.getPlayerName() : "Player";

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
            Rectangle cb = new Rectangle(c.getX(), c.getY(), c.getW(), c.getH());

            if (pb.intersects(cb)) {
                if (wallet != null) wallet.addCoins(c.getValue());
                coinsCollectedThisRun += c.getValue();
                it.remove();
            }
        }

        // Bonus speed
        Iterator<Bonus> bon = bonus.iterator();
        while (bon.hasNext()) {
            Bonus b = bon.next();
            Rectangle bb = new Rectangle(b.getX(), b.getY(), b.getW(), b.getH());

            if (pb.intersects(bb)) {
                activateSpeedBonus();
                bon.remove();
            }
        }

        // Bonus score x2
        Iterator<Multiplier> x2 = multiplier.iterator();
        while (x2.hasNext()) {
            Multiplier m = x2.next();
            Rectangle mb = new Rectangle(m.getX(), m.getY(), m.getW(), m.getH());

            if (pb.intersects(mb)) {
                activateScoreBonus();
                x2.remove();
            }
        }
    }

    private void activateSpeedBonus() {
        long now = System.currentTimeMillis();
        bonusUntilMs = now + BONUS_DURATION;
        player.setSpeedMultiplier(2.0);
        isBonusActive = true;
    }

    private void activateScoreBonus() {
        long now = System.currentTimeMillis();
        scoreBonusUntilMs = now + BONUS_DURATION;
        isX2Active = true;
    }

    // =========================================================
    // ========================= GETTERS ========================
    // =========================================================

    public boolean isX2Active() { return isX2Active; }
    public boolean isBonusActive() { return isBonusActive; }

    public boolean isInvulnerable() {
        return System.currentTimeMillis() < invulnerableUntilMs;
    }

    public State getState() { return state; }
    public Player getPlayer() { return player; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<Coin> getCoins() { return coins; }
    public List<Bonus> getBonus() { return bonus; }
    public List<Multiplier> getMultiplier() { return multiplier; }
    public int getScoreSeconds() { return scoreSeconds; }
    public int getCurrentFallSpeed() { return currentFallSpeed; }
    public int getCoinsCollectedThisRun() { return coinsCollectedThisRun; }
    public int getLives() { return lives; }
}
