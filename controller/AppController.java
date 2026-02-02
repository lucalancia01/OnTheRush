package controller;

import model.*;
import view.*;

public class AppController {

    private MainFrame frame;

    // Views
    private StartPanel startPanel;
    private GamePanel gamePanel;
    private VehicleCustomizationPanel vehiclePanel;
    private GeneralCustomizationPanel settingsPanel;
    private LeaderboardPanel leaderboardPanel;
    private AudioModel audio;

    // Models
    private GameModel gameModel;
    private VehicleCustomizationModel walletModel;
    private SettingsModel settingsModel;
    private LeaderboardModel leaderboardModel;
    private PlayerProfileModel profileModel;

    // Controllers
    private NavigationController nav;
    private GameController gameController;
    private CustomizationController customizationController;

    public void start() {

        // ===== MODELS =====
        walletModel = new VehicleCustomizationModel();
        settingsModel = new SettingsModel();
        leaderboardModel = new LeaderboardModel();
        profileModel = new PlayerProfileModel();

        gameModel = new GameModel(UiConstants.GAME_W, UiConstants.GAME_H);

        // prima di caricare audio: inizializza settingsModel
        settingsModel = new SettingsModel();
        settingsModel.setSoundEnabled(utils.Config.getInstance().isSoundEnabled()); // oppure come lo leggi tu

        //carica audio
        try {
            audio = new AudioModel("MenuMusic.wav","GameplayMusic.wav");

            // NON play diretto: usa la funzione che rispetta soundEnabled
            syncMusicWithScreen(MainFrame.START);

        } catch (Exception ex) {
            ex.printStackTrace();
            audio = null;
        }

        // ===== VIEWS =====
        frame = new MainFrame();
        startPanel = new StartPanel();
        gamePanel = new GamePanel();
        vehiclePanel = new VehicleCustomizationPanel();
        settingsPanel = new GeneralCustomizationPanel();
        leaderboardPanel = new LeaderboardPanel();

        gamePanel.bind(gameModel, walletModel, profileModel);

        // ===== CONTROLLERS =====
        nav = new NavigationController(frame);
        gameController = new GameController(gameModel, walletModel, gamePanel);
        customizationController =
                new CustomizationController(walletModel, settingsModel, vehiclePanel, settingsPanel, audio);

        // Pre-carica nome giocatore
        startPanel.nameField.setText(profileModel.getPlayerName());

        // ===== SCREENS =====
        frame.addScreen(MainFrame.START, startPanel);
        frame.addScreen(MainFrame.GAME, gamePanel);
        frame.addScreen(MainFrame.VEHICLE, vehiclePanel);
        frame.addScreen(MainFrame.SETTINGS, settingsPanel);
        frame.addScreen(MainFrame.LEADERBOARD, leaderboardPanel);

        wireButtons();

        frame.setVisible(true);
        nav.goTo(MainFrame.START);
    }

    // metodo per semplificare la gestione audio nello switch tra due schermate
    private void syncMusicWithScreen(String screenKey) {
        if (audio == null) return;
        
        // Se il suono è disabilitato, non deve partire nulla
        if (!settingsModel.isSoundEnabled()) {
            audio.pause();   // oppure audio.setMuted(true)
            return;
        }

        if (MainFrame.GAME.equals(screenKey)) {
            audio.play(AudioModel.Track.B);
        } else {
            audio.play(AudioModel.Track.A);
        }
    }

    private void wireButtons() {

        // START -> GAME
        startPanel.playButton.addActionListener(e -> {
            profileModel.setPlayerName(startPanel.nameField.getText());
            syncMusicWithScreen(MainFrame.GAME);
            nav.goTo(MainFrame.GAME);
            gamePanel.refreshSkin(walletModel);
            gameController.startGame(profileModel);
            gamePanel.requestFocusInWindow();
        });

        // START -> VEHICLE
        startPanel.vehicleButton.addActionListener(e -> {
            vehiclePanel.updateCoins(walletModel);
            vehiclePanel.updateExtraLives(walletModel);
            syncMusicWithScreen(MainFrame.VEHICLE);
            nav.goTo(MainFrame.VEHICLE);
        });

        // START -> SETTINGS
        startPanel.settingsButton.addActionListener(e -> {
            settingsPanel.bind(settingsModel);
            syncMusicWithScreen(MainFrame.SETTINGS);
            nav.goTo(MainFrame.SETTINGS);
        });

        // START -> LEADERBOARD
        startPanel.leaderboardButton.addActionListener(e -> {
            leaderboardPanel.setEntries(leaderboardModel.getTop10());
            syncMusicWithScreen(MainFrame.LEADERBOARD);
            nav.goTo(MainFrame.LEADERBOARD);
        });

        // LEADERBOARD MANAGMENT
        leaderboardPanel.backButton.addActionListener(e -> {
            syncMusicWithScreen(MainFrame.START);
            nav.goTo(MainFrame.START);
        });

        leaderboardPanel.clearButton.addActionListener(e -> {
            int res = javax.swing.JOptionPane.showConfirmDialog(
                    leaderboardPanel,
                    "Vuoi davvero cancellare la leaderboard?",
                    "Conferma",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );
            if (res == javax.swing.JOptionPane.YES_OPTION) {
                utils.Config.getInstance().resetLeaderboard();
                leaderboardPanel.setEntries(leaderboardModel.getTop10());
            }
        });

        // GAME -> START
        gamePanel.backButton.addActionListener(e -> {
            gameController.stopGame();
            syncMusicWithScreen(MainFrame.START);
            nav.goTo(MainFrame.START);
        });

        // GAME -> PAUSE
        gamePanel.pauseButton.addActionListener(e -> {
            gameController.togglePause();
            gamePanel.requestFocusInWindow();
        });

        // PAUSA -> RESUME
        gamePanel.resumeButton.addActionListener(e -> {
            gameController.togglePause();
            gamePanel.requestFocusInWindow();
        });

        gamePanel.toStartButton.addActionListener(e -> {
            gameController.stopGame();
            syncMusicWithScreen(MainFrame.START);
            nav.goTo(MainFrame.START);
        });

        gamePanel.toVehicleButton.addActionListener(e -> {
            gameController.stopGame();
            vehiclePanel.updateCoins(walletModel);
            vehiclePanel.updateExtraLives(walletModel);
            syncMusicWithScreen(MainFrame.VEHICLE);
            nav.goTo(MainFrame.VEHICLE);
        });

        gamePanel.toSettingsButton.addActionListener(e -> {
            gameController.stopGame();
            settingsPanel.bind(settingsModel);
            syncMusicWithScreen(MainFrame.SETTINGS);
            nav.goTo(MainFrame.SETTINGS);
        });

        // VEHICLE -> START
        vehiclePanel.backButton.addActionListener(e -> {
            syncMusicWithScreen(MainFrame.START);
            nav.goTo(MainFrame.START);
        });

        // SETTINGS -> START
        settingsPanel.backButton.addActionListener(e -> {
            syncMusicWithScreen(MainFrame.START);
            nav.goTo(MainFrame.START);
        });
    }
}
