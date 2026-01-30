package model;

import java.awt.Rectangle;

/**
 * Player che si muove solo orizzontalmente.
 */
public class Player {
    private int x, y, w, h;
    private int speed = 8;
    private double speedMultiplier = 1.0;

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
        return new Rectangle(x, y, w, h);
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
