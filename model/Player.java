package model;

import java.awt.Rectangle;

/**
 * Player che si muove solo orizzontalmente.
 */
public class Player {
    private int x, y, w, h;
    private int speed = 8;

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

    public int getX() { return x; }
    public int getY() { return y; }
    public int getW() { return w; }
    public int getH() { return h; }
    public int getSpeed() {return speed; }
}
