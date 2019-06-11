package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Screen_start_textures implements Screen{

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

    protected int WorldWidth = 1024;
    protected int WorldHeigth = 1024;

    SnakeGame game;

    public Screen_start_textures(SnakeGame _game){
        game = _game;

        batch = new SpriteBatch();
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setSize(WorldWidth, WorldHeigth);

        double_multi = new Double_button(string_multi_up, string_multi_down);
        button_multi = new ImageButton(double_multi.texture_draw(true));
        table.add(button_multi);
        table.row();

        double_mode = new Double_button(string_mode_up, string_mode_down);
        button_mode = new ImageButton(double_mode.texture_draw(true));
        table.add(button_mode);
        table.row();

        texture_jeu = new Texture(Gdx.files.internal("texture_jeu.gif"));
        region_jeu = new TextureRegion(texture_jeu);
        drawable_jeu = new TextureRegionDrawable(region_jeu);
        button_jeu = new ImageButton(drawable_jeu);
        table.add(button_jeu);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        button_jeu.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_jeu.addAction(Actions.fadeOut(0.7f));
                button_jeu.setDisabled(true);
                button_mode.setDisabled(true);
                button_multi.setDisabled(true);
                game.setScreen(new GameScreen(2, 0));
            }
        });

        button_multi.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_multi.addAction(Actions.fadeOut(0.7f));
                double_multi.update_state();
                button_multi = new ImageButton(double_multi.texture_draw(double_multi.get_state()));
            }
        });

        button_mode.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                button_mode.addAction(Actions.fadeOut(0.7f));
                double_mode.update_state();
                button_mode = new ImageButton(double_mode.texture_draw(double_mode.get_state()));
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

        batch.begin();
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
