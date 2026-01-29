package model;

/**
 * Moneta: collisione => incrementa monete totali + monete run.
 */
public class Coin extends FallingObject {
    private final int value;

    public Coin(int x, int y, int w, int h, int value) {
        super(x, y, w, h);
        this.value = value;
    }

    public int getValue() { return value; }
}
