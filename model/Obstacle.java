package model;

import java.awt.Rectangle;

/**
 * Ostacolo: collisione => perde vita (o game over se vite finite).
 */
public class Obstacle extends FallingObject {

    private static final double HITBOX_SCALE = 0.75;

    public Obstacle(int x, int y, int w, int h) {
        super(x, y, w, h);
    }
    
    public Rectangle getBounds() {
        int hitW = (int) (w * HITBOX_SCALE);
        int hitH = (int) (h * HITBOX_SCALE);

        int hitX = x + (w - hitW) / 2;
        int hitY = y + (h - hitH) / 2;

        return new Rectangle(hitX, hitY, hitW, hitH);
    }
}
