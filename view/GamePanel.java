package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.*;

// Schermata di gioco
public class GamePanel extends JPanel {
    private GameModel gameModel;
    private VehicleCustomizationModel wallet;
    private VehicleSkin skin = VehicleSkin.DEFAULT;

    public final JButton backButton = new JButton("← Menu");

    // Overlay pausa
    public final JPanel pauseOverlay = new JPanel();
    public final JButton resumeButton = new JButton("Riprendi");
    public final JButton toStartButton = new JButton("Schermata iniziale");
    public final JButton toVehicleButton = new JButton("Menu veicolo");
    public final JButton toSettingsButton = new JButton("Impostazioni");
    
    // Status bar
    private final JPanel statusBarPanel = new JPanel(new BorderLayout());
    public final JButton pauseButton = new JButton("Pausa");
    private final JLabel livesLabel = new JLabel("Vite: 0");
    private final JLabel scoreLabel = new JLabel("Score: 0");
    private final JLabel coinsLabel = new JLabel("Monete: 0");

    // Sprites statici
    private BufferedImage imgCar;
    private BufferedImage imgCoin;
    private BufferedImage imgBonus;
    private BufferedImage imgx2;
    private BufferedImage imgRiderDefault;
    private BufferedImage imgRiderRed;
    private BufferedImage imgRiderBlue;
    private BufferedImage imgRiderGold;
    
    // Sfondo animato da PNG
    private int bgFrameIndex = 0;
    private static final int BG_FRAME_DELAY = 70; // 14 fps
    private BufferedImage[] bgFrames;
    private Timer bgTimer;

    public GamePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(UiConstants.WINDOW_SIZE, UiConstants.WINDOW_SIZE));
        setFocusable(true);

        setupStatusBarPanel();
        setupPauseOverlay();
        loadSprites();
        loadBackgroundFrames();
        setupBackgroundTimer();

    }

    // costruisce la status bar e la aggiunge in alto
    private void setupStatusBarPanel() {
        statusBarPanel.setBackground(Color.WHITE);
        statusBarPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        backButton.setFont(UiConstants.UI_FONT);
        pauseButton.setFont(UiConstants.UI_FONT);

        // Centro: labels
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        center.setOpaque(false);

        livesLabel.setFont(UiConstants.UI_FONT);
        scoreLabel.setFont(UiConstants.UI_FONT);
        coinsLabel.setFont(UiConstants.UI_FONT);

        center.add(scoreLabel);
        center.add(coinsLabel);
        center.add(livesLabel);

        statusBarPanel.add(backButton, BorderLayout.WEST);
        statusBarPanel.add(center, BorderLayout.CENTER);
        statusBarPanel.add(pauseButton, BorderLayout.EAST);

        add(statusBarPanel);
    }

    // Regola l'animazione dello schermo attraverso un timer
    private void setupBackgroundTimer() {
        bgTimer = new Timer(BG_FRAME_DELAY, e -> {
            if (bgFrames == null || bgFrames.length == 0) return;
            if (gameModel == null) return;

            // STOP se pausa o se game over
            boolean stopped = pauseOverlay.isVisible()
                    || gameModel.getState() == GameModel.State.GAME_OVER;

            if (stopped) return;

            bgFrameIndex = (bgFrameIndex + 1) % bgFrames.length;
            repaint();
        });
        bgTimer.start();
    }

    // Crea il layout per gli elementi secondari
    @Override
    public void doLayout() {
        super.doLayout();

        statusBarPanel.setBounds(0, 0, getWidth(), 50);
        centerPauseOverlay();
    }

    // Aggiorna i label nella status bar
    private void updateStatusBarTexts() {
        if (gameModel == null || wallet == null) return;

        int totalCoins = wallet.getCoins();
        int runCoins = gameModel.getCoinsCollectedThisRun();

        livesLabel.setText("Vite: " + gameModel.getLives());
        scoreLabel.setText("Score: " + gameModel.getScoreSeconds());
        coinsLabel.setText("Monete: " + runCoins + " (Tot: " + totalCoins + ")");
    }

    // posiziona elementi menù pausa
    private void setupPauseOverlay(){
        pauseOverlay.setPreferredSize(new Dimension(300, 260));
        pauseOverlay.setLayout(new GridLayout(5, 1, 10, 10));
        pauseOverlay.setBackground(new Color(0, 0, 0, 180));

        JLabel paused = new JLabel("PAUSA", SwingConstants.CENTER);
        paused.setForeground(Color.WHITE);
        paused.setFont(UiConstants.TITLE_FONT);

        resumeButton.setFont(UiConstants.UI_FONT);
        toStartButton.setFont(UiConstants.UI_FONT);
        toVehicleButton.setFont(UiConstants.UI_FONT);
        toSettingsButton.setFont(UiConstants.UI_FONT);

        pauseOverlay.add(paused);
        pauseOverlay.add(resumeButton);
        pauseOverlay.add(toStartButton);
        pauseOverlay.add(toVehicleButton);
        pauseOverlay.add(toSettingsButton);

        pauseOverlay.setVisible(false);
        add(pauseOverlay);
    }

    // Centra il pannello di pausa nella finestra
    private void centerPauseOverlay() {
        int pw = pauseOverlay.getPreferredSize().width;
        int ph = pauseOverlay.getPreferredSize().height;

        int x = (getWidth() - pw) / 2;
        int y = (getHeight() - ph) / 2;

        pauseOverlay.setBounds(x, y, pw, ph);
    }
    
    //Carica risorse grafiche
    private void loadSprites() {
        imgCar = readPngFromClasspath("/assets/car.png");
        imgCoin = readPngFromClasspath("/assets/coin.png");
        imgBonus = readPngFromClasspath("/assets/bonus.png");
        imgx2 = readPngFromClasspath("/assets/x2.png");
        imgRiderDefault = readPngFromClasspath("/assets/rider_default.png");
        imgRiderRed = readPngFromClasspath("/assets/rider_red.png");
        imgRiderBlue = readPngFromClasspath("/assets/rider_blue.png");
        imgRiderGold = readPngFromClasspath("/assets/rider_gold.png");
    }

    //carica Frames sfondo
    private void loadBackgroundFrames() {
        try {
            int frameCount = 17; // numero di frame
            bgFrames = new BufferedImage[frameCount];

            for (int i = 0; i < frameCount; i++) {
                String path = String.format("/assets/bg/frame_%03d.png", i + 1);

                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    bgFrames = null;
                    return;
                }

                bgFrames[i] = ImageIO.read(is);
            }
        } catch (IOException e) {
            bgFrames = null;
        }
    }

    // Metodo per caricare correttamente le immagini dal classpath
    private BufferedImage readPngFromClasspath(String resourcePath) {
        try {
            if (resourcePath == null) return null;

            // path tipo "/assets/leaderboard_background.png"
            return ImageIO.read(
                    getClass().getResourceAsStream(resourcePath)
            );
        } catch (Exception e) {
            return null;
        }
    }

    // Collega model e wallet (monete/skin)
    public void bind(GameModel model, VehicleCustomizationModel wallet, PlayerProfileModel profileModel) {
        this.gameModel = model;
        this.wallet = wallet;
        this.skin = wallet.getOwnedSkin();
    }

    // Richiamato quando cambia la skin nel menu veicolo.
    public void refreshSkin(VehicleCustomizationModel wallet) {
        this.skin = wallet.getOwnedSkin();
    }

    // Associa la corretta skin al giocatore
    private BufferedImage riderForSkin() {
        switch (skin) {
            case RED:
                return imgRiderRed;
            case BLUE:
                return imgRiderBlue;
            case GOLD:
                return imgRiderGold;
            case DEFAULT:
            default:
                return imgRiderDefault;
        }
    }

    //disegna le componenti grafiche del gioco
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateStatusBarTexts();

        if (gameModel == null) return; // solo questo deve bloccare

        // 1) bordi neri (letterboxing)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 2) calcolo offset per centrare il game world (480x720)
        int offX = (getWidth() - UiConstants.GAME_W) / 2;
        int offY = (getHeight() - UiConstants.GAME_H) / 2;

        // 3) crea un contesto grafico separato e trasla
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(offX, offY);

        // 4) limita il disegno all'area di gioco
        g2.setClip(0, 0, UiConstants.GAME_W, UiConstants.GAME_H);
        
        // 5) Sfondo: se ho i frame li disegno, altrimenti fallback a tinta unita
        if (bgFrames != null && bgFrames.length > 0) {
            BufferedImage bg = bgFrames[bgFrameIndex];

            g.drawImage(bg, 0, 0, UiConstants.WINDOW_SIZE, UiConstants.WINDOW_SIZE, null);
        } else {
            // fallback: sfondo semplice
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 6) Ostacoli
        for (Obstacle o : gameModel.getObstacles()) {
            if (imgCar != null) {
                g2.drawImage(imgCar, o.getX(), o.getY(), o.getW(), o.getH(), null);
            } else {
                g2.setColor(new Color(200, 70, 70));
                g2.fillRect(o.getX(), o.getY(), o.getW(), o.getH());
            }
        }

        // 7) Monete
        for (Coin c : gameModel.getCoins()) {
            if (imgCoin != null) {
                g2.drawImage(imgCoin, c.getX(), c.getY(), c.getW(), c.getH(), null);
            } else {
                g2.setColor(new Color(240, 220, 50));
                g2.fillOval(c.getX(), c.getY(), c.getW(), c.getH());
            }
        }

        // 8) Bonus
        for (Bonus b : gameModel.getBonus()) {
            if (imgBonus != null) {
                g2.drawImage(imgBonus, b.getX(), b.getY(), b.getW(), b.getH(), null);
            } else {
                g2.setColor(Color.BLUE);
                g2.fillOval(b.getX(), b.getY(), b.getW(), b.getH());
            }
        }

        // 9) Multiplier
        for (Multiplier m : gameModel.getMultiplier()) {
            if (imgx2 != null) {
                g2.drawImage(imgx2, m.getX(), m.getY(), m.getW(), m.getH(), null);
            } else {
                g2.setColor(Color.GREEN);
                g2.fillOval(m.getX(), m.getY(), m.getW(), m.getH());
            }
        }

        // 10) Player con effetto blink invulnerabile
        Player p = gameModel.getPlayer();
        boolean blink = gameModel.isInvulnerable() && ((System.currentTimeMillis() / 120) % 2 == 0);
        if (!blink) {
            BufferedImage rider = riderForSkin();
            if (rider != null) {
                g2.drawImage(rider, p.getX(), p.getY(), p.getW(), p.getH(), null);
            } else {
                g2.setColor(new Color(70, 160, 240));
                g2.fillRoundRect(p.getX(), p.getY(), p.getW(), p.getH(), 12, 12);
            }
        }

        // 11) BONUS SPEED
        if (gameModel.isBonusActive()) {
            drawBonusText(g2, "PLAYER SPEED x2!", (UiConstants.GAME_H / 2) - 30);
        }   

        // 12)BONUS PUNTEGGIO
        if (gameModel.isX2Active()) {
            drawBonusText(g2, "SCORE x2", (UiConstants.GAME_H / 2) + 30);
        }

        // 13) Game over
        if (gameModel.getState() == GameModel.State.GAME_OVER) {
            int boxW = 420;
            int boxH = 180;
            int boxX = (UiConstants.GAME_W - boxW) / 2;
            int boxY = (UiConstants.GAME_H - boxH) / 2;

            g2.setColor(new Color(0, 0, 0, 220));
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);

            g2.setColor(Color.WHITE);
            g2.setFont(UiConstants.TITLE_FONT);
            drawCenteredTextInGameWorld(g2, "GAME OVER", boxY + 60);

            g2.setFont(UiConstants.UI_FONT);
            drawCenteredTextInGameWorld(g2, "Premi R per ripartire", boxY + 105);
            drawCenteredTextInGameWorld(g2, "Premi P per pausa/menu", boxY + 135);
        }

        g2.dispose();
    }

    // Testo bonus centrato
    private void drawCenteredTextInGameWorld(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (UiConstants.GAME_W - textWidth) / 2;
        g.drawString(text, x, y);
    }

    // Imposta le stringhe di testo dei bonus
    private void drawBonusText(Graphics2D g, String text, int y) {
        g.setFont(UiConstants.UI_FONT.deriveFont(Font.BOLD, 26f));

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (UiConstants.GAME_W - textWidth) / 2;

        g.setColor(Color.YELLOW);
        g.drawString(text, x, y);
    }

}
//versione 30/01/26