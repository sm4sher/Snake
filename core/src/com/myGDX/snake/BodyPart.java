package com.myGDX.snake;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import java.awt.Point;

class BodyPart {
	private int x,y;
	private int positionIndex;
	private Array<Point> previousPositions;
	private Texture texture;

	public BodyPart(Texture texture, Array<Point> previousPositions, int positionIndex){
		this.texture=texture;
		this.previousPositions = previousPositions;
		this.positionIndex = positionIndex;
		x = previousPositions.get(positionIndex).x;
		y = previousPositions.get(positionIndex).y;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public void nextPosition(){
		positionIndex++;
		x = previousPositions.get(positionIndex).x;
		y = previousPositions.get(positionIndex).y;
	}

	public void draw(Batch batch){
		batch.draw(texture,x,y);
	}
}