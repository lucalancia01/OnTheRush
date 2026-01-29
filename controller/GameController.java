package controller;

import model.GameModel;
import model.PlayerProfileModel;
import model.VehicleCustomizationModel;
import view.GamePanel;
import view.UiConstants;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Controller del gioco:
 * - gestisce input da tastiera (dx/sx, pausa, restart)
 * - gestisce game loop tramite Swing Timer (~60 FPS)
 *
 * Importante: Swing Timer esegue su EDT; per un progetto universitario va bene.
 */
public class GameController {
    private final GameModel model;
    private final VehicleCustomizationModel wallet;
    private final GamePanel view;

    private PlayerProfileModel profile;

    private final Timer timer;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean paused = false;

    public GameController(GameModel model, VehicleCustomizationModel wallet, GamePanel view) {
        this.model = model;
        this.wallet = wallet;
        this.view = view;

        // 16ms ~ 60fps
        this.timer = new Timer(16, e -> tick());

        attachInput();
    }

    private void attachInput() {
        view.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;

                // Pausa: P oppure ESC
                if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }

                // Restart solo se GAME OVER
                if (e.getKeyCode() == KeyEvent.VK_R && model.getState() == GameModel.State.GAME_OVER) {
                    startGame(profile);
                }
            }

            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
            }
        });
    }

    /**
     * Avvia una partita. Le vite vengono calcolate in base agli upgrade nel model.
     */
    public void startGame(PlayerProfileModel profile) {
        this.profile = profile;

        paused = false;
        view.pauseOverlay.setVisible(false);

        model.start(wallet); // wallet contiene extraLives acquistate
        timer.start();

        // Fondamentale: il pannello deve avere focus per ricevere i tasti
        view.requestFocusInWindow();
    }

    /**
     * Ferma il loop (es. quando si esce dal gioco).
     */
    public void stopGame() {
        timer.stop();
        paused = false;
        view.pauseOverlay.setVisible(false);
    }

    /**
     * Mostra/nasconde overlay pausa e blocca gli update logici.
     */
    public void togglePause() {
        paused = !paused;
        view.pauseOverlay.setVisible(paused);
        view.requestFocusInWindow();
        view.repaint();
    }

    private void tick() {
        if (paused) {
            view.repaint();
            return;
        }

        // Movimento player solo in RUNNING
        if (model.getState() == GameModel.State.RUNNING) {
            if (leftPressed) model.getPlayer().moveLeft(0);
            if (rightPressed) model.getPlayer().moveRight(UiConstants.GAME_W);
        }

        // Update logico + repaint
        model.update(wallet, profile);
        view.repaint();
    }
}
