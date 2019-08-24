package com.etoitau.coinrun.domain;

import com.badlogic.gdx.graphics.Texture;

/**
 * Object helps with drawing scrolling background
 * does so by keeping three copies and running them like a conveyor belt
 */
public class Background {
    private Texture texture; // base texture to tile
    private int width; // width of background tile
    private double x1, x2, x3; // positions of three copies


    public void setTexture(Texture texture) {
        this.texture = texture;
        this.width = texture.getWidth();
        this.x1 = 10 - width;
        this.x2 = x1 + width;
        this.x3 = x2 + width;
    }

    public Texture getTexture() {
        return this.texture;
    }

    // runs every active game loop
    public void update(double dt, double v) {
        double delta = dt * v; // distance to move = time * speed

        // if first copy will be totally off screen, second copy becomes first
        if (x1 - delta < -1 * width) {
            x1 = x2 - delta;
        } else {
            x1 -= delta;
        }
        x2 = x1 + width;
        x3 = x2 + width;
    }

    public int getX1() {
        return (int) Math.round(x1);
    }

    public int getX2() {
        return (int) Math.round(x2);
    }

    public int getX3() {
        return (int) Math.round(x3);
    }
}
