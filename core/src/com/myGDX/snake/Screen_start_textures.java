package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Screen_start_textures implements Screen{

    private SpriteBatch batch;
    private Stage stage;
    private Table table;
    Preferences prefs = Gdx.app.getPreferences("game preferences");
    public int current_score = prefs.getInteger("current_score");
    public int highscore = prefs.getInteger("highscore");

    private String string_multi_up = "texture_multi_up.png";
    private String string_multi_down = "texture_multi_down.png";
    private Double_button double_multi;
    private ImageButton button_multi;

    private String string_mode_up = "texture_mode_up.png";
    private String string_mode_down = "texture_mode_down.png";
    private String string_mode_disabled = "texture_mode_disabled.png";
    private Double_button double_mode;
    private ImageButton button_mode;

    private String string_jeu = "texture_jeu.gif";
    private Double_button double_jeu;
    private ImageButton button_jeu;

    private String string_doom = "pixel_doom.png";
    private String string_link = "pixel_link.png";
    private Double_button double_music;
    private ImageButton button_music;

    private String[] string_snake1 = new String[]{"snakeHead1.png","snakeHead2.png"};
    private Double_button double_snake1;
    private ImageButton button_snake1;

    private String[] string_snake2 = new String[]{"snakeHead3.png","snakeHead4.png"};
    private Double_button double_snake2;
    private ImageButton button_snake2;

    private Texture texture_background;
    private Texture texture_upground;

    private int WorldWidth = 1024;
    private int WorldHeigth = 1024;

    private static final int FRAME_COLS = 5, FRAME_ROWS = 8;

    private Animation<TextureRegion> playAnimation; // Must declare frame type (TextureRegion)
    private Texture walkSheet;
    private float stateTime;

    private SnakeGame game;

    private Sound sound;
    private long id;
    private int num_music=1;

    private BitmapFont font;

    Pixmap cursor = new Pixmap(Gdx.files.internal("cursor.png"));

    protected void create() {

        walkSheet = new Texture(Gdx.files.internal("playSheet.png"));

        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] playFrame = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                playFrame[index++] = tmp[i][j];
            }
        }

        playAnimation = new Animation<TextureRegion>(0.050f, playFrame);

        stateTime = 0f;
    }


    public Screen_start_textures(SnakeGame _game, int current_score){
        create();

        game = _game;
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, 0, 0));
        cursor.dispose();

        this.current_score = current_score;
        if (current_score > highscore) {
            prefs.putInteger("highscore", current_score);
            prefs.flush();
        }
        highscore = prefs.getInteger("highscore");

        sound = Gdx.audio.newSound(Gdx.files.internal("boutton.wav"));
        texture_background = new Texture("menuBG.png");
        texture_upground = new Texture("bgSupp.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("AldotheApache.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        font = generator.generateFont(parameter);
        generator.dispose();

        table = new Table();
        table.setSize(WorldWidth, WorldHeigth);
        table.bottom().left();

        double_multi = new Double_button(string_multi_up, string_multi_down, string_multi_down);
        button_multi = new ImageButton(double_multi.texture_draw(true), double_multi.texture_draw(false), double_multi.texture_draw(false));
        table.add(button_multi).padLeft(192);

        table.row().height(146).width(240);

        double_mode = new Double_button(string_mode_up, string_mode_down, string_mode_disabled);
        double_mode.set_style();

        button_mode = new ImageButton(double_mode.getStyle());
        button_mode.setDisabled(true);
        table.add(button_mode).padLeft(192);

        table.row().height(106).width(240);

        double_jeu = new Double_button(string_jeu, string_jeu, string_jeu);
        button_jeu = new ImageButton(double_jeu.texture_draw(true));
        table.add(button_jeu).spaceBottom(80).padLeft(192);

        table.row();

        table.add(new Label("Couleur joueur 1 : ", new Label.LabelStyle(font, Color.WHITE))).spaceBottom(20).padLeft(175);
        table.row();
        double_snake1 = new Double_button(string_snake1[0],string_snake1[1],string_snake1[1]);
        button_snake1 = new ImageButton(double_snake1.texture_draw(true),double_snake1.texture_draw(false),double_snake1.texture_draw(false));
        table.add(button_snake1).padBottom(50).padLeft(175);

        table.row();

        table.add(new Label("Couleur joueur 2 : ", new Label.LabelStyle(font, Color.WHITE))).spaceBottom(20).padLeft(175);
        table.row();
        double_snake2 = new Double_button(string_snake2[0],string_snake2[1],string_snake2[1]);
        button_snake2 = new ImageButton(double_snake2.texture_draw(true),double_snake2.texture_draw(false),double_snake2.texture_draw(false));
        table.add(button_snake2).padBottom(50).padLeft(175);

        table.row().height(56).width(240);

        double_music = new Double_button(string_link,string_doom,string_doom);
        button_music = new ImageButton(double_music.texture_draw(true), double_music.texture_draw(false), double_music.texture_draw(false));
        table.add(button_music).bottom().padBottom(50).padRight(500).padLeft(50);

        table.add(new Label("Meilleur score : "+highscore, new Label.LabelStyle(font, Color.WHITE))).spaceBottom(50);

        stage.addActor(table);

        button_snake1.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                id = sound.play();
                button_snake1.addAction(Actions.fadeOut(0.7f));
                double_snake1.update_state();
                button_snake1.setChecked(double_snake1.get_state());
            }
        });

        button_snake2.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                id = sound.play();
                button_snake2.addAction(Actions.fadeOut(0.7f));
                double_snake2.update_state();
                button_snake2.setChecked(double_snake2.get_state());
            }
        });

        button_multi.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                id = sound.play(1.0f);
                button_mode.addAction(Actions.fadeOut(0.7f));
                double_multi.update_state();
                button_multi.setChecked(double_multi.get_state());

                if(!double_multi.get_state()){
                    button_mode.setDisabled(true);
                }
                else{
                    button_mode.setDisabled(false);
                }
            }
        });

        button_mode.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                id = sound.play(1.0f);
                button_mode.addAction(Actions.fadeOut(0.7f));
                double_mode.update_state();
                button_mode.setChecked(double_mode.get_state());
            }
        });

        button_jeu.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_jeu.addAction(Actions.fadeOut(0.7f));
                id = sound.play(1.0f);
                table.clearChildren();
                game.setScreen(new GameScreen(game,double_multi.get_state(), double_mode.get_state(), num_music, double_snake1.get_state(), double_snake2.get_state()));
            }
        });

        button_music.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_music.addAction(Actions.fadeOut(0.7f));
                id = sound.play(1.0f);
                double_music.update_state();
                button_music.setChecked(double_music.get_state());
                if(num_music == 1){
                    num_music = 0;
                }
                else{
                    num_music = 1;
                }
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
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
        width = WorldWidth;
        height = WorldHeigth;
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
