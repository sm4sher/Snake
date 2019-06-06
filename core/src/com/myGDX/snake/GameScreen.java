package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.awt.Point;

public class GameScreen extends ScreenAdapter {

	private SpriteBatch batch;
	private Texture snakeHead;
	
	private static final float MOVE_TIME= 0.017F;
	private float timer = MOVE_TIME;

	private static final int RIGHT=0;
	private static final int LEFT=1;
	private static final int UP=2;
	private static final int DOWN=3;
	
	
	private Texture apple;
	private boolean appleAvailable=false;
	private int appleX, appleY;
	
	private enum STATE {
		PLAYING, GAME_OVER
	}
	private STATE state=STATE.PLAYING;
	private int numberAlives;

	private Array<Snake> snakes = new Array<Snake>();

	
	private static final String GAME_OVER_TEXT="Game Over!";
	
	@Override
	public void show(){
		batch=new SpriteBatch();
		apple=new Texture(Gdx.files.internal("apple.png"));
		snakes.add(new Snake(this, "green", 0, 0));
		snakes.add(new Snake(this, "yellow", 50, 50));
		numberAlives = snakes.size;
	}
	
	@Override
	public void render(float delta){
		if(numberAlives == 0)
			state = STATE.GAME_OVER;
		switch(state){
		case PLAYING: {
			queryInput();

			timer-=delta;
			if(timer<=0){
				timer=MOVE_TIME;
				numberAlives = 0;
				for(Snake snake : snakes){
					snake.update(Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), appleX, appleY, apple.getHeight(), apple.getWidth(), snakes);
					if(snake.getState() == Snake.STATE.ALIVE)
						numberAlives++;
					//updateSnake(snake);
				}
			}
			checkAndPlaceApple();
		}
		break;
		case GAME_OVER:{

		}
		break;
		}		
		clearScreen();
		draw();
	}
	
	private void queryInput() {
		boolean lPressed1 = Gdx.input.isKeyPressed(Input.Keys.LEFT);
		boolean rPressed1 = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
		boolean uPressed1 = Gdx.input.isKeyPressed(Input.Keys.UP);
		boolean dPressed1 = Gdx.input.isKeyPressed(Input.Keys.DOWN);
		
		if (lPressed1) snakes.get(0).updateDirection(LEFT);
		if (rPressed1) snakes.get(0).updateDirection(RIGHT);
		if (uPressed1) snakes.get(0).updateDirection(UP);
		if (dPressed1) snakes.get(0).updateDirection(DOWN);


		boolean lPressed2 = Gdx.input.isKeyPressed(Input.Keys.Q);
		boolean rPressed2 = Gdx.input.isKeyPressed(Input.Keys.D);
		boolean uPressed2 = Gdx.input.isKeyPressed(Input.Keys.Z);
		boolean dPressed2 = Gdx.input.isKeyPressed(Input.Keys.S);
		
		if (lPressed2) snakes.get(1).updateDirection(LEFT);
		if (rPressed2) snakes.get(1).updateDirection(RIGHT);
		if (uPressed2) snakes.get(1).updateDirection(UP);
		if (dPressed2) snakes.get(1).updateDirection(DOWN);
	}
	
	//TODO: Empecher d'apparaitre sur tout le corps du serpent, pas seulement la tête
	private void checkAndPlaceApple(){
		if(!appleAvailable){
			boolean onASnake;
			do{
				appleX=MathUtils.random(Gdx.graphics.getBackBufferHeight()/Snake.SNAKE_MOVEMENT-1)*Snake.SNAKE_MOVEMENT;
				appleY=MathUtils.random(Gdx.graphics.getHeight()/Snake.SNAKE_MOVEMENT-1)*Snake.SNAKE_MOVEMENT;
				appleAvailable=true;
				onASnake = false;
				//for(Snake snake : snakes)
					//if(isOverlapping())
			} while(onASnake);
			//} while (isOverlapping(appleX, appleY, apple.getHeight(), apple.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth()));
		}
	}

	public void setAppleAvailable(boolean available){
		appleAvailable = available;
	}

	public boolean getAppleAvailable(){
		return appleAvailable;
	}
	
	private void clearScreen(){
		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	private void draw(){
		batch.begin();

		for(Snake snake : snakes)
			snake.draw(batch);

		if (appleAvailable) {
		batch.draw(apple, appleX, appleY);
		}
		if (state == STATE.GAME_OVER) {
			new BitmapFont().draw(batch, GAME_OVER_TEXT, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }
		batch.end();
	}

	private boolean isOverlapping(int x1, int y1, int h1, int w1, int x2, int y2, int h2, int w2){
		int top1 = y1 + h1;
		int right1 = x1 + w1;
		int top2 = y2 + h2;
		int right2 = x2 + w2;
		return !(y1 >= top2 || y2 >= top1 || x1 >= right2 || x2 >= right1);

	}
}
