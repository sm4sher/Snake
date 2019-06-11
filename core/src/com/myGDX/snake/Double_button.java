package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Double_button{

    private TextureRegionDrawable drawable_button_up;
    private TextureRegionDrawable drawable_button_down;

    private boolean state;

    private int width = 240;
    private int height = 96;

    public Double_button(String string_up, String string_down){
        Texture texture = new Texture(Gdx.files.internal(string_up), true);
        TextureRegion texture_region = new TextureRegion(texture);
        drawable_button_up = new TextureRegionDrawable(texture_region);
        drawable_button_up.setMinWidth(width);
        drawable_button_up.setMinHeight(height);

        texture = new Texture(Gdx.files.internal(string_down), true);
        texture_region = new TextureRegion(texture);
        drawable_button_down = new TextureRegionDrawable(texture_region);
        drawable_button_down.setMinWidth(width);
        drawable_button_down.setMinHeight(height);
    }

    public TextureRegionDrawable texture_draw(boolean state){
        if(state){
            return drawable_button_up;
        }
        else{
            return drawable_button_down;
        }
    }

    public boolean get_state(){
        return state;
    }

    public void update_state(){
        if(state){
            state=false;
        }
        else{
            state=true;
        }
    }
}
