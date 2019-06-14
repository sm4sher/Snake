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

import static com.badlogic.gdx.math.MathUtils.random;

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
	Texture snakeDie2;
	TextureRegion[] animationFrames2;
	Animation animation2;
	Texture snakeDie3;
	TextureRegion[] animationFrames3;
	Animation animation3;
	Texture snakeDie4;
	TextureRegion[] animationFrames4;
	Animation animation4;
	Texture snakeUp;
	TextureRegion[] animationFramesUp;
	Animation animationUp;
	Texture snakeDown;
	TextureRegion[] animationFramesDown;
	Animation animationDown;
	float elapsedTime;
	float elapsedTimeUp;
	float elapsedTimeDown;

	public enum Color {
		GREEN, BLUE, ORANGE, RED
	}

	private int snakeDirection=RIGHT;
	public boolean directionSet=false;

	private Texture snakeBody;
	private ArrayList<BodyPart> bodyParts= new ArrayList<BodyPart>();
	private ArrayList<Point> previousPositions = new ArrayList<Point>();

	public enum STATE {
		ALIVE, DEAD, DYING
	}
	private STATE state=STATE.ALIVE;

	private GameScreen game;

	long id;
	int score=0;
	Sound damage;
	public Color colorS;
	boolean isUping = false;
	boolean isDowning = false;

	public Snake(GameScreen game, Color color, int initX, int initY){
		color = getColorByNumber(random.nextInt(4) + 1);
		colorS = color;
		snakeX = initX;
		snakeY = initY;
		snakeHead = new Texture(Gdx.files.internal("snakeHead" + getColorNumber(color) + ".png"));
		snakeBody = new Texture(Gdx.files.internal("snakeBody" + getColorNumber(color) + ".png"));
		damage = Gdx.audio.newSound(Gdx.files.internal("Minecraft_damage.wav"));
		this.game = game;
		create();
	}

	public void create () {
		snakeDie = new Texture("snakeDie.png");
		snakeDie2 = new Texture("snakeDie2.png");
		snakeDie3 = new Texture("snakeDie3.png");
		snakeDie4 = new Texture("snakeDie4.png");
		snakeUp = new Texture("snakeUp.png");
		snakeDown = new Texture("snakeDown.png");


		TextureRegion[][] tmpFrames = TextureRegion.split(snakeDie,32,32);
		TextureRegion[][] tmpFrames2 = TextureRegion.split(snakeDie2,32,32);
		TextureRegion[][] tmpFrames3 = TextureRegion.split(snakeDie3,32,32);
		TextureRegion[][] tmpFrames4 = TextureRegion.split(snakeDie4,32,32);
		TextureRegion[][] tmpFramesUp = TextureRegion.split(snakeUp,32,32);
		TextureRegion[][] tmpFramesDown = TextureRegion.split(snakeDown,32,32);

		animationFrames = new TextureRegion[4];
		animationFrames2 = new TextureRegion[4];
		animationFrames3 = new TextureRegion[4];
		animationFrames4 = new TextureRegion[4];
		animationFramesUp = new TextureRegion[30];
		animationFramesDown = new TextureRegion[30];
		int index = 0;
		int index2 = 0;
		int index3 = 0;
		int index4 = 0;
		int index5 = 0;
		int index6 = 0;


		for (int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++) {
				animationFrames[index++] = tmpFrames[j][i];
				animationFrames2[index2++] = tmpFrames2[j][i];
				animationFrames3[index3++] = tmpFrames3[j][i];
				animationFrames4[index4++] = tmpFrames4[j][i];
			}
		}
		for (int i = 0; i < 6; i++){
			for(int j = 0; j < 5; j++) {
				if (index5 < 36){
					animationFramesUp[index5++] = tmpFramesUp[i][j];
				}
			}
		}
		for (int i = 6; i > 0; i--){
			for(int j = 5; j > 0; j--) {
				if (index5 < 36){
					animationFramesDown[index6++] = tmpFramesDown[i][j];
				}
			}
		}

		animation = new Animation(1f/3f,animationFrames);
		animation2 = new Animation(1f/3f,animationFrames2);
		animation3 = new Animation(1f/3f,animationFrames3);
		animation4 = new Animation(1f/3f,animationFrames4);
		animationUp = new Animation(1f/50f,animationFramesUp);
		animationDown = new Animation(1f/50f,animationFramesDown);
	}

	public void addBodyParts(int nb){
		for(int i = 0; i < nb; i++){
			//On calcule l'index de la position à laquelle placer la bodyPart: il faut que ce soit la position qui se trouve juste avant la dernière bodyPart (ou la tête si aucune bodyPart)
			//Comme le snake bouge de SNAKE_MOVEMENT pixels à chaque position, pour chaque bodyPart (+la tête), il faut enlever TAILLE_IMAGE/SNAKE_MOVEMENT positions.
			//Attention, fonction à changer si image non carrée (getHeight est utilisé) ou si taille de la tête différente des bodyParts
			int positionIndex = previousPositions.size() - (bodyParts.size() + 1)*(snakeHead.getHeight()/SNAKE_MOVEMENT);
			bodyParts.add(new BodyPart(snakeBody, previousPositions, positionIndex));
		}
		isUping = true;
	}

	public void removeBodyParts(int nb){
		nb = Math.min(nb, bodyParts.size());
		for(int i = 0; i < nb; i++){
			bodyParts.remove(bodyParts.size()-1);
			isDowning = true;
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

	public static Color getColorByNumber(int color){
		switch(color){
			case 1:
				return Color.GREEN;
			case 2:
				return Color.BLUE;
			case 3:
				return Color.ORANGE;
			case 4:
				return Color.RED;
		}
		return Color.GREEN;
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
			if(Util.isOverlapping(bodyPart.getX(), bodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
				state = STATE.DYING;
				damage.play(3.0f);
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
			if(Util.isOverlapping(otherSnake.getSnakeX(), otherSnake.getSnakeY(), snakeHead.getHeight(), snakeHead.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
				state = STATE.DYING;
				damage.play(3.0f);
				otherSnake.setState(STATE.DYING);
			}
			for(BodyPart otherBodyPart : otherSnake.getBodyParts()){
				if(Util.isOverlapping(otherBodyPart.getX(), otherBodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth(), snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth())){
					state = STATE.DYING;
					damage.play(3.0f);
				}
			}
		}
	}

	public void checkForOutOfBounds(int height, int width){
		if(snakeX+snakeHead.getWidth()>=width){
			snakeX=0;
		}
		if(snakeX<0){
			snakeX=width-snakeHead.getWidth();
		}
		if(snakeY+snakeHead.getHeight()>=height){
			snakeY=0;
		}
		if(snakeY<0){
			snakeY=height-snakeHead.getHeight();
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
		if(Util.isOverlapping(x, y, height, width, snakeX, snakeY, snakeHead.getHeight(), snakeHead.getWidth()))
			return true;
		for(BodyPart bodyPart : bodyParts)
			if(Util.isOverlapping(x, y, height, width, bodyPart.getX(), bodyPart.getY(), snakeBody.getHeight(), snakeBody.getWidth()))
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
				switch (getColorNumber(this.colorS)){
					case 1:
						batch.draw((TextureRegion) animation.getKeyFrame(elapsedTime,false),bodyPart.getX(),bodyPart.getY());
						break;
					case 2:
						batch.draw((TextureRegion) animation2.getKeyFrame(elapsedTime,false),bodyPart.getX(),bodyPart.getY());
						break;
					case 3:
						batch.draw((TextureRegion) animation3.getKeyFrame(elapsedTime,false),bodyPart.getX(),bodyPart.getY());
						break;
					case 4:
						batch.draw((TextureRegion) animation4.getKeyFrame(elapsedTime,false),bodyPart.getX(),bodyPart.getY());
						break;
				}
				if(elapsedTime > 0.8)
					state = STATE.DEAD;
			}
			return;
		}
		batch.draw(snakeHead, snakeX, snakeY);
		for(BodyPart bodyPart:bodyParts){
			bodyPart.draw(batch);
		}
		if (isDowning && !bodyParts.isEmpty()){
			elapsedTimeDown += Gdx.graphics.getDeltaTime();
			for(BodyPart bodyPart:bodyParts) {
				batch.draw((TextureRegion) animationDown.getKeyFrame(elapsedTimeDown, false), bodyPart.getX(), bodyPart.getY());
			}
			if(elapsedTimeDown > 0.8){
				isDowning = false;
				elapsedTimeDown = 0;
			}
		}
		if (isUping && !bodyParts.isEmpty()){
			elapsedTimeUp += Gdx.graphics.getDeltaTime();
			for(BodyPart bodyPart:bodyParts) {
				batch.draw((TextureRegion) animationUp.getKeyFrame(elapsedTimeUp, false), bodyPart.getX(), bodyPart.getY());
			}
			if(elapsedTimeUp > 0.8){
				isUping = false;
				elapsedTimeUp = 0;
			}
		}
	}

	public int getSnakeX(){
		return this.snakeX;
	}
	public int getSnakeY(){
		return this.snakeY;
	}
}