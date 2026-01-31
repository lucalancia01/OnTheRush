package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.*;

/**
 * Schermata di gioco: disegna HUD, oggetti e player.
 * - Sprite PNG per ostacoli/monete/player
 * - Sfondo GIF animata (ImageIcon)
 * - Overlay pausa con pulsanti
 */
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


    // ===== Sfondo animato da PNG =====
    private int bgFrameIndex = 0;
    private long lastBgFrameTime = 0;
    private static final int BG_FRAME_DELAY = 70; // ms (≈14 fps)

    // ===== Background animation =====
    private BufferedImage[] bgFrames;
    private int bgIndex = 0;


    // Sprites statici
    private BufferedImage imgCar;
    private BufferedImage imgCoin;
    private BufferedImage imgBonus;
    private BufferedImage imgx2;
    private BufferedImage imgRiderDefault;
    private BufferedImage imgRiderRed;
    private BufferedImage imgRiderBlue;
    private BufferedImage imgRiderGold;

    //costruttore
    public GamePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(UiConstants.WINDOW_W, UiConstants.WINDOW_H));
        setFocusable(true);

        backButton.setBounds(10, 10, 100, 30);
        add(backButton);

        setupPauseOverlay();
        loadSprites();
        loadBackgroundFrames();

    }

    //posiziona elementi menù pausa
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

    /**
     * Centra il pannello di pausa nella finestra (GamePanel).
     * Viene chiamato ogni repaint e quando cambia la dimensione.
     */
    private void centerPauseOverlay() {
        int pw = pauseOverlay.getPreferredSize().width;
        int ph = pauseOverlay.getPreferredSize().height;

        int x = (getWidth() - pw) / 2;
        int y = (getHeight() - ph) / 2;

        pauseOverlay.setBounds(x, y, pw, ph);
    }
    
    //Carica risorse da /resources/... Se una risorsa manca, resta null e si useranno le forme
    private void loadSprites() {
        imgCar = safeReadPng(getAbsolutePath ("/resources/car.png"));
        imgCoin = safeReadPng(getAbsolutePath ("/resources/coin.png"));
        imgBonus = safeReadPng(getAbsolutePath ("/resources/bonus.png"));
        imgx2 = safeReadPng(getAbsolutePath ("/resources/x2.png"));
        imgRiderDefault = safeReadPng(getAbsolutePath ("/resources/rider_default.png"));
        imgRiderRed = safeReadPng(getAbsolutePath ("/resources/rider_red.png"));
        imgRiderBlue = safeReadPng(getAbsolutePath ("/resources/rider_blue.png"));
        imgRiderGold = safeReadPng(getAbsolutePath ("/resources/rider_gold.png"));
    }


    

    //carica Frames
    private void loadBackgroundFrames() {
    try {
        File dir = new File("resources/bg");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));

        if (files == null || files.length == 0) return;

        Arrays.sort(files, Comparator.comparing(File::getName));

        bgFrames = new BufferedImage[files.length];
        for (int i = 0; i < files.length; i++) {
            bgFrames[i] = ImageIO.read(files[i]);
        }
    }   catch (IOException e) {
        bgFrames = null;
        }
    }



    //aggiorna frame png a sfondo
    private void drawAnimatedBackground(Graphics2D g2) {
    if (bgFrames == null || bgFrames.length == 0) {
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, UiConstants.GAME_W, UiConstants.GAME_H);
        return;
    }

    long now = System.currentTimeMillis();
    if (now - lastBgFrameTime >= BG_FRAME_DELAY) {
        bgFrameIndex = (bgFrameIndex + 1) % bgFrames.length;
        lastBgFrameTime = now;
    }

    BufferedImage frame = bgFrames[bgFrameIndex];
    g2.drawImage(frame, 0, 0, UiConstants.GAME_W, UiConstants.GAME_H, null);
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
    
    
    /**
     * Collega model e wallet (monete/skin).
     */
    public void bind(GameModel model, VehicleCustomizationModel wallet, PlayerProfileModel profileModel) {
        this.gameModel = model;
        this.wallet = wallet;
        this.skin = wallet.getOwnedSkin();
    }

    /**
     * Richiamato quando cambia la skin nel menu veicolo.
     */
    public void refreshSkin(VehicleCustomizationModel wallet) {
        this.skin = wallet.getOwnedSkin();
    }

    //
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

    //disegna le componenti grafiche

    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    centerPauseOverlay();

    if (gameModel == null || bgFrames == null || bgFrames.length == 0) return;

    Graphics2D g2 = (Graphics2D) g.create();

    // 1) Bordo nero letterboxing
    g2.setColor(Color.BLACK);
    g2.fillRect(0, 0, getWidth(), getHeight());

    // 2) Disegna sfondo proporzionato e centrato
    BufferedImage bg = bgFrames[bgFrameIndex];
    int gifW = bg.getWidth();
    int gifH = bg.getHeight();

    double scaleX = (double) getWidth() / gifW;
    double scaleY = (double) getHeight() / gifH;
    double scale = Math.min(scaleX, scaleY);

    int drawW = (int) (gifW * scale);
    int drawH = (int) (gifH * scale);
    int drawX = (getWidth() - drawW) / 2;
    int drawY = (getHeight() - drawH) / 2;

    g2.drawImage(bg, drawX, drawY, drawW, drawH, null);

    // 3) Calcola offset per centrare game world
    int offX = (getWidth() - UiConstants.GAME_W) / 2;
    int offY = (getHeight() - UiConstants.GAME_H) / 2;

    // 4) Contesto grafico per game world
    g2.translate(offX, offY);
    g2.setClip(0, 0, UiConstants.GAME_W, UiConstants.GAME_H);

    // 5) Disegna area di gioco di base se sfondo non sufficiente
    g2.setColor(new Color(30, 30, 30));
    g2.fillRect(0, 0, UiConstants.GAME_W, UiConstants.GAME_H);

    // 7) Ostacoli
    for (Obstacle o : gameModel.getObstacles()) {
        if (imgCar != null) {
            g2.drawImage(imgCar, o.getX(), o.getY(), o.getW(), o.getH(), null);
        } else {
            g2.setColor(new Color(200, 70, 70));
            g2.fillRect(o.getX(), o.getY(), o.getW(), o.getH());
        }
    }

    // 8) Monete
    for (Coin c : gameModel.getCoins()) {
        if (imgCoin != null) {
            g2.drawImage(imgCoin, c.getX(), c.getY(), c.getW(), c.getH(), null);
        } else {
            g2.setColor(new Color(240, 220, 50));
            g2.fillOval(c.getX(), c.getY(), c.getW(), c.getH());
        }
    }

    // 7) Bonus
    for (Bonus b : gameModel.getBonus()) {
        if (imgBonus != null) {
            g2.drawImage(imgBonus, b.getX(), b.getY(), b.getW(), b.getH(), null);
        } else {
            g2.setColor(Color.BLUE);
            g2.fillOval(b.getX(), b.getY(), b.getW(), b.getH());
        }
    }

    // 7) Multiplier
    for (Multiplier m : gameModel.getMultiplier()) {
        if (imgx2 != null) {
            g2.drawImage(imgx2, m.getX(), m.getY(), m.getW(), m.getH(), null);
        } else {
            g2.setColor(Color.GREEN);
            g2.fillOval(m.getX(), m.getY(), m.getW(), m.getH());
        }
    }


    // 9) Player con effetto blink invulnerabile
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

    // 10) Game over
    if (gameModel.getState() == GameModel.State.GAME_OVER) {
        g2.setFont(UiConstants.TITLE_FONT);
        g2.setColor(Color.WHITE);
        g2.drawString("GAME OVER", 140, 280);
        g2.setFont(UiConstants.UI_FONT);
        g2.drawString("Premi R per ripartire", 160, 320);
        g2.drawString("Premi P per pausa/menu", 150, 350);
    }

    g2.dispose();

    // 11) Aggiorna frame sfondo animato
    long now = System.currentTimeMillis();
    if (now - lastBgFrameTime > BG_FRAME_DELAY) {
        bgFrameIndex = (bgFrameIndex + 1) % bgFrames.length;
        lastBgFrameTime = now;
        repaint();
    }
 
    int totalCoins = wallet.getCoins();
    int runCoins = gameModel.getCoinsCollectedThisRun();
    
    Graphics2D hud = (Graphics2D) g.create();
    hud.setColor(Color.WHITE);
    hud.fillRect(0, 0, getWidth(),50);

    hud.setColor(Color.BLACK);
    hud.setFont(UiConstants.UI_FONT);

    int x = 12;
    int y = 30; // baseline testo dentro la barra

    hud.drawString("Vite: " + gameModel.getLives(), x + 500, y);
    hud.drawString("Score: " + gameModel.getScoreSeconds(), x + 140, y);
    hud.drawString("Monete: " + runCoins + " (Tot: " + totalCoins + ")", x + 280, y);

    int centerY = getHeight() / 2;
    int lineGap = 34; // distanza tra le righe

    if (gameModel.isBonusActive()) {
        drawBonusText(hud, "PLAYER SPEED x2!", centerY - lineGap);
    }

    if (gameModel.isX2Active()) {
        drawBonusText(hud, "SCORE x2", centerY + lineGap);
    }

    hud.dispose();
}

    private void drawCenteredText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (getWidth() - textWidth) / 2;
        g.drawString(text, x, y);
    }

    private void drawBonusText(Graphics2D g, String text, int y) {
        g.setColor(Color.YELLOW);
        g.setFont(UiConstants.UI_FONT.deriveFont(Font.BOLD, 28f));
        drawCenteredText(g, text, y);
    }
}
//versione 30/01/26