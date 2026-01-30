package controller;

import model.SettingsModel;
import model.VehicleCustomizationModel;
import model.VehicleSkin;
import view.GeneralCustomizationPanel;
import view.VehicleCustomizationPanel;

import javax.swing.*;

/**
 * Controller per i menu di personalizzazione:
 * - skin veicolo
 * - upgrade vite extra
 * - impostazioni (audio)
 *
 * Nota MVC: qui aggiorniamo SOLO le view di menu.
 * La skin del gioco viene aggiornata quando si entra nel Game (in AppController).
 */
public class CustomizationController {
    private final VehicleCustomizationModel vehicleModel;
    private final SettingsModel settingsModel;

    private final VehicleCustomizationPanel vehicleView;
    private final GeneralCustomizationPanel settingsView;

    public CustomizationController(VehicleCustomizationModel vehicleModel,
                                   SettingsModel settingsModel,
                                   VehicleCustomizationPanel vehicleView,
                                   GeneralCustomizationPanel settingsView) {
        this.vehicleModel = vehicleModel;
        this.settingsModel = settingsModel;
        this.vehicleView = vehicleView;
        this.settingsView = settingsView;

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

            if (!ok) {
                JOptionPane.showMessageDialog(vehicleView,
                        "Monete insufficienti per " + selected.name() + " (costo: " + selected.getCost() + ")",
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
            // 1) reset nel file
            utils.Config.getInstance().resetUiAndSettingsToDefaults();

            // 2) riallinea model e view (sound subito)
            settingsModel.setSoundEnabled(utils.Config.getInstance().isSoundEnabled());
            settingsView.soundCheck.setSelected(settingsModel.isSoundEnabled());

            // alternativa (feedback più forte):
            JOptionPane.showMessageDialog (settingsView, 
                "Impostazioni ripristinate.\nLe modifiche saranno effettive al prossimo avvio.",
                "OK", 
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        // Impostazioni audio
        settingsView.bind(settingsModel);
        settingsView.soundCheck.addActionListener(e -> {
            settingsModel.setSoundEnabled(settingsView.soundCheck.isSelected());
        });
    }
}
