package model;

public class Bonus extends FallingObject {
        private final int value;

    public Bonus(int x, int y, int w, int h, int value) {
        super(x, y, w, h);
        this.value = value;
       
    }

public int getValue() { return value; }
    
}
