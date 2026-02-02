package controller;

import model.SettingsModel;
import model.VehicleCustomizationModel;
import model.VehicleSkin;
import model.LeaderboardModel;
import model.AudioModel;
import view.GeneralCustomizationPanel;
import view.VehicleCustomizationPanel;
import view.LeaderboardPanel;

import javax.swing.*;

// Gestione dei menu di personalizzazione e leaderboard
public class CustomizationController {
    private final VehicleCustomizationModel vehicleModel;
    private final SettingsModel settingsModel;
    private final LeaderboardModel leaderboardModel;

    private final VehicleCustomizationPanel vehicleView;
    private final GeneralCustomizationPanel settingsView;
    private final LeaderboardPanel leaderboardView;

    private AudioModel audio;

    public CustomizationController(VehicleCustomizationModel vehicleModel,
                                   SettingsModel settingsModel,
                                   LeaderboardModel leaderboardModel,
                                   VehicleCustomizationPanel vehicleView,
                                   GeneralCustomizationPanel settingsView,
                                   LeaderboardPanel leaderboardView,
                                    AudioModel audio) {
        this.vehicleModel = vehicleModel;
        this.settingsModel = settingsModel;
        this.leaderboardModel = leaderboardModel;
        this.vehicleView = vehicleView;
        this.settingsView = settingsView;
        this.leaderboardView = leaderboardView;
        this.audio = audio; 

        bind();
    }

    private void bind() {
        vehicleView.updateCoins(vehicleModel);
        vehicleView.updateExtraLives(vehicleModel);

        // Compra/seleziona skin
        vehicleView.buySelectButton.addActionListener(e -> {
            VehicleSkin selected = vehicleView.getSelectedSkin();
            boolean ok = vehicleModel.buyAndSelectSkin(selected);

            vehicleView.updateCoins(vehicleModel);

            int cost = utils.Config.getInstance().getSkinCost(selected.name());

            if (!ok) {
                JOptionPane.showMessageDialog(vehicleView,
                        "Monete insufficienti per " + selected.name() + " (costo: " + cost + ")",
                        "Acquisto non riuscito",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vehicleView,
                        "Skin selezionata: " + vehicleModel.getOwnedSkin(),
                        "OK",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Compra upgrade vita extra
        vehicleView.buyExtraLifeButton.addActionListener(e -> {
            int cost = vehicleModel.getExtraLifeCost();
            
            boolean ok = vehicleModel.buyExtraLifeUpgrade();
            vehicleView.updateCoins(vehicleModel);
            vehicleView.updateExtraLives(vehicleModel);

            if (!ok) {
                JOptionPane.showMessageDialog(vehicleView,
                        "Monete insufficienti (costo: " + cost + ")",
                        "Acquisto non riuscito",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vehicleView,
                        "Vita extra acquistata! Totale vite extra: " + vehicleModel.getExtraLives(),
                        "OK",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

         //riprisino impostazioni default
        settingsView.restoreDefaultsButton.addActionListener(e -> {
            // reset nel file
            utils.Config.getInstance().resetUiAndSettingsToDefaults();

            // riallinea model e view
            settingsModel.setSoundEnabled(utils.Config.getInstance().isSoundEnabled());
            settingsView.soundCheck.setSelected(settingsModel.isSoundEnabled());
            boolean enabled = settingsModel.isSoundEnabled();
            if (audio != null) audio.setMuted(!enabled);

            // Informa che le impostazioni sono state ripristinate
            JOptionPane.showMessageDialog (settingsView, 
                "Impostazioni ripristinate.\nLe modifiche saranno effettive al prossimo avvio.",
                "OK", 
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        // ripristino leaderboard
        leaderboardView.clearButton.addActionListener(e -> {
            int res = javax.swing.JOptionPane.showConfirmDialog(
                    leaderboardView,
                    "Vuoi davvero cancellare la leaderboard?",
                    "Conferma",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );
            if (res == javax.swing.JOptionPane.YES_OPTION) {
                utils.Config.getInstance().resetLeaderboard();
                leaderboardView.setEntries(leaderboardModel.getTop10());
            }
        });
        
        // Impostazioni audio
        settingsView.bind(settingsModel);
        settingsView.soundCheck.addItemListener(e -> {
        boolean enabled = settingsView.soundCheck.isSelected();
        settingsModel.setSoundEnabled(enabled);

        if (audio == null) return;

        if (!enabled) {
            audio.pause();
        } else {
            /*
            * quando riattivi, riparti dalla traccia "di menu"
            * (ci si troverà sempre nella schermata settings 
            * quando verà spuntata la checkbox)
            */
            audio.play(AudioModel.Track.A);
        }
        
        });

    }
}
