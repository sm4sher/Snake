package com.myGDX.snake;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Screen_start implements Screen{
    protected SpriteBatch batch;
    protected Skin skin;
    protected Stage stage;
    protected Table table;
    protected TextureAtlas atlas;

    protected int WorldWidth = 1024;
    protected int WorldHeigth = 1024;

    protected final TextButton jeu;
    protected final TextButton jeu_coop;
    protected final TextButton jeu_versus;

    SnakeGame game;

    public Screen_start(SnakeGame _game){
        game = _game;

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage();
        atlas = new TextureAtlas("uiskin.atlas");
        skin.addRegions(atlas);

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setSize(WorldWidth, WorldHeigth);

        jeu = new TextButton("1 joueur", skin);
        table.add(jeu).width(150).padTop(10).padBottom(3);
        table.row();

        jeu_coop = new TextButton("2 joueurs coop", skin);
        table.add(jeu_coop).width(150).padTop(10).padBottom(3);
        table.row();

        jeu_versus = new TextButton("2 joueurs versus", skin);
        table.add(jeu_versus).width(150).padTop(10).padBottom(3);
        table.row();

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        jeu.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                jeu.addAction(Actions.fadeOut(0.7f));
                game.setScreen(new GameScreen());
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
