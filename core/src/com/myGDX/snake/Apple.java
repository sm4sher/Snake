package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.awt.Point;

public class Apple extends Bonus {
	public static Texture texture = new Texture("apple.png");

	public Apple(){
		super();
		sound = Gdx.audio.newSound(Gdx.files.internal("beep.wav"));
		_texture = texture;
	}

	public void collision(Snake snake){
		snake.addBodyParts(1);
		snake.addScore(1);
		sound.play(2.0f);
		eaten = true;
	}
}