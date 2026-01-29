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
                new CustomizationController(walletModel, settingsModel, vehiclePanel, settingsPanel);

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

    private void wireButtons() {

        // START -> GAME
        startPanel.playButton.addActionListener(e -> {
            profileModel.setPlayerName(startPanel.nameField.getText());
            nav.goTo(MainFrame.GAME);
            gamePanel.refreshSkin(walletModel);
            gameController.startGame(profileModel);
            gamePanel.requestFocusInWindow();
        });

        // START -> VEHICLE
        startPanel.vehicleButton.addActionListener(e -> {
            vehiclePanel.updateCoins(walletModel);
            vehiclePanel.updateExtraLives(walletModel);
            nav.goTo(MainFrame.VEHICLE);
        });

        // START -> SETTINGS
        startPanel.settingsButton.addActionListener(e -> {
            settingsPanel.bind(settingsModel);
            nav.goTo(MainFrame.SETTINGS);
        });

        // START -> LEADERBOARD
        startPanel.leaderboardButton.addActionListener(e -> {
            leaderboardPanel.setEntries(leaderboardModel.getTop10());
            nav.goTo(MainFrame.LEADERBOARD);
        });

        leaderboardPanel.backButton.addActionListener(
                e -> nav.goTo(MainFrame.START)
        );

        // GAME -> START
        gamePanel.backButton.addActionListener(e -> {
            gameController.stopGame();
            nav.goTo(MainFrame.START);
        });

        // PAUSA -> RESUME
        gamePanel.resumeButton.addActionListener(e -> {
            gameController.togglePause();
            gamePanel.requestFocusInWindow();
        });

        gamePanel.toStartButton.addActionListener(e -> {
            gameController.stopGame();
            nav.goTo(MainFrame.START);
        });

        gamePanel.toVehicleButton.addActionListener(e -> {
            gameController.stopGame();
            vehiclePanel.updateCoins(walletModel);
            vehiclePanel.updateExtraLives(walletModel);
            nav.goTo(MainFrame.VEHICLE);
        });

        gamePanel.toSettingsButton.addActionListener(e -> {
            gameController.stopGame();
            settingsPanel.bind(settingsModel);
            nav.goTo(MainFrame.SETTINGS);
        });

        // VEHICLE -> START
        vehiclePanel.backButton.addActionListener(
                e -> nav.goTo(MainFrame.START)
        );

        // SETTINGS -> START
        settingsPanel.backButton.addActionListener(
                e -> nav.goTo(MainFrame.START)
        );
    }
}
