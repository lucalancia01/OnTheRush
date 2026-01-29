package model;

import utils.Config;

/**
 * Impostazioni generali (audio).
 */
public class SettingsModel {
    private final Config cfg = Config.getInstance();
    private boolean soundEnabled;

    public SettingsModel() {
        this.soundEnabled = cfg.isSoundEnabled();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        cfg.setSoundEnabled(enabled);
        cfg.save();
    }
}
