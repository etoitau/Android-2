package com.etoitau.coinrun.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Collects and manages Bombs
 */
public class Armory {
    private LinkedList<Bomb> bombs; // list of bombs, acts like a queue
    private final int START_X, LOW_MAX_Y, LOW_MIN_Y, HIGH_MAX_Y, HIGH_MIN_Y; // regions where they should appear
    private Random random;
    private final double TNT_TIME = 2.5; // time between bomb appearances
    private double tntTimer = 0; // time since last bomb generated


    public Armory(int startX, Dog dog) {
        this.START_X = startX;

        // bomb will either appear low to jump over, or high to run under
        // if low, should be between standing level and jump height
        this.LOW_MIN_Y = dog.getFLOOR();
        this.LOW_MAX_Y = (int) Math.round(2 * dog.getJUMP_POWER() * dog.getJUMP_POWER() / dog.getG());
        // if high, should be between over head height and head height at top of jump
        this.HIGH_MIN_Y = dog.getFLOOR() + dog.getFrame().getHeight() + 10;
        this.HIGH_MAX_Y = (int) Math.round(2 * dog.getJUMP_POWER() * dog.getJUMP_POWER() / dog.getG()) + dog.getFrame().getHeight() / 2;

        this.random = new Random();
        this.bombs = new LinkedList<>();
    }

    // when time for a new bomb, generate low or high randomly
    public void addBomb(double v) {
        double y;
        if (random.nextBoolean()) {
            y = random.nextDouble() * (LOW_MAX_Y - LOW_MIN_Y) + LOW_MIN_Y;
        } else {
            y = random.nextDouble() * (HIGH_MAX_Y - HIGH_MIN_Y) + HIGH_MIN_Y;
        }
        bombs.addLast(new Bomb(START_X, y, v));
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    // any bombs collide with provided hitbox?
    public Bomb checkCollision(Rectangle hitBox) {
        int i = 0;
        boolean result = false;
        Bomb touchedBomb = null;
        // check all bombs, if one hits, return it, else return null
        while (i < bombs.size()) {
            if (Intersector.overlaps(bombs.get(i).getRectangle(), hitBox)) {
                touchedBomb = bombs.get(i);
                result = true;
                break;
            }
            i++;
        }
        if (result) {
            bombs.remove(i);
        }
        return touchedBomb;
    }

    // runs every active game loop
    public void update(double dt, double v) {
        if (!bombs.isEmpty()) {
            // if off screen, remove. Works like queue, so just check head
            if (bombs.getFirst().getX() + bombs.getFirst().getFrame().getWidth() < 0) {
                bombs.removeFirst();
            }
            // update all bomb objects
            for (Bomb bomb: bombs) {
                bomb.update(dt);
            }
        }
        // time to add new bomb?
        tntTimer += dt;
        if (tntTimer > TNT_TIME && v > 1) {
            tntTimer -= TNT_TIME;
            addBomb(v);
        }
    }

    public void reset() {
        bombs.clear();
        tntTimer = 0;
    }
}
