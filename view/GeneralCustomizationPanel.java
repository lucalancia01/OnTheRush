package view;

import model.SettingsModel;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

// Menu impostazioni generali
public class GeneralCustomizationPanel extends JPanel {
    public final JButton backButton = new JButton("← Menu");
    public final JButton restoreDefaultsButton = new JButton("Ripristina impostazioni");
    public final JCheckBox soundCheck = new JCheckBox("Audio abilitato");

    public GeneralCustomizationPanel() {
        setLayout(new BorderLayout());
 
        // destra: pannello vuoto (bilancia il layout)
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setPreferredSize(backButton.getPreferredSize());
        
        JLabel title = new JLabel("Impostazioni", SwingConstants.CENTER);
        title.setFont(UiConstants.TITLE_FONT);
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(backButton);
        top.add(right);
        top.add(title);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // centra orizzontalmente i componenti
        restoreDefaultsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundCheck.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(soundCheck);
        center.add(Box.createVerticalStrut(20));
        center.add(restoreDefaultsButton);
        center.add(Box.createVerticalGlue());

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
   
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

    // sfondo
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage customizationBackground = safeReadPng(getAbsolutePath ("/resources/settings_background.png"));
        if (customizationBackground != null) {
                 g.drawImage(customizationBackground, 0, 0, UiConstants.WINDOW_SIZE, UiConstants.WINDOW_SIZE, this);
            }
    }
    
    // Mostra se il JCheckBox è selezionato
    public void bind(SettingsModel settings) {
        soundCheck.setSelected(settings.isSoundEnabled());
    }

}
