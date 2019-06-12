package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.awt.Point;

public class Banana extends Bonus {
	public static Texture texture = new Texture("banana.png");

	public Banana(){
		super();
		sound = Gdx.audio.newSound(Gdx.files.internal("beep.wav"));
		_texture = texture;
	}

	public void collision(Snake snake){
		snake.addBodyParts(5);
		snake.addScore(10);
		sound.play(2.0f);
		eaten = true;
	}
}