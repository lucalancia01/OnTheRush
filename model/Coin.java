package model;

import java.awt.Rectangle;

/**
 * Moneta: collisione => incrementa monete totali + monete run.
 */
public class Coin extends FallingObject {
    private final int value;

    private static final double HITBOX_SCALE = 0.5;

    public Coin(int x, int y, int w, int h, int value) {
        super(x, y, w, h);
        this.value = value;
    }

    public Rectangle getBounds() {
        int hitW = (int) (w * HITBOX_SCALE);
        int hitH = (int) (h * HITBOX_SCALE);

        int hitX = x + (w - hitW) / 2;
        int hitY = y + (h - hitH) / 2;

        return new Rectangle(hitX, hitY, hitW, hitH);
    }

    public int getValue() { return value; }
}
