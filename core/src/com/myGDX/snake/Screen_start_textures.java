package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.swing.*;

public class Screen_start_textures implements Screen{

    Preferences prefs = Gdx.app.getPreferences("game preferences");
    public int currentScore = 0;
    public int highscore = prefs.getInteger("highscore");

    protected SpriteBatch batch;
    protected Stage stage;
    protected Table table;

    protected String string_multi_up = "texture_multi_up.png";
    protected String string_multi_down = "texture_multi_down.png";
    protected Double_button double_multi;
    protected ImageButton button_multi;

    protected String string_mode_up = "texture_mode_up.png";
    protected String string_mode_down = "texture_mode_down.png";
    protected Double_button double_mode;
    protected ImageButton button_mode;

    protected Texture texture_jeu;
    protected TextureRegion region_jeu;
    protected TextureRegionDrawable drawable_jeu;
    protected ImageButton button_jeu;

    protected Texture texture_background;
    protected Texture texture_upground;

    protected int WorldWidth = 1024;
    protected int WorldHeigth = 1024;

    private static final int FRAME_COLS = 5, FRAME_ROWS = 8;

    // Objects used
    public Animation<TextureRegion> playAnimation; // Must declare frame type (TextureRegion)
    Texture walkSheet;
    SpriteBatch spriteBatch;

    // A variable for tracking elapsed time for the animation
    float stateTime;

    SnakeGame game;

    public void create() {

        // Load the sprite sheet as a Texture
        walkSheet = new Texture(Gdx.files.internal("playSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] playFrame = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                playFrame[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        playAnimation = new Animation<TextureRegion>(0.050f, playFrame);

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        stateTime = 0f;
    }


    public Screen_start_textures(SnakeGame _game){
        game = _game;
        create();
        batch = new SpriteBatch();
        stage = new Stage();



        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setSize(WorldWidth, WorldHeigth);

        double_multi = new Double_button(string_multi_up, string_multi_down);
        button_multi = new ImageButton(double_multi.texture_draw(true), double_multi.texture_draw(false), double_multi.texture_draw(false));
        table.add(button_multi);
        table.row().height(146).width(240);

        double_mode = new Double_button(string_mode_up, string_mode_down);
        button_mode = new ImageButton(double_mode.texture_draw(true), double_mode.texture_draw(false), double_mode.texture_draw(false));
        table.add(button_mode);
        table.row().height(106).width(240);

        texture_jeu = new Texture(Gdx.files.internal("texture_jeu.gif"));
        region_jeu = new TextureRegion(texture_jeu);
        drawable_jeu = new TextureRegionDrawable(region_jeu);
        button_jeu = new ImageButton(drawable_jeu);

        texture_background = new Texture("menuBG.png");
        texture_upground = new Texture("bgSupp.png");

        table.add(button_jeu);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        button_jeu.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_jeu.addAction(Actions.fadeOut(0.7f));
                table.clearChildren();
                game.setScreen(new GameScreen(game,2, 0));
            }
        });

        button_multi.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_multi.addAction(Actions.fadeOut(0.7f));
                double_multi.update_state();
                button_multi.setChecked(double_multi.get_state());
            }
        });

        button_mode.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_mode.addAction(Actions.fadeOut(0.7f));
                double_mode.update_state();
                button_mode.setChecked(double_mode.get_state());
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (currentScore > highscore) {
            prefs.putInteger("highscore", highscore);
            prefs.flush();
        }
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = playAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        stage.draw();
        batch.draw(currentFrame, 50, 50); // Draw current frame at (50, 50)
        stage.getBatch().begin();
        stage.getBatch().draw(texture_background,0,0);
        stage.getBatch().draw(texture_upground,0,0);
        stage.getBatch().end();
        Drawable drawer = (Drawable)new TextureRegionDrawable(currentFrame);
        drawer.setMinHeight(800);
        drawer.setMinWidth(1280);
        ImageButton.ImageButtonStyle test = new ImageButton.ImageButtonStyle();
        test.imageUp = drawer;
        button_jeu.setStyle(test);
        stage.draw();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
