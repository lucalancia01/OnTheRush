package model;

import utils.Config;

/**
 * Profilo giocatore: salva/legge il nome dal Config unico.
 */
public class PlayerProfileModel {
    private final Config cfg = Config.getInstance();
    private String playerName;

    public PlayerProfileModel() {
        this.playerName = cfg.getPlayerName();
    }

    public String getPlayerName() {
        return playerName;
    }

    /**
     * Salva nome nel file unico di config.
     */
    public void setPlayerName(String name) {
        cfg.setPlayerName(name);
        cfg.save();
        this.playerName = cfg.getPlayerName();
    }
}
