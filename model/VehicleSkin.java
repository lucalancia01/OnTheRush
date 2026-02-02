package model;

import utils.Config;

public enum VehicleSkin {
    DEFAULT,
    RED,
    BLUE,
    GOLD;

    // Ritorna il costo della skin leggendo dal file di configurazione
    public int getCost() {
        Config cfg = Config.getInstance();
        return cfg.getSkinCost(this.name());
    }
}
