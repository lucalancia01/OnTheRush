package model;

import utils.Config;

// Modello che gestisce monete totali, skin selezionata e upgrade vite extra
public class VehicleCustomizationModel {
    private final Config cfg = Config.getInstance();

    private int coins;
    private VehicleSkin ownedSkin;
    private int extraLives;

    public VehicleCustomizationModel() {
        this.coins = cfg.getPlayerCoins();
        this.ownedSkin = VehicleSkin.valueOf(cfg.getVehicleSkin());
        this.extraLives = cfg.getExtraLives();
    }

    // getters
    public int getCoins() { return coins; }
    public VehicleSkin getOwnedSkin() { return ownedSkin; }
    public int getExtraLives() { return extraLives; }

    // Legge il costo corrente dell'upgrade vita extra dal config
    public int getExtraLifeCost() {
        return cfg.getExtraLifeCost();
    }

    // aggiorna coins totali
    public void addCoins(int amount) {
        coins = Math.max(0, coins + amount);
        cfg.setPlayerCoins(coins);
        cfg.save();
    }

    // Acquista skin
    public boolean buyAndSelectSkin(VehicleSkin skin) {
        if (skin == null) return false;
        if (ownedSkin == skin) return true;

        int cost = skin.getCost();

        if (cost <= 0) {
            ownedSkin = skin;
            cfg.setVehicleSkin(ownedSkin.name());
            cfg.save();
            return true;
        }

        if (coins >= cost) {
            coins -= cost;
            ownedSkin = skin;

            cfg.setPlayerCoins(coins);
            cfg.setVehicleSkin(ownedSkin.name());
            cfg.save();
            return true;
        }

        return false;
    }

    // Compra vita extra
    public boolean buyExtraLifeUpgrade() {
        int cost = getExtraLifeCost();
        if (coins >= cost) {
            coins -= cost;
            extraLives += 1;

            cfg.setPlayerCoins(coins);
            cfg.setExtraLives(extraLives);
            cfg.save();
            return true;
        }
        return false;
    }
}
