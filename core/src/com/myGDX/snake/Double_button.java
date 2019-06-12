package com.myGDX.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Double_button{

    private TextureRegionDrawable drawable_button_up;
    private TextureRegionDrawable drawable_button_down;
    private TextureRegionDrawable drawable_disabled;
    private ImageButton.ImageButtonStyle style;

    private boolean state;

    private int width = 240;
    private int height = 96;

    public Double_button(String string_up, String string_down, String disabled){
        drawable_button_up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(string_up),true)));
        drawable_button_up.setMinWidth(width);
        drawable_button_up.setMinHeight(height);

        drawable_button_down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(string_down),true)));
        drawable_button_down.setMinWidth(width);
        drawable_button_down.setMinHeight(height);

        drawable_disabled = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(disabled),true)));
        drawable_disabled.setMinWidth(width);
        drawable_disabled.setMinHeight(height);

        style = new ImageButton.ImageButtonStyle();
        state = false;
    }

    public TextureRegionDrawable texture_draw(boolean state){
        if(state){
            return drawable_button_up;
        }
        else{
            return drawable_button_down;
        }
    }

    public TextureRegionDrawable disabled_draw(){
        return drawable_disabled;
    }

    public void set_style(){
        style.imageUp = drawable_button_up;
        style.imageChecked = drawable_button_down;
        style.imageDisabled = drawable_disabled;
    }

    public ImageButton.ImageButtonStyle getStyle(){
        return style;
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
