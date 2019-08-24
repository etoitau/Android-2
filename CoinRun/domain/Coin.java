package com.etoitau.coinrun.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.etoitau.coinrun.game.CoinRun;

/**
 * Coin object. Player is trying to collect these
 */
public class Coin {
    private double x, y, v, aniclock = 0; // position, speed, and time since last animation frame
    private final double FRAME_RATE = 1/12d; // animation frame rate
    private Texture[] spinAni; // animation frames
    private int spinStep = 0; // current frame index
    private final int COIN_SCALE = 2; // scale source image up by this factor


    public Coin(double x, double y, double v) {
        this.x = x;
        this.y = y;
        this.v = v; // will be scroll speed at time created

        spinAni = CoinRun.loadFrames("coin_0", 8, ".png");

        for (int i = 0; i < spinAni.length; i++) {
            spinAni[i] = CoinRun.getResizedTexture(spinAni[i],
                    spinAni[i].getWidth() * COIN_SCALE,
                    spinAni[i].getHeight() * COIN_SCALE);
        }

    }

    // runs every game loop
    public void update(double dt) {
        x -= v * dt; // position changes by speed * time

        // time for animation frame to change?
        aniclock += dt;
        if (aniclock > FRAME_RATE) {
            spinStep++;
            aniclock -= FRAME_RATE;
        }
        // if at end of animation, loop
        if (spinStep == spinAni.length) {
            spinStep = 0;
        }
    }

    public Texture getFrame() {
        return spinAni[spinStep];
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    // for collision detection
    public Rectangle getRectangle() {
        Texture current = getFrame();
        return new Rectangle((float) (x - current.getWidth() / 2d), (float) y, current.getWidth(), current.getHeight());
    }
}
