package view;

import model.VehicleCustomizationModel;
import model.VehicleSkin;

import utils.Config;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

// view del menù personalizzazione veicolo
public class VehicleCustomizationPanel extends JPanel {
    public final JButton backButton = new JButton("← Menu");

    private final JLabel coinsLabel = new JLabel();
    private final JLabel extraLivesLabel = new JLabel();
    private final JLabel extraLifeCostLabel = new JLabel();

    private final VehicleSkin[] skins = VehicleSkin.values();
    private final JComboBox<String> skinCombo = new JComboBox<>();

    public final JButton buySelectButton = new JButton("Compra / Seleziona");
    public final JButton buyExtraLifeButton = new JButton("Compra Vita Extra");

    public VehicleCustomizationPanel() {
        setLayout(new BorderLayout());

        // destra: pannello vuoto (bilancia il layout)
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setPreferredSize(backButton.getPreferredSize());
        
        JLabel title = new JLabel("Personalizzazione Veicolo", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(backButton);
        top.add(right);
        top.add(title);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.setOpaque(false);
        
        coinsLabel.setFont(UiConstants.UI_FONT);
        extraLivesLabel.setFont(UiConstants.UI_FONT);
        extraLifeCostLabel.setFont(UiConstants.UI_FONT);
        
        coinsLabel.setForeground(Color.WHITE);
        extraLivesLabel.setForeground(Color.WHITE);
        extraLifeCostLabel.setForeground(Color.WHITE);
        
        Dimension pref = skinCombo.getPreferredSize();
        skinCombo.setMaximumSize(
            new Dimension(UiConstants.GAME_W, pref.height)
        );

        skinCombo.setFont(UiConstants.UI_FONT);
        buySelectButton.setFont(UiConstants.UI_FONT);
        buyExtraLifeButton.setFont(UiConstants.UI_FONT);

        center.add(coinsLabel);
        center.add(Box.createVerticalStrut(12));

        JLabel txtSkin = new JLabel("Seleziona skin (costo in monete):");
        txtSkin.setForeground(Color.WHITE);
        center.add(txtSkin);
        reloadSkinComboItems();
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
        add(center, BorderLayout.CENTER);
    }

    // crea la stringa associando alla skin il relativo costo
    private void reloadSkinComboItems() {
        String[] items = new String[skins.length];

        for (int i = 0; i < skins.length; i++) {
            VehicleSkin skin = skins[i];
            int cost = Config.getInstance().getSkinCost(skin.name());
            items[i] = skin.name() + " - (" + cost + ")";
        }

        skinCombo.setModel(new DefaultComboBoxModel<>(items));
    }

    // Restituisce l'oggetto di tipo VehicleSkin corrispondente a quello selezionato in skinCombo
    public VehicleSkin getSelectedSkin() {
        String sel = (String) skinCombo.getSelectedItem();
        if (sel == null) return null;

        int idx = sel.indexOf(" - ");
        String skinName = (idx >= 0) ? sel.substring(0, idx) : sel;

        return VehicleSkin.valueOf(skinName);
    }


    // crea label monete
    public void updateCoins(VehicleCustomizationModel model) {
        coinsLabel.setText("Monete disponibili: " + model.getCoins());
    }

    // crea label vite extra
    public void updateExtraLives(VehicleCustomizationModel model) {
        extraLivesLabel.setText("Vite extra acquistate: " + model.getExtraLives());
        extraLifeCostLabel.setText("Costo vita extra: " + model.getExtraLifeCost() + " monete");
    }

    //metodo per caricare correttamente immagini png
    private BufferedImage readPngFromClasspath(String resourcePath) {
        try {
            if (resourcePath == null) return null;

            return ImageIO.read(
                    getClass().getResourceAsStream(resourcePath)
            );
        } catch (Exception e) {
            return null;
        }
    }

    // sfondo
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage leaderboardBackground =
            readPngFromClasspath("/assets/settings_background.png");

        if (leaderboardBackground != null) {
            g.drawImage(leaderboardBackground,0, 0,
                UiConstants.WINDOW_SIZE, UiConstants.WINDOW_SIZE, this
            );
        }
    }

}
