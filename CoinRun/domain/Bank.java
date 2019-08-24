package com.etoitau.coinrun.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Object for collecting and managing Coins
 */
public class Bank {
    private LinkedList<Coin> coins; // queue of coins
    private final int START_X, MAX_Y, MIN_Y; // range where coins should appear
    private Random random;
    private final double COIN_TIME = 3; // time between coin appearances
    private double coinTimer = 0; // time since last coin generated


    public Bank(int startX, Dog dog) {
        this.START_X = startX;
        // no lower than dog's standing level
        this.MIN_Y = dog.getFLOOR();
        // no higher than dog can reach jumping
        this.MAX_Y = (int) Math.round(2 * dog.getJUMP_POWER() * dog.getJUMP_POWER() / dog.getG()) + dog.getFrame().getHeight() / 2;
        this.random = new Random();
        this.coins = new LinkedList<>();
    }

    // generate a new coin randomly in range, add to end of queue
    public void addCoin(double v) {
        double y = random.nextDouble() * (MAX_Y - MIN_Y) + MIN_Y;
        coins.addLast(new Coin(START_X, y, v));
    }

    public List<Coin> getCoins() {
        return coins;
    }

    // see if any coins collide with given hitbox
    public boolean checkCollision(Rectangle hitBox) {
        int i = 0;
        boolean result = false;
        while (i < coins.size()) {
            if (Intersector.overlaps(coins.get(i).getRectangle(), hitBox)) {
                result = true;
                break;
            }
            i++;
        }
        // if one does collide, remove it
        if (result) {
            coins.remove(i);
        }
        return result;
    }

    // called every active game loop
    public void update(double dt, double v) {
        if (!coins.isEmpty()) {
            // if off screen, remove. Works like queue, so just check head
            if (coins.getFirst().getX() + coins.getFirst().getFrame().getWidth() < 0) {
                coins.removeFirst();
            }
            // update all coins
            for (Coin coin: coins) {
                coin.update(dt);
            }
        }
        // time to add new coin?
        coinTimer += dt;
        if (coinTimer > COIN_TIME && v > 1) {
            coinTimer -= COIN_TIME;
            addCoin(v);
        }
    }

    public void reset() {
        coins.clear();
        coinTimer = 0;
    }
}
