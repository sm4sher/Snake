package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.awt.Point;

public class Apple extends Bonus {
	public Apple(Point pos){
		super(pos);
		texture = new Texture("apple.png");
		sound = Gdx.audio.newSound(Gdx.files.internal("beep.wav"));
	}

	public void collision(Snake snake){
		snake.addBodyParts(1);
		snake.addScore(1);
		sound.play(2.0f);
		eaten = true;
	}
}