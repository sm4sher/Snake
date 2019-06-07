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
	
	private static final float MOVE_TIME= 0.001F;
	private float timer = MOVE_TIME;
	private static final int SNAKE_MOVEMENT=4;
	private int snakeX=0, snakeY=0;
	private static final int RIGHT=0;
	private static final int LEFT=1;
	private static final int UP=2;
	private static final int DOWN=3;
	
	private int snakeDirection=RIGHT;
	
	private Texture apple;
	private boolean appleAvailable=false;
	private int appleX, appleY;
	
	private Texture snakeBody;
	private Array<BodyPart> bodyParts= new Array<BodyPart>();
	private Array<Point> previousPositions = new Array<Point>();
	
	private ShapeRenderer shapeRenderer;
	
	private boolean directionSet=false;
	private boolean hasHit=false;
	
	private enum STATE {
		PLAYING, GAME_OVER
	}
	private STATE state=STATE.PLAYING;

	
	private static final String GAME_OVER_TEXT="Game Over!";
	
	@Override
	public void show(){
		shapeRenderer=new ShapeRenderer();
		batch=new SpriteBatch();
		snakeHead=new Texture(Gdx.files.internal("snakeHead.png"));
		apple=new Texture(Gdx.files.internal("apple.png"));
		snakeBody=new Texture(Gdx.files.internal("snakeBody.png"));
	}
	
	@Override
	public void render(float delta){
		switch(state){
		case PLAYING: {
			queryInput();
			updateSnake(delta);
			checkAppleCollision();
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
	
	private void checkForOutOfBounds(){
		if(snakeX>=Gdx.graphics.getWidth()){
			snakeX=0;
		}
		if(snakeX<0){
			snakeX=Gdx.graphics.getWidth()-SNAKE_MOVEMENT;
		}
		if(snakeY>=Gdx.graphics.getHeight()){
			snakeY=0;
		}
		if(snakeY<0){
			snakeY=Gdx.graphics.getHeight()-SNAKE_MOVEMENT;
		}
	}
	
	private void queryInput() {
		boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
		boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
		boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
		boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
		
		if (lPressed) updateDirection(LEFT);
		if (rPressed) updateDirection(RIGHT);
		if (uPressed) updateDirection(UP);
		if (dPressed) updateDirection(DOWN);
	}
	
	private void moveSnake(){
		previousPositions.add(new Point(snakeX, snakeY));
		switch(snakeDirection){
			case RIGHT: {
				snakeX+=SNAKE_MOVEMENT;
				break;
			}
			case LEFT: {
				snakeX-=SNAKE_MOVEMENT;
				break;
			}
			case UP: {
				snakeY+=SNAKE_MOVEMENT;
				break;
			}
			case DOWN: {
				snakeY-=SNAKE_MOVEMENT;
				break;
			}
		}
	}
	
	private void updateDirection(int newSnakeDirection) {
		if (!directionSet && snakeDirection != newSnakeDirection) {
			directionSet = true;
			switch (newSnakeDirection) {
			case LEFT: {
				updateIfNotOppositeDirection(newSnakeDirection, RIGHT);
			}
			break;
			case RIGHT: {
				updateIfNotOppositeDirection(newSnakeDirection, LEFT);
			}
			break;
			case UP: {
				updateIfNotOppositeDirection(newSnakeDirection, DOWN);
			}
			break;
			case DOWN: {
				updateIfNotOppositeDirection(newSnakeDirection, UP);
			}
			break;
			}
		}
	}
	
	private void updateBodyPartsPosition(){
		for(BodyPart bodyPart : bodyParts)
			bodyPart.nextPosition();
	}
	
	//TODO: Empecher d'apparaitre sur tout le corps du serpent, pas seulement la tête
	private void checkAndPlaceApple(){
		if(!appleAvailable){
			do{
				appleX=MathUtils.random(Gdx.graphics.getBackBufferHeight()/SNAKE_MOVEMENT-1)*SNAKE_MOVEMENT;
				appleY=MathUtils.random(Gdx.graphics.getHeight()/SNAKE_MOVEMENT-1)*SNAKE_MOVEMENT;
				appleAvailable=true;		
			} while (isOverlapping(appleX, appleY, apple.getHeight(), apple.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth()));
		}
	}
	
	private void clearScreen(){
		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	private void draw(){
		batch.begin();
		batch.draw(snakeHead, snakeX, snakeY); 
		for(BodyPart bodyPart:bodyParts){
			bodyPart.draw(batch);
		}
		if (appleAvailable) {
		batch.draw(apple, appleX, appleY);
		}
		if (state == STATE.GAME_OVER) {
			new BitmapFont().draw(batch, GAME_OVER_TEXT, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }
		batch.end();
	}
	
	private void checkAppleCollision(){
		if(appleAvailable && isOverlapping(snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth(), appleX, appleY, apple.getHeight(), apple.getWidth())){
			//On calcule l'index de la position à laquelle placer la bodyPart: il faut que ce soit la position qui se trouve juste avant la dernière bodyPart (ou la tête si aucune bodyPart)
			//Comme le snake bouge de SNAKE_MOVEMENT pixels à chaque position, pour chaque bodyPart (+la tête), il faut enlever TAILLE_IMAGE/SNAKE_MOVEMENT positions.
			//Attention, fonction à changer si image non carrée (getHeight est utilisé) ou si taille de la tête différente des bodyParts
			int positionIndex = previousPositions.size - (bodyParts.size + 1)*(snakeHead.getHeight()/SNAKE_MOVEMENT);
			bodyParts.add(new BodyPart(snakeBody, previousPositions, positionIndex));
			appleAvailable=false;
		}
	}

	private boolean isOverlapping(int x1, int y1, int h1, int w1, int x2, int y2, int h2, int w2){
		int top1 = y1 + h1;
		int right1 = x1 + w1;
		int top2 = y2 + h2;
		int right2 = x2 + w2;
		return !(y1 >= top2 || y2 >= top1 || x1 >= right2 || x2 >= right1);

	}

	private void updateIfNotOppositeDirection(int newSnakeDirection, int oppositeDirection) {
			if (snakeDirection != oppositeDirection || bodyParts.size==0) snakeDirection = newSnakeDirection;
	}

	private void checkSnakeBodyCollision(){
		//On ne vérifie pas la première bodyPart car elle overlap avec la tête lors des virages par exemple, et il est de toute façon normalement impossible de percuter la première
		for(int i = 1; i < bodyParts.size; i++){
			BodyPart bodyPart = bodyParts.get(i);
			if(isOverlapping(bodyPart.getX(), bodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
				state = STATE.GAME_OVER;
				hasHit = true;
			}
		}
	}

	private void updateSnake(float delta){
		if(!hasHit){
			timer-=delta;
			if(timer<=0){
				timer=MOVE_TIME;
				moveSnake();
				checkForOutOfBounds();
				updateBodyPartsPosition();
				checkSnakeBodyCollision();
				directionSet=false;
			}
		}
	}
}
