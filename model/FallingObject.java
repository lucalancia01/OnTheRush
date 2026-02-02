package model;

import java.awt.Rectangle;

// Classe astratta, scheletro per gli oggetti che cadono
public abstract class FallingObject {
    protected int x, y, w, h;

    public FallingObject(int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public void moveDown(int dy) {
        y += dy;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getW() { return w; }
    public int getH() { return h; }
}
