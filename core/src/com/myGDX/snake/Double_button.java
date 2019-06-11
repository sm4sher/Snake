package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Double_button{

    private TextureRegionDrawable drawable_button_up;
    private TextureRegionDrawable drawable_button_down;

    private boolean state;

    public Double_button(String string_up, String string_down){
        Texture texture = new Texture(Gdx.files.internal(string_up));
        TextureRegion texture_region = new TextureRegion(texture);
        drawable_button_up = new TextureRegionDrawable(texture_region);

        texture = new Texture(Gdx.files.internal(string_down));
        texture_region = new TextureRegion(texture);
        drawable_button_down = new TextureRegionDrawable(texture_region);
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
