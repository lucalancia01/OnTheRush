package model;

import utils.Config;

/**
 * Enum delle skin.
 * Il costo NON è hard-coded: viene letto dal Config unico:
 *   shop.skin.DEFAULT, shop.skin.RED, ...
 */
public enum VehicleSkin {
    DEFAULT,
    RED,
    BLUE,
    GOLD;

    /**
     * Ritorna il costo della skin leggendo dal file di configurazione.
     * Questo permette di cambiare i prezzi senza ricompilare.
     */
    public int getCost() {
        Config cfg = Config.getInstance();
        return cfg.getSkinCost(this.name());
    }
}
