package controller;

import model.*;
import view.*;

// Gestione start applicazione
public class AppController {

    // Frame
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

    // avvia l'applicazione
    public void start() {

        walletModel = new VehicleCustomizationModel();
        settingsModel = new SettingsModel();
        leaderboardModel = new LeaderboardModel();
        profileModel = new PlayerProfileModel();

        gameModel = new GameModel(UiConstants.GAME_W, UiConstants.GAME_H);

        // prima di caricare audio: inizializza settingsModel
        settingsModel = new SettingsModel();
        settingsModel.setSoundEnabled(utils.Config.getInstance().isSoundEnabled());

        //carica audio
        try {
            audio = new AudioModel("MenuMusic.wav","GameplayMusic.wav");
            syncMusicWithScreen(MainFrame.START);

        } catch (Exception ex) {
            ex.printStackTrace();
            audio = null;
        }

        // Views
        frame = new MainFrame();
        startPanel = new StartPanel();
        gamePanel = new GamePanel();
        vehiclePanel = new VehicleCustomizationPanel();
        settingsPanel = new GeneralCustomizationPanel();
        leaderboardPanel = new LeaderboardPanel();

        // associa wallet a player
        gamePanel.bind(gameModel, walletModel, profileModel);

        // Controllers
        nav = new NavigationController(frame);
        gameController = new GameController(gameModel, walletModel, gamePanel);
        customizationController =
                new CustomizationController(walletModel, settingsModel, leaderboardModel, vehiclePanel,
                     settingsPanel, leaderboardPanel, audio);

        // Pre-carica nome giocatore
        startPanel.nameField.setText(profileModel.getPlayerName());

        // Screens
        frame.addScreen(MainFrame.START, startPanel);
        frame.addScreen(MainFrame.GAME, gamePanel);
        frame.addScreen(MainFrame.VEHICLE, vehiclePanel);
        frame.addScreen(MainFrame.SETTINGS, settingsPanel);
        frame.addScreen(MainFrame.LEADERBOARD, leaderboardPanel);

        wireButtons();

        frame.setVisible(true);
        nav.goTo(MainFrame.START); //mostra la schermata iniziale
    }

    // metodo per semplificare la gestione audio nello switch tra due schermate
    private void syncMusicWithScreen(String screenKey) {
        if (audio == null) return;
        
        // Se il suono è disabilitato, non deve partire nulla
        if (!settingsModel.isSoundEnabled()) {
            audio.pause();
            return;
        }

        if (MainFrame.GAME.equals(screenKey)) {
            audio.play(AudioModel.Track.B);
        } else {
            audio.play(AudioModel.Track.A);
        }
    }

    // Contiene tutti i listener necessari allo spostamento tra le varie schermate
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

        // LEADERBOARD -> START
        leaderboardPanel.backButton.addActionListener(e -> {
            syncMusicWithScreen(MainFrame.START);
            nav.goTo(MainFrame.START);
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

        // PAUSE -> RESUME
        gamePanel.resumeButton.addActionListener(e -> {
            gameController.togglePause();
            gamePanel.requestFocusInWindow();
        });

        // PAUSE -> START
        gamePanel.toStartButton.addActionListener(e -> {
            gameController.stopGame();
            syncMusicWithScreen(MainFrame.START);
            nav.goTo(MainFrame.START);
        });

        // PAUSE -> VEHICLE
        gamePanel.toVehicleButton.addActionListener(e -> {
            gameController.stopGame();
            vehiclePanel.updateCoins(walletModel);
            vehiclePanel.updateExtraLives(walletModel);
            syncMusicWithScreen(MainFrame.VEHICLE);
            nav.goTo(MainFrame.VEHICLE);
        });

        // PAUSE -> SETTINGS
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
