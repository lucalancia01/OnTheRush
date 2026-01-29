package view;

import model.SettingsModel;

import javax.swing.*;
import java.awt.*;

/**
 * Menu impostazioni generali (es. audio).
 */
public class GeneralCustomizationPanel extends JPanel {
    public final JButton backButton = new JButton("← Menu");
    public final JCheckBox soundCheck = new JCheckBox("Audio abilitato");

    public GeneralCustomizationPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Impostazioni", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(backButton);

        JPanel center = new JPanel();
        soundCheck.setFont(UiConstants.UI_FONT);
        center.add(soundCheck);

        add(top, BorderLayout.NORTH);
        add(title, BorderLayout.CENTER);
        add(center, BorderLayout.SOUTH);
    }

    /**
     * Mostra nella UI lo stato attuale delle impostazioni.
     */
    public void bind(SettingsModel settings) {
        soundCheck.setSelected(settings.isSoundEnabled());
    }
}
