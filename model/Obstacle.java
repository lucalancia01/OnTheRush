package model;

/**
 * Ostacolo: collisione => perde vita (o game over se vite finite).
 */
public class Obstacle extends FallingObject {
    public Obstacle(int x, int y, int w, int h) {
        super(x, y, w, h);
    }
}
