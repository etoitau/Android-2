package com.etoitau.coinrun.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etoitau.coinrun.domain.Armory;
import com.etoitau.coinrun.domain.Background;
import com.etoitau.coinrun.domain.Bank;
import com.etoitau.coinrun.domain.Blast;
import com.etoitau.coinrun.domain.Coin;
import com.etoitau.coinrun.domain.Dog;
import com.etoitau.coinrun.domain.Sign;
import com.etoitau.coinrun.domain.Bomb;


public class CoinRun extends ApplicationAdapter {
	// Objects from the domain
	private Sign sign;
	private Background bg;
	private Dog dog;
	private Bank bank;
	private Armory armory;
	private Blast blast;
	// gamestate and geometry variables
	private int screenWidth, screenHeight, score = 0, standHeight, gameState, highScore = 0;
	// geometry constants
	private final int DOG_MARGIN = 75, FONT_X = 50, FONT_Y = 50,
	// game states
            PRE_START = 0, RUNNING = 1, WIND_DOWN = 2, REPORT = 3;
	private double v, a; // running speed and acceleration
	private static double START_V, START_A = 10; // starting speed and acceleration
	// GDX elements
	private BitmapFont scoreFont, reportFont;
	private SpriteBatch batch;


	@Override
	public void create () {
		batch = new SpriteBatch();
		// many elements are adjusted for screen size
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		// calibrate scroll speed to screen width
		START_V = screenWidth / 3;
		v = START_V;
		a = START_A;
		// set up background
		Texture bgTexture = new Texture("desert_bg.png");
		bgTexture = getResizedTexture(bgTexture,
				bgTexture.getWidth() * screenHeight / bgTexture.getHeight(), screenHeight);
		bg = new Background();
		bg.setTexture(bgTexture);
		// set up dog
		dog = new Dog(DOG_MARGIN, screenHeight / 7);
		standHeight = dog.getFrame().getHeight();
		// set up objects for managing coins and bombs
		bank = new Bank(screenWidth, dog);
		armory = new Armory(screenWidth, dog);
		// set up sign
		sign = new Sign(screenWidth / 2, screenWidth - 75,
				3 * screenHeight / 4, screenHeight / 4);

		// set up fonts
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(8);

		reportFont = new BitmapFont();
		reportFont.setColor(Color.BLACK);
		reportFont.getData().setScale(6);

		// game waiting to start
		gameState = PRE_START;
	}


	/**
	 * Called continuously, this is the game loop
	 */
	@Override
	public void render () {
		batch.begin();
		// run loop appropriate to each game state
		switch (gameState) {
            case (PRE_START):
                preStartLoop();
                break;
            case (RUNNING):
                runningLoop();
                break;
            case (WIND_DOWN):
                windDownLoop();
                break;
            case (REPORT):
                reportLoop();
                break;
            default:
                // nothing
        }
		batch.end();
	}

	// if game hasn't started yet, just draw static textures and wait for tap
	private void preStartLoop() {
	    draw();
	    if (Gdx.input.justTouched()) {
	        gameState = RUNNING;
        }
    }

    // if game is running, draw elements, update scroll speed,
	// update all game elements, and check for jumps or collisions
    private void runningLoop() {
	    draw();
        // how much time since last render?
        double dt = Gdx.graphics.getDeltaTime();
        // adjust speed
        v += dt * a;
        if (v < 0) {
            v = 0;
            a = 0;
        }
        // update game elements
        updates(dt, v);
        checkGameEvents();
    }

    // if hit a bomb, wind things down waiting for report
	// note checkGameEvents has cleared bombs and coins and reversed acceleration to slow down
    private void windDownLoop() {
		// draw elements
	    draw();
        // how much time since last render?
        double dt = Gdx.graphics.getDeltaTime();
        // adjust speed
        v += dt * a;
        if (v < 0) {
            v = 0;
            a = 0;
        }
        // update game elements
        updates(dt, v);
        if (Gdx.input.justTouched()) {
            gameState = REPORT;
        }
    }

    // at end of game show a message
    private void reportLoop() {
		// draw elements
		draw();
		// draw the sign
        batch.draw(sign.getSignTexture(), sign.getLeft(), sign.getBot());
        // put together the message
        StringBuilder sb = new StringBuilder("Game Over\nTap to restart");
        if (score > highScore) {
        	sb.append("\nNew high score!\nOld high score: ");
        	sb.append(highScore);
		} else {
        	sb.append("\nHigh score: ").append(highScore);
		}
        // write message
        reportFont.draw(batch, sb.toString(), sign.getLeft() + 25, sign.getTop() - 25);
        // if user taps, reset everything
        if (Gdx.input.justTouched()) {
            gameState = PRE_START;
            dog.revive();
            v = START_V;
            a = START_A;
            score = 0;
            blast = null;
        }
    }


	@Override
	public void dispose () {
		batch.dispose();
	}

	// a tool for loading a bunch of assets into a Texture array for animation
	public static Texture[] loadFrames(String pre, int frames, String suf) {
		Texture[] result = new Texture[frames];
		for (int i = 0; i < frames; i++) {
			result[i] = new Texture(pre + (i + 1) + suf);
		}
		return result;
	}

	// a tool for resizing a texture
	public static Texture getResizedTexture(Texture source, int newWidth, int newHeight) {
		// https://www.snip2code.com/Snippet/774713/LibGDX-Resize-texture-on-load
		// https://stackoverflow.com/a/29451932/11517662
		if (!source.getTextureData().isPrepared()) {
			source.getTextureData().prepare();
		}
		Pixmap pix1 = source.getTextureData().consumePixmap();
		Pixmap pix2 = new Pixmap(newWidth, newHeight, pix1.getFormat());
		pix2.drawPixmap(pix1,
				0, 0, pix1.getWidth(), pix1.getHeight(),
				0, 0, pix2.getWidth(), pix2.getHeight()
		);
		Texture result = new Texture(pix2);
		pix1.dispose();
		pix2.dispose();
		return result;
	}

	private void draw() {
		// draw everything
		batch.draw(bg.getTexture(), bg.getX1(), 0);
		batch.draw(bg.getTexture(), bg.getX2(), 0);
		batch.draw(bg.getTexture(), bg.getX3(), 0);
		Texture dogFrame = dog.getFrame();
		// note using top of dog as reference point since it's constant during animation
		batch.draw(dogFrame, dog.getX(), dog.getY() + (standHeight - dogFrame.getHeight()));
		for (Coin coin: bank.getCoins()) {
			batch.draw(coin.getFrame(), coin.getX() - coin.getFrame().getWidth() / 2f, coin.getY());
		}
		for (Bomb bomb : armory.getBombs()) {
			batch.draw(bomb.getFrame(), bomb.getX(), bomb.getY());
		}
		scoreFont.draw(batch, String.valueOf(score), FONT_X, FONT_Y + scoreFont.getXHeight());
		// only draw bomb explosion animation if available
		if (gameState == WIND_DOWN && blast != null) {
			Texture boom = blast.getFrame();
			if (boom != null) {
				batch.draw(boom, blast.getX(), blast.getY());
			}
		}
	}

	private void updates(double dt, double v) {
		// update background
		bg.update(dt, v);

		// update dog
		dog.update(dt);

		if (gameState == RUNNING) {
            // update coins
            bank.update(dt, v);

            // update bombs
            armory.update(dt, v);
        }

		// update blast
		if (blast != null && gameState == WIND_DOWN) {
			blast.update(dt);
		}
	}

	private void checkGameEvents() {
		// tell dog to jump if possible if user touched
		if (Gdx.input.justTouched()) {
			dog.jump();
		}

		// see if collected a coin, bank will remove coin if needed
		if (bank.checkCollision(dog.getRectangle())) {
			score++;
		}

		// see if hit a bomb, armory will remove bomb if needed
		Bomb hitBomb = armory.checkCollision(dog.getRectangle());
		if (hitBomb != null) {
			gameState = WIND_DOWN;
			// set acceleration so will come to a stop quickly
			a = -1 * v;
			// set up a blast to animate
			blast = new Blast(hitBomb.getX(), hitBomb.getY());
			dog.daze();
			// clear coins and bombs
			bank.reset();
			armory.reset();
			// check/update high score
			checkHighScore();
		}
	}

	// storing high score in preferences
	private void checkHighScore() {
		Preferences prefs = Gdx.app.getPreferences("High Score");
		highScore = prefs.getInteger("score", 0);
		if (score > highScore) {
			prefs.putInteger("score", score);
			prefs.flush();
		}
	}
}
