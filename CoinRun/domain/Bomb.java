package com.etoitau.coinrun.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.etoitau.coinrun.game.CoinRun;

/**
 * Bomb object. These are the game obstacles
 */
public class Bomb {
    private double x, y, v; // position and speed
    private Texture texture; // appearance
    private final float BOMB_SCALE = 0.75f; // scale source image by this


    public Bomb(double x, double y, double v) {
        this.x = x;
        this.y = y;
        this.v = v; // will be scroll speed at time bomb is created

        Texture temp = new Texture("tnt.png");
        texture = CoinRun.getResizedTexture(temp,
                Math.round(temp.getWidth() * BOMB_SCALE), Math.round(temp.getHeight() * BOMB_SCALE));
    }

    // each game loop, scroll to left
    public void update(double dt) {
        x -= v * dt; // position changes by speed * distance
    }

    public Texture getFrame() {
        return texture;
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    // for use in colision detection
    public Rectangle getRectangle() {
        return new Rectangle((float) x, (float) y, texture.getWidth(), texture.getHeight());
    }

}
