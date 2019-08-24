package com.etoitau.coinrun.domain;

import com.badlogic.gdx.graphics.Texture;
import com.etoitau.coinrun.game.CoinRun;

/**
 * A blast appears when dog hits a bomb
 */
public class Blast {
    private double x, y, aniclock = 0; // screen position and time since bomb went off
    private final double FRAME_RATE = 1/12d; // time between new animation frames
    private Texture[] blastAni; // animation frames
    private int blastStep = 0; // current frame index
    private final int BLAST_SCALE = 2; // scale up source image by this factor

    public Blast(double x, double y) {
        this.x = x;
        this.y = y;

        blastAni = CoinRun.loadFrames("blast", 3, ".png");

        for (int i = 0; i < blastAni.length; i++) {
            blastAni[i] = CoinRun.getResizedTexture(blastAni[i],
                    blastAni[i].getWidth() * BLAST_SCALE,
                    blastAni[i].getHeight() * BLAST_SCALE);
        }

    }

    // runs every relevant game loop, progressing animation
    public void update(double dt) {
        aniclock += dt;
        if (aniclock > FRAME_RATE) {
            blastStep++;
            aniclock -= FRAME_RATE;
        }
    }

    // return current animation frame, unless over already
    public Texture getFrame() {
        if (blastStep < blastAni.length) {
            return blastAni[blastStep];
        } else {
            return null;
        }
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }
}
