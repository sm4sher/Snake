package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

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

	private Array<Snake> snakes = new Array<Snake>();

	private boolean dying = false;
	private boolean nbPlayers;
	private boolean gameMode;
	
	private static final String GAME_OVER_TEXT="Game Over!";

	private SnakeGame game;

	Sound sound;
	long id;
	int score;
	Timer time;

	public GameScreen(SnakeGame game, boolean nbPlayers, boolean gameMode){
		super();
		this.game = game;
		this.nbPlayers = nbPlayers;
		this.gameMode = gameMode;

		sound = Gdx.audio.newSound(Gdx.files.internal("doom.wav"));
		id = sound.play(2.0f);
		sound.setLooping(id, true);
		score = 0;
		time = new Timer();
		time.start();
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
		numberAlives = snakes.size;
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
					snake.update(Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), appleX, appleY, apple.getHeight(), apple.getWidth(), snakes);
					if(snake.getState() == Snake.STATE.ALIVE)
						numberAlives++;
				}
			}
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
	
	private void checkAndPlaceApple(){
		if(!appleAvailable){
			boolean onASnake;
			do{
				int pomme_x = Gdx.graphics.getBackBufferHeight()/Snake.SNAKE_MOVEMENT-1;
				int pomme_y = Gdx.graphics.getHeight()/Snake.SNAKE_MOVEMENT-1;
				appleX=MathUtils.random(pomme_x*1/4,pomme_x*3/4)*Snake.SNAKE_MOVEMENT;
				appleY=MathUtils.random(pomme_y*1/4,pomme_x*3/4)*Snake.SNAKE_MOVEMENT;
				appleAvailable=true;
				onASnake = false;
				for(Snake snake : snakes){
					if(snake.isOnSnake(appleX, appleY, apple.getHeight(), apple.getWidth())){
						onASnake = true;
						break;
					}
				}

			} while(onASnake);
		}
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

		if (appleAvailable) {
		batch.draw(apple, appleX, appleY);
		}
		if (state == STATE.GAME_OVER) {
			new BitmapFont().draw(batch, GAME_OVER_TEXT, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
			new BitmapFont().draw(batch,"socre joueur 1 : " + Integer.toString(snakes.get(0).get_score()), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*3/4);
			if(nbPlayers){
				new BitmapFont().draw(batch,"socre joueur 2 : " + Integer.toString(snakes.get(1).get_score()), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*2/3);
			}
			Timer.schedule(new Timer.Task(){
							   @Override
							   public void run() {
							   	Timer.instance().clear();
								   game.setScreen(new Screen_start_textures(game));
								   sound.stop();
							   }
						   },3);
		}
        batch.draw(bgSupp, 0, 0);
		batch.end();
	}
}
