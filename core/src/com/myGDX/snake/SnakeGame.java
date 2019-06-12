package com.myGDX.snake;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class SnakeGame extends Game {
	@Override
	public void create () {
		setScreen(new Screen_start_textures(this, 0));
	}
}
