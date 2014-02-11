package com.aa.entities;

import pong.client.core.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Level {
	private Sprite levelSprite;
	
	private Body levelBody;
	private World world;			
	private static BodyEditorLoader loader;
	
	private float scale;
	
	public Level(World w, float newScale)
	{
		scale = newScale;	
		create(w,0,0,newScale,"Name");
		Texture texture = new Texture(Gdx.files.internal("gfx/Ground.png"));
		levelSprite = new Sprite(texture);
	}
	
	public static void load()
	{
   	   
   	    
	}
	
	public void create(World w, float x, float y, float scale, String level)
	{	 
		// 0. Create a loader for the file saved from the editor.
	    
		loader = new BodyEditorLoader(Gdx.files.internal("data/myMap"));
	    // 1. Create a BodyDef, as usual.
	    BodyDef groundBodyDef = new BodyDef();
	    groundBodyDef.position.set(x, y);
	    groundBodyDef.type = BodyType.StaticBody;
	    
	    // 2. Create a FixtureDef, as usual.
	    FixtureDef fd = new FixtureDef();
	 
	    // 3. Create a Body, as usual.
	    levelBody = w.createBody(groundBodyDef);
	    levelBody.setUserData(this);
	    // 4. Create the body fixture automatically by using the loader.
	    loader.attachFixture(levelBody, level, fd, scale);
	}
	
	public Sprite setSpriteToBody()
	{
		Vector2 modelOrigin = loader.getOrigin("Name", scale).cpy();		
		Body modelBody = levelBody;
		
		Vector2 modelPos = modelBody.getPosition().sub(modelOrigin);		
		
		//System.err.println("(x,y):(" + modelPos.x + "," + modelPos.y + ")");
		levelSprite.setPosition(modelPos.x, modelPos.y);
		levelSprite.setOrigin(modelOrigin.x, modelOrigin.y);
		levelSprite.setRotation(modelBody.getAngle() * MathUtils.radiansToDegrees);
		levelSprite.setScale(0.72f);
		return levelSprite;
	}
}
