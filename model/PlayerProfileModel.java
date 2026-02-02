package model;

import utils.Config;

// regola il profilo giocatore
public class PlayerProfileModel {
    private final Config cfg = Config.getInstance();
    private String playerName;
    private boolean bonusState = false;

    public PlayerProfileModel() {
        this.playerName = cfg.getPlayerName();
    }

    // getters
    public String getPlayerName() {
        return playerName;
    }
    
    public Boolean getBonusState() {
        return bonusState;
    }

    public void setBonusStateTrue() {
        bonusState = true;
    }
    // Salva nome nel file config
    public void setPlayerName(String name) {
        cfg.setPlayerName(name);
        cfg.save();
        this.playerName = cfg.getPlayerName();
    }
}
