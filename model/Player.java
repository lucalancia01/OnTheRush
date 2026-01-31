package model;

import java.awt.Rectangle;

/**
 * Player che si muove solo orizzontalmente.
 */
public class Player {
    private int x, y, w, h;
    
    private int speed = 8;
    private double speedMultiplier = 1.0;

    private static final double HITBOX_SCALE = 0.7;

    public Player(int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public void moveLeft(int minX) {
        x = Math.max(minX, x - speed);
    }

    public void moveRight(int maxX) {
        x = Math.min(maxX - w, x + speed);
    }

    public Rectangle getBounds() {
        int hitW = (int) (w * HITBOX_SCALE);
        int hitH = (int) (h * HITBOX_SCALE);

        int hitX = x + (w - hitW) / 2;
        int hitY = y + (h - hitH) / 2;

        return new Rectangle(hitX, hitY, hitW, hitH);
    }

     public void setSpeedMultiplier(double m) {
        this.speedMultiplier = m;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getW() { return w; }
    public int getH() { return h; }

}
