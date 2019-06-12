package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxBuild;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.awt.Point;

public class GameScreen extends ScreenAdapter {

	private SpriteBatch batch;
	
	private static final float MOVE_TIME= 0.017F;
	private float timer = MOVE_TIME;

	private static final int RIGHT=0;
	private static final int LEFT=1;
	private static final int UP=2;
	private static final int DOWN=3;
	
	private Texture bc = new Texture("bc.png");
    private Texture bgSupp = new Texture("bgSupp.png");
	private Texture apple;
    private Texture banana;
    private Texture bug;
	private boolean appleAvailable=false;
	private int appleX, appleY;
    private int bananaX, bananaeY;
    private int bugX, bugY;
	
	private enum STATE {
		PLAYING, GAME_OVER
	}
	private STATE state=STATE.PLAYING;
	private int numberAlives;

	private ArrayList<Snake> snakes = new ArrayList<Snake>();
	private ArrayList<Bonus> bonuses = new ArrayList<Bonus>();

	private boolean dying = false;
	private boolean nbPlayers;
	private boolean gameMode;
	
	private static final String GAME_OVER_TEXT="Game Over!";

	private SnakeGame game;

	Sound sound;
	long id;
	int num_music;
	int score;
	int current_score;

	public GameScreen(SnakeGame game, boolean nbPlayers, boolean gameMode, int num_music){
		super();
		this.game = game;
		this.nbPlayers = nbPlayers;
		this.gameMode = gameMode;
		this.num_music = num_music;

		if(num_music == 0){
			sound = Gdx.audio.newSound(Gdx.files.internal("doom.wav"));
		}
		else{
			sound = Gdx.audio.newSound(Gdx.files.internal("Zelda_music.wav"));
		}
		id = sound.play(2.0f);
		sound.setLooping(id,true);
		score = 0;

		Gdx.input.setCursorPosition(0,0);
	}

	public int get_score(){
		return score;
	}

	@Override
	public void show(){
		batch=new SpriteBatch();
		apple=new Texture(Gdx.files.internal("apple.png"));
        banana=new Texture(Gdx.files.internal("banana.png"));
        bug=new Texture(Gdx.files.internal("bug.png"));
		snakes.add(new Snake(this, Snake.Color.GREEN, 0, 0));
		if(nbPlayers)
			snakes.add(new Snake(this, Snake.Color.RED, 50, 50));
		numberAlives = snakes.size();
	}

	@Override
	public void render(float delta){
		if(numberAlives == 0) state = STATE.GAME_OVER;
		switch(state){
		case PLAYING: {
			queryInput_solo();
			if(nbPlayers){
				queryInput_multi();
			}

			timer-=delta;
			if(timer<=0){
				timer=MOVE_TIME;
				numberAlives = 0;
				for(Snake snake : snakes){
					snake.update(Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), bonuses, snakes);
					if(snake.getState() == Snake.STATE.ALIVE)
						numberAlives++;
				}
			}
			checkAndPlaceBonus();
		}
		break;
		case GAME_OVER:{

		}
		break;
		}		
		clearScreen();
		draw();
	}
	
	private void queryInput_solo() {
		boolean lPressed1 = Gdx.input.isKeyPressed(Input.Keys.LEFT);
		boolean rPressed1 = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
		boolean uPressed1 = Gdx.input.isKeyPressed(Input.Keys.UP);
		boolean dPressed1 = Gdx.input.isKeyPressed(Input.Keys.DOWN);
		
		if (lPressed1) snakes.get(0).updateDirection(LEFT);
		if (rPressed1) snakes.get(0).updateDirection(RIGHT);
		if (uPressed1) snakes.get(0).updateDirection(UP);
		if (dPressed1) snakes.get(0).updateDirection(DOWN);

	}

	private void queryInput_multi() {
		boolean lPressed2 = Gdx.input.isKeyPressed(Input.Keys.Q);
		boolean rPressed2 = Gdx.input.isKeyPressed(Input.Keys.D);
		boolean uPressed2 = Gdx.input.isKeyPressed(Input.Keys.Z);
		boolean dPressed2 = Gdx.input.isKeyPressed(Input.Keys.S);

		if (lPressed2) snakes.get(1).updateDirection(LEFT);
		if (rPressed2) snakes.get(1).updateDirection(RIGHT);
		if (uPressed2) snakes.get(1).updateDirection(UP);
		if (dPressed2) snakes.get(1).updateDirection(DOWN);
	}
	
	private void checkAndPlaceBonus(){
		int nbBonuses = getNbBonusesAvailable();
		if(nbBonuses < 3){
			float proba = MathUtils.random();
			Bonus newBonus;
			if(proba < 0.995 && nbBonuses > 0)
				return;
			else if (proba < 0.999)
				newBonus = new Apple();
			else if (proba < 0.9999)
				newBonus = new Bug();
			else
				newBonus = new Banana();


			boolean onSomething;
			int bonusX;
			int bonusY;
			do{
				int bonus_x = Gdx.graphics.getBackBufferHeight()/Snake.SNAKE_MOVEMENT-1;
				int bonus_y = Gdx.graphics.getHeight()/Snake.SNAKE_MOVEMENT-1;
				bonusX=MathUtils.random(bonus_x*1/8,bonus_x*7/8)*Snake.SNAKE_MOVEMENT;
				bonusY=MathUtils.random(bonus_y*1/8,bonus_y*7/8)*Snake.SNAKE_MOVEMENT;

				onSomething = false;
				for(Snake snake : snakes){
					if(snake.isOnSnake(bonusX, bonusY, newBonus.getHeight(), newBonus.getWidth())){
						onSomething = true;
						break;
					}
				}
				if(!onSomething){
					for(Bonus bonus : bonuses){
						if(bonus.isOnBonus(bonusX, bonusY, newBonus.getHeight(), newBonus.getWidth())){
							onSomething = true;
							break;
						}
					}
				}
			} while(onSomething);
			newBonus.setPos(new Point(bonusX, bonusY));
			bonuses.add(newBonus);
		}
	}

	public int getNbBonusesAvailable(){
		int nb = 0;
		for(Bonus bonus : bonuses)
			if(!bonus.isEaten())
				nb++;
		return nb;
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		batch.begin();
		batch.draw(bc, 0, 0);
		for(Snake snake : snakes)
			snake.draw(batch);
		for(Bonus bonus : bonuses)
			bonus.draw(batch);

		if (state == STATE.GAME_OVER) {
			sound.stop();

			new BitmapFont().draw(batch, GAME_OVER_TEXT, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
			current_score=0;
			if(!nbPlayers){
				current_score = snakes.get(0).get_score();
				new BitmapFont().draw(batch,"score joueur : " + Integer.toString(current_score), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*3/4);
			}
			else if(nbPlayers){
				if(!gameMode){
					current_score = snakes.get(0).get_score()+snakes.get(1).get_score();
					new BitmapFont().draw(batch,"score joueurs : " + Integer.toString(current_score), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*3/4);
				}
				else{
					current_score = Math.max(snakes.get(0).get_score(),snakes.get(1).get_score());
					new BitmapFont().draw(batch,"score joueur 1 : " + Integer.toString(snakes.get(0).get_score()), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*3/4);
					new BitmapFont().draw(batch,"score joueur 2 : " + Integer.toString(snakes.get(1).get_score()), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*2/3);
				}
			}

			Timer.schedule(new Timer.Task(){
							   @Override
							   public void run() {
							   	Timer.instance().clear();
								   game.setScreen(new Screen_start_textures(game,current_score));
							   }
						   },3);
		}
        batch.draw(bgSupp, 0, 0);
		batch.end();
	}
}
