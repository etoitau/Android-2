package com.etoitau.coinrun.domain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

// Something on which we can write at end of game
public class Sign {
    private int left, right, top, bot, width, height;
    private Texture signTexture;

    public Sign(int left, int right, int top, int bot) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bot = bot;
        width = right - left;
        height = top - bot;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixmap.setColor(Color.BLACK);
        // pixmap y = 0 at top
        // top border
        pixmap.fillRectangle(0, 0, width, height / 50);
        // right border
        pixmap.fillRectangle(width - width / 20, 0, width / 20, height);
        // bot border
        pixmap.fillRectangle(0, height - height / 20, width, height / 20);
        // left border
        pixmap.fillRectangle(0, 0, width / 50, height);
        signTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public Texture getSignTexture() {
        return signTexture;
    }

    public int getLeft() {
        return left;
    }

    public int getBot() {
        return bot;
    }

    public int getTop() {
        return top;
    }
}
