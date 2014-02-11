package com.aa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Wheel {
	Sprite wheelSprite;
	Texture wheelTexture;
	public float scale;
	
	public Wheel(float scale)
	{
		this.scale = scale;
		wheelTexture = new Texture(Gdx.files.internal("gfx/forklift_wheel.png"));
		wheelSprite = new Sprite(wheelTexture);
	}
	public Sprite getSprite()
	{
		return wheelSprite;
		
	}
}
