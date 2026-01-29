package view;

import model.VehicleCustomizationModel;
import model.VehicleSkin;

import javax.swing.*;
import java.awt.*;

/**
 * Menu personalizzazione veicolo:
 * - mostra monete totali
 * - permette selezione/compra skin
 * - permette comprare upgrade vite extra (permanente)
 */
public class VehicleCustomizationPanel extends JPanel {
    public final JButton backButton = new JButton("← Menu");

    private final JLabel coinsLabel = new JLabel();
    private final JLabel extraLivesLabel = new JLabel();
    private final JLabel extraLifeCostLabel = new JLabel();

    private final JComboBox<VehicleSkin> skinCombo = new JComboBox<>(VehicleSkin.values());
    public final JButton buySelectButton = new JButton("Compra / Seleziona");

    public final JButton buyExtraLifeButton = new JButton("Compra Vita Extra");

    public VehicleCustomizationPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Personalizzazione Veicolo", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(backButton);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        coinsLabel.setFont(UiConstants.UI_FONT);
        extraLivesLabel.setFont(UiConstants.UI_FONT);
        extraLifeCostLabel.setFont(UiConstants.UI_FONT);
        
        skinCombo.setFont(UiConstants.UI_FONT);
        buySelectButton.setFont(UiConstants.UI_FONT);
        buyExtraLifeButton.setFont(UiConstants.UI_FONT);

        center.add(coinsLabel);
        center.add(Box.createVerticalStrut(12));

        center.add(new JLabel("Seleziona skin (costo in monete):"));
        center.add(skinCombo);
        center.add(Box.createVerticalStrut(12));
        center.add(buySelectButton);

        center.add(Box.createVerticalStrut(18));
        center.add(extraLivesLabel);
        center.add(Box.createVerticalStrut(8));
        center.add(buyExtraLifeButton);
        center.add(Box.createVerticalStrut(6));
        center.add(extraLifeCostLabel);
        
        add(top, BorderLayout.NORTH);
        add(title, BorderLayout.CENTER);
        add(center, BorderLayout.SOUTH);
    }

    public VehicleSkin getSelectedSkin() {
        return (VehicleSkin) skinCombo.getSelectedItem();
    }

    public void updateCoins(VehicleCustomizationModel model) {
        coinsLabel.setText("Monete disponibili: " + model.getCoins());
    }

    public void updateExtraLives(VehicleCustomizationModel model) {
        extraLivesLabel.setText("Vite extra acquistate: " + model.getExtraLives());
        extraLifeCostLabel.setText("Costo vita extra: " + model.getExtraLifeCost() + " monete");
    }

}
