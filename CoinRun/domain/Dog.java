package com.etoitau.coinrun.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.etoitau.coinrun.game.CoinRun;

/**
 * The player character
 * can jump and double jump
 * has animations for several modes
 */
public class Dog {
    private Texture[] runAni, jumpAni, fallAni, dazeAni; // animation frames for each mode
    private double x, y, vy = 0, aniClock = 0; // position, vertical speed, and time since last frame
    private final double FRAME_RATE = 1/24d, g = 1; // time between frames, and gravity (pixels/sec/sec)
    // current mode state, current animation frame index for each state
    private int mode, runStep = 0, jumpStep = 0, fallStep = 0, dazeStep = 0;
    // constants indicating each mode state
    private final int RUN = 0, JUMP = 1, FALL = 2, DAZE = 3, FLOOR, JUMP_POWER = 21;
    private boolean hasDouble = true; // can make a double jump?
    private final float SCALE = 0.5f;

    public Dog(int x, int floor) {
        this.FLOOR = floor;
        this.mode = RUN;
        this.x = x;
        this.y = floor;
        runAni = CoinRun.loadFrames("run", 8, ".png");
        jumpAni = CoinRun.loadFrames("jump", 4, ".png");
        fallAni = CoinRun.loadFrames("fall", 4, ".png");
        dazeAni = CoinRun.loadFrames("hurt", 2, ".png");
        for (int i = 0; i < runAni.length; i++) {
            runAni[i] = CoinRun.getResizedTexture(runAni[i],
                    Math.round(runAni[i].getWidth() * SCALE), Math.round(runAni[i].getHeight() * SCALE));
        }
        for (int i = 0; i < jumpAni.length; i++) {
            jumpAni[i] = CoinRun.getResizedTexture(jumpAni[i],
                    Math.round(jumpAni[i].getWidth() * SCALE), Math.round(jumpAni[i].getHeight() * SCALE));
        }
        for (int i = 0; i < fallAni.length; i++) {
            fallAni[i] = CoinRun.getResizedTexture(fallAni[i],
                    Math.round(fallAni[i].getWidth() * SCALE), Math.round(fallAni[i].getHeight() * SCALE));
        }
        for (int i = 0; i < dazeAni.length; i++) {
            dazeAni[i] = CoinRun.getResizedTexture(dazeAni[i],
                    Math.round(dazeAni[i].getWidth() * SCALE), Math.round(dazeAni[i].getHeight() * SCALE));
        }

    }

    // runs every game loop
    public void update(double dt) {
        // increment time since last animation frame change
        aniClock += dt;
        // depending on mode, progress animation and position/speed
        switch (mode) {
            case (RUN):
                if (aniClock > FRAME_RATE) {
                    runProgress();
                    aniClock -= FRAME_RATE;
                }
                break;
            case (JUMP):
                y += vy;
                vy -= g;
                if (vy < 0) {
                    mode = FALL;
                    resetSteps();
                }
                if (aniClock > FRAME_RATE) {
                    jumpProgress();
                    aniClock -= FRAME_RATE;
                }
                break;
            case (FALL):
                y += vy;
                vy -= g;
                if (y < FLOOR) {
                    y = FLOOR;
                    vy = 0;
                    mode = RUN;
                    resetSteps();
                    hasDouble = true;
                }
                if (aniClock > FRAME_RATE) {
                    fallProgress();
                    aniClock -= FRAME_RATE;
                }
                break;
            case (DAZE):
                if (y > FLOOR) {
                    y += vy;
                    vy -= g;
                } else if (y < FLOOR) {
                    y = FLOOR;
                    vy = 0;
                }
                if (aniClock > 10 * FRAME_RATE) {
                    dazeProgress();
                    aniClock -= 10 * FRAME_RATE;
                }
                break;
            default:
                // do nothing
        }
    }

    public Texture getFrame() {
        switch (mode) {
            case (RUN):
                return runAni[runStep];
            case (JUMP):
                return jumpAni[jumpStep];
            case (FALL):
                return  fallAni[fallStep];
            case (DAZE):
                return dazeAni[dazeStep];
            default:
                return runAni[0];
        }
    }

    // for collision detection
    public Rectangle getRectangle() {
        Texture current = getFrame();
        return new Rectangle((float) x, (float) y, current.getWidth(), current.getHeight());
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    // if user wants to jump. Can only jump if currently running,
    // or in the air with double jump available
    public void jump() {
        if (mode == RUN) {
            vy += JUMP_POWER;
            mode = JUMP;
            resetSteps();
        } else if ((mode == JUMP || mode == FALL) && hasDouble) {
            vy += JUMP_POWER;
            mode = JUMP;
            hasDouble = false;
            resetSteps();
        }
    }

    // running animation loop
    private void runProgress() {
        runStep++;
        runStep %= runAni.length;
    }

    // jumping animation
    private void jumpProgress() {
        jumpStep++;
        // if at end of animation, just alternate between last two frames
        if (jumpStep == jumpAni.length) {
            jumpStep -= 2;
        }
    }

    // falling animation
    private void fallProgress() {
        fallStep++;
        // if at end of loop, just stay at last frame
        if (fallStep == fallAni.length) {
            fallStep--;
        }
    }

    // dazed animation loop
    private void dazeProgress() {
        dazeStep++;
        dazeStep %= dazeAni.length;
    }

    // reset all animations
    private void resetSteps() {
        runStep = 0;
        jumpStep = 0;
        fallStep = 0;
        dazeStep = 0;
    }

    public double getG() {
        return g;
    }

    public int getFLOOR() {
        return FLOOR;
    }

    public int getJUMP_POWER() {
        return JUMP_POWER;
    }

    public void daze() {
        mode = DAZE;
    }

    // reset for a new run
    public void revive() {
        if (mode == DAZE) {
            mode = RUN;
            resetSteps();
            y = FLOOR;
            vy = 0;
            aniClock = 0;
            hasDouble = true;
        }
    }
}
