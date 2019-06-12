package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.awt.Point;

public abstract class Bonus {
	protected Point pos;
	protected Texture _texture;
	protected Sound sound;
	protected boolean eaten;

	public Bonus(Point pos){
		this.pos = pos;
		eaten = false;
	}

	public int getX(){
		return pos.x;
	}

	public int getY(){
		return pos.y;
	}

	public int getHeight(){
		return _texture.getHeight();
	}

	public int getWidth(){
		return _texture.getWidth();
	}

	public boolean isEaten(){
		return eaten;
	}

	public abstract void collision(Snake snake);

	public void draw(Batch batch){
		if(eaten)
			return;
		batch.draw(_texture, pos.x, pos.y);
	}
}