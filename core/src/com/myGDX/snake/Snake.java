package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;
import java.awt.Point;

public class Snake {
	private Texture snakeHead;
	
	public static final int SNAKE_MOVEMENT=8;
	public int snakeX=0, snakeY=0;
	private static final int RIGHT=0;
	private static final int LEFT=1;
	private static final int UP=2;
	private static final int DOWN=3;

	Texture snakeDie;
	TextureRegion[] animationFrames;
	Animation animation;
	float elapsedTime;

	public static enum Color {
		GREEN, BLUE, ORANGE, RED
	}

	private int snakeDirection=RIGHT;
	public boolean directionSet=false;

	private Texture snakeBody;
	private ArrayList<BodyPart> bodyParts= new ArrayList<BodyPart>();
	private ArrayList<Point> previousPositions = new ArrayList<Point>();

	public static enum STATE {
		ALIVE, DEAD, DYING
	}
	private STATE state=STATE.ALIVE;

	private GameScreen game;

	long id;
	int score=0;

	public Snake(GameScreen game, Color color, int initX, int initY){
		snakeX = initX;
		snakeY = initY;
		snakeHead = new Texture(Gdx.files.internal("snakeHead" + getColorNumber(color) + ".png"));
		snakeBody = new Texture(Gdx.files.internal("snakeBody" + getColorNumber(color) + ".png"));
		this.game = game;
		create();
	}

	public void create () {
		snakeDie = new Texture("snakeDie.png");

		TextureRegion[][] tmpFrames = TextureRegion.split(snakeDie,32,32);

		animationFrames = new TextureRegion[4];
		int index = 0;

		for (int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++) {
				animationFrames[index++] = tmpFrames[j][i];
			}
		}

		animation = new Animation(1f/3f,animationFrames);
	}

	public void addBodyParts(int nb){
		for(int i = 0; i < nb; i++){
			//On calcule l'index de la position à laquelle placer la bodyPart: il faut que ce soit la position qui se trouve juste avant la dernière bodyPart (ou la tête si aucune bodyPart)
			//Comme le snake bouge de SNAKE_MOVEMENT pixels à chaque position, pour chaque bodyPart (+la tête), il faut enlever TAILLE_IMAGE/SNAKE_MOVEMENT positions.
			//Attention, fonction à changer si image non carrée (getHeight est utilisé) ou si taille de la tête différente des bodyParts
			int positionIndex = previousPositions.size() - (bodyParts.size() + 1)*(snakeHead.getHeight()/SNAKE_MOVEMENT);
			bodyParts.add(new BodyPart(snakeBody, previousPositions, positionIndex));
		}
	}

	public void removeBodyParts(int nb){
		nb = Math.min(nb, bodyParts.size());
		for(int i = 0; i < nb; i++){
			bodyParts.remove(bodyParts.size()-1);
		}
	}

	public int get_score(){
		return score;
	}

	public static int getColorNumber(Color color){
		switch(color){
			case GREEN:
				return 1;
			case BLUE:
				return 2;
			case ORANGE:
				return 3;
			case RED:
				return 4;
		}
		return 1;
	}

	public ArrayList<BodyPart> getBodyParts(){
		return bodyParts;
	}

	public STATE getState(){
		return state;
	}

	public void update(int windowHeight, int windowWidth, ArrayList<Bonus> bonuses, ArrayList<Snake> snakes){
		if(isDead())
			return;
		move();
		checkForOutOfBounds(windowHeight, windowWidth);
		updateBodyPartsPosition();
		checkSelfCollision();
		checkSnakeCollision(snakes);
		checkBonusCollision(bonuses);
		directionSet=false;
	}

	public boolean isDead(){
		return state == STATE.DEAD || state == STATE.DYING;
	}

	public void setState(STATE state){
		this.state = state;
	}

	public void move(){
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

	public void updateDirection(int newSnakeDirection) {
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

	public void updateBodyPartsPosition(){
		for(BodyPart bodyPart : bodyParts)
			bodyPart.nextPosition();
	}

	private void updateIfNotOppositeDirection(int newSnakeDirection, int oppositeDirection) {
			if (snakeDirection != oppositeDirection || bodyParts.size()==0) snakeDirection = newSnakeDirection;
	}

	public boolean checkSelfCollision(){
		//On ne vérifie pas la première bodyPart car elle overlap avec la tête lors des virages par exemple, et il est de toute façon normalement impossible de percuter la première
		for(int i = 1; i < bodyParts.size(); i++){
			BodyPart bodyPart = bodyParts.get(i);
			if(isOverlapping(bodyPart.getX(), bodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
				state = STATE.DYING;
			}
		}
		return false;
	}

	public void checkSnakeCollision(ArrayList<Snake> snakes){
		for(int i = 0; i < snakes.size(); i++){
			Snake otherSnake = snakes.get(i);
			if(otherSnake == this)
				continue;
			if(otherSnake.isDead())
				continue;
			if(isOverlapping(otherSnake.getSnakeX(), otherSnake.getSnakeY(), snakeHead.getHeight(), snakeHead.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
				state = STATE.DYING;
				otherSnake.setState(STATE.DYING);
			}
			for(BodyPart otherBodyPart : otherSnake.getBodyParts()){
				if(isOverlapping(otherBodyPart.getX(), otherBodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
					state = STATE.DYING;
				}
			}
		}
	}

	public void checkForOutOfBounds(int width, int height){
		if(snakeX>=width){
			snakeX=0;
		}
		if(snakeX<0){
			snakeX=width-SNAKE_MOVEMENT;
		}
		if(snakeY>=height){
			snakeY=0;
		}
		if(snakeY<0){
			snakeY=height-SNAKE_MOVEMENT;
		}
	}

	public void checkBonusCollision(ArrayList<Bonus> bonuses){
		for(Bonus bonus : bonuses){
			if(!bonus.isEaten() && isOnSnake(bonus.getX(), bonus.getY(), bonus.getHeight(), bonus.getWidth())){
				bonus.collision(this);
			}
		}
	}

	public boolean isOnSnake(int x, int y, int height, int width){
		if(isOverlapping(x, y, height, width, snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth()))
			return true;
		for(BodyPart bodyPart : bodyParts)
			if(isOverlapping(x, y, height, width, bodyPart.getX(), bodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth()))
				return true;
		return false;
	}

	public void addScore(int nb){
		score += nb;
	}

	public void draw(Batch batch){
		if(state == STATE.DEAD) 
			return;
		if(state == STATE.DYING){
			elapsedTime += Gdx.graphics.getDeltaTime();
			for(BodyPart bodyPart:bodyParts){
				batch.draw((TextureRegion) animation.getKeyFrame(elapsedTime,false),bodyPart.getX(),bodyPart.getY());
				if(elapsedTime > 0.8)
					state = STATE.DEAD;
			}
			return;
		}
		batch.draw(snakeHead, snakeX, snakeY);
		for(BodyPart bodyPart:bodyParts){
			bodyPart.draw(batch);
		}
	}

	private boolean isOverlapping(int x1, int y1, int h1, int w1, int x2, int y2, int h2, int w2){
		int top1 = y1 + h1;
		int right1 = x1 + w1;
		int top2 = y2 + h2;
		int right2 = x2 + w2;
		return !(y1 >= top2 || y2 >= top1 || x1 >= right2 || x2 >= right1);

	}
	public int getSnakeX(){
		return this.snakeX;
	}
	public int getSnakeY(){
		return this.snakeY;
	}
}