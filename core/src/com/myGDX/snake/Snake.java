package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.awt.Point;

public class Snake {
	private Texture snakeHead;
	
	public static final int SNAKE_MOVEMENT=8;
	private int snakeX=0, snakeY=0;
	private static final int RIGHT=0;
	private static final int LEFT=1;
	private static final int UP=2;
	private static final int DOWN=3;
	
	private int snakeDirection=RIGHT;
	public boolean directionSet=false;

	private Texture snakeBody;
	private Array<BodyPart> bodyParts= new Array<BodyPart>();
	private Array<Point> previousPositions = new Array<Point>();

	private GameScreen game;

	public Snake(GameScreen game, String color, int initX, int initY){
		snakeX = initX;
		snakeY = initY;
		snakeHead = new Texture(Gdx.files.internal("snakeHead.png"));
		snakeBody = new Texture(Gdx.files.internal("snakeBody.png"));
		this.game = game;
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
			if (snakeDirection != oppositeDirection || bodyParts.size==0) snakeDirection = newSnakeDirection;
	}

	public boolean checkSelfCollision(){
		//On ne vérifie pas la première bodyPart car elle overlap avec la tête lors des virages par exemple, et il est de toute façon normalement impossible de percuter la première
		for(int i = 1; i < bodyParts.size; i++){
			BodyPart bodyPart = bodyParts.get(i);
			if(isOverlapping(bodyPart.getX(), bodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
				return true;
			}
		}
		return false;
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

	public void checkAppleCollision(int appleX, int appleY, int appleHeight, int appleWidth){
		if(game.getAppleAvailable() && isOverlapping(snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth(), appleX, appleY, appleHeight, appleWidth)){
			//On calcule l'index de la position à laquelle placer la bodyPart: il faut que ce soit la position qui se trouve juste avant la dernière bodyPart (ou la tête si aucune bodyPart)
			//Comme le snake bouge de SNAKE_MOVEMENT pixels à chaque position, pour chaque bodyPart (+la tête), il faut enlever TAILLE_IMAGE/SNAKE_MOVEMENT positions.
			//Attention, fonction à changer si image non carrée (getHeight est utilisé) ou si taille de la tête différente des bodyParts
			int positionIndex = previousPositions.size - (bodyParts.size + 1)*(snakeHead.getHeight()/SNAKE_MOVEMENT);
			bodyParts.add(new BodyPart(snakeBody, previousPositions, positionIndex));
			game.setAppleAvailable(false);
		}
	}

	public void draw(Batch batch){
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
}