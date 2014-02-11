package com.aa.entities;

import pong.client.core.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Bomb {
	public boolean isFlaggedForDelete = false;
	private Sprite sprite;	
	private Body body;
	private World world;			
	private static BodyEditorLoader loader;
	private float scale;
	private long previousTime=0;
	private boolean timerStarted = false;
	private static final float FUSE_TIME = 1000;
	public Bomb(World world, float scale)
	{		
		this.world = world;
	    this.scale = scale;
	    
	    Texture texture = new Texture(Gdx.files.internal("loaders/bomb.png"));   	 
   	    sprite = new Sprite(texture);	    
	    
		// 0. Create a loader for the file saved from the editor.
	    loader = new BodyEditorLoader(Gdx.files.internal("loaders/bombLoader.json"));
	 
	    // 1. Create a BodyDef, as usual.
	    BodyDef bodyDef = new BodyDef();
	    bodyDef.position.set(450, 60);
	    bodyDef.type = BodyType.DynamicBody;
	   
	    // 2. Create a FixtureDef, as usual.
	    FixtureDef fd = new FixtureDef();
	    fd.density = 5.0f;
	    fd.friction = .8f;
	    fd.restitution = 0.01f;
	 
	    // 3. Create a Body, as usual.
	    body = world.createBody(bodyDef);
	    body.setUserData(this);
	    // 4. Create the body fixture automatically by using the loader.
	    loader.attachFixture(body, "bomb", fd, scale);
	}
	
	public void getUpdatedSprite(SpriteBatch batch)
	{	    
		if(!isFlaggedForDelete)
		{
			
			//Put this stuff inside forklift--------------//
			Vector2 origin = loader.getOrigin("bomb", scale);
			Body modelBody = body;
			
			Vector2 modelPos = modelBody.getPosition().sub(origin);		
			Sprite modelSprite = this.getSprite();
			
			//System.err.println("(x,y):(" + modelPos.x + "," + modelPos.y + ")");
			modelSprite.setPosition(modelPos.x, modelPos.y);
			modelSprite.setOrigin(origin.x, origin.y);
			modelSprite.setRotation(modelBody.getAngle() * MathUtils.radiansToDegrees);
			modelSprite.setScale(0.079f*scale/20);
		    modelSprite.draw(batch);
		} 
	}
	
	public Sprite getSprite()
	{
		return sprite;
	}
	public Float getScale()
	{
		return scale;
	}
	
	public void stopTimer() {
		timerStarted = false;
		previousTime = com.badlogic.gdx.utils.TimeUtils.millis();
	}
	
	public void resetTimer() {
		timerStarted = true;
		previousTime = com.badlogic.gdx.utils.TimeUtils.millis();
	}
	
	public boolean checkTimer() {
		if( timerStarted  )
		{
			long currentTime = com.badlogic.gdx.utils.TimeUtils.millis();
			long diff = currentTime - previousTime;
			System.err.println("Diff: " + diff);
			boolean result = diff > FUSE_TIME;
			if (result)
			{
				createExplosion(32, body, 500);
			
			}
			return result;
		} 
		else
		{
			return false;
		}
		
	}
	
	public void createExplosion(int numRays, Body bombBody, float blastPower)
	{	
		System.err.println("Explosion");
		 for (int i = 0; i < numRays; i++) {
			 
			 float angle = (i / (float) numRays) * 360 * MathUtils.degreesToRadians;
			 System.err.println("Creating particle Rads:" + angle);
			 Vector2 rayDir = new Vector2( MathUtils.sin(angle), MathUtils.cos(angle) );
	
			 BodyDef bd = new BodyDef();
			 bd.type = BodyType.DynamicBody;
			 bd.fixedRotation = true;
			 bd.bullet = true;
			 bd.linearDamping = .5f;
			 bd.gravityScale = 0f;
			 bd.position.set(bombBody.getWorldCenter());
			 bd.linearVelocity.set( rayDir.scl(blastPower) );
			 
			 Body particleBody = world.createBody(bd);
			 
			 CircleShape circleShape = new CircleShape();
			 circleShape.setRadius(.2f);
			 
			 FixtureDef fd = new FixtureDef();
			 fd.shape = circleShape;
			 fd.density = 60 / numRays ;
			 fd.friction = 0;
			 fd.restitution = 0.99f;
			 fd.filter.groupIndex = -1;
			 
			 particleBody.createFixture(fd);
			 
		 }
	}
	
}

	
