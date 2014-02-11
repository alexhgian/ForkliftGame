package com.aa.ForkLift;

import com.aa.entities.Bomb;
import com.aa.entities.Forklift;
import com.aa.entities.Level;
import com.aa.entities.Wheel;
import com.aa.listeners.CollisionDetection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Play implements Screen {
	private static final boolean debugTrue = true;
	private World world;
	Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private OrthographicCamera camera;
    
	private float timestep = 1 / 60f;
	private static float GRAVITY = -9.8f;
	//private Tank tank;
	private Forklift myForklift;
	private Bomb bomb;
	//------------ Maps ---------------//
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private Level myLevel;
	
	public Play()
	{	
		world = new World(new Vector2(0,GRAVITY), true);//Zero Gravity
		batch = new SpriteBatch();		
		camera = new OrthographicCamera();	
   		debugRenderer = new Box2DDebugRenderer(true,true,false, false, true, true );  
		//debugRenderer = new Box2DDebugRenderer( true, false, false, false, false, false );  
   		world.setContactListener(new CollisionDetection());
	}
	
	@Override
	public void render(float delta) {	
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
		
		renderer.render();
		renderer.setView(camera);
		camera.position.set(myForklift.getVehicle().getWorldCenter().x, myForklift.getVehicle().getWorldCenter().y, 0);
		camera.update();	
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		myForklift.getUpdatedSprite().draw(batch); 
        if(bomb !=null) 
        	bomb.getUpdatedSprite(batch);
		myLevel.setSpriteToBody().draw(batch);
		Array<Body> tmpBodies = new Array<Body>();
		world.getBodies(tmpBodies);
		for(Body body: tmpBodies)
		{
			if(body.getUserData() != null && body.getUserData() instanceof Wheel) 
			{
				Wheel wheel = (Wheel) body.getUserData();
				Sprite sprite = wheel.getSprite();
				sprite.setPosition(body.getPosition().x-sprite.getWidth()/2, body.getPosition().y-sprite.getHeight()/2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.setScale(wheel.scale);
				sprite.draw(batch);
			} else if( body != null && body.getUserData() instanceof Bomb)
			{
				Bomb data = (Bomb) body.getUserData();
		    	if(data.checkTimer()){
		    		data.isFlaggedForDelete = true; 
		    	}
				
	            if(data.isFlaggedForDelete ) {
	            	
	            		System.err.println("Deleting bomb");
		            	world.destroyBody(body);
		            	body.setUserData(null);
		            	body = null;
	            	
	            
	            }
	        }
		} 
		
		
		batch.end();
		
		if(debugTrue) debugRenderer.render(world, camera.combined);
		world.step(timestep, 8, 3);		

		
	}
	@Override
	public void resize(int width, int height) {		
		camera.viewportWidth = width / 15;
		camera.viewportHeight = height / 15;
		camera.update();
	}
	@Override
	public void show() {
		
		float myScale = 0.9f;
		
		//Setup Map Loader
		map = new TmxMapLoader().load("data/myTiledMap.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 10*myScale);
	
		//End Map Setup
	    myForklift = new Forklift(world,11f);//Default scale is 20
	    myLevel = new Level(world,800*myScale);
	    
	    bomb = new Bomb(world,4);
		//platform(79);
		Gdx.input.setInputProcessor(myForklift);
		
	}
	@Override
	public void hide() {
		
	}
	@Override
	public void pause() {
		
	}
	@Override
	public void resume() {	
		
	}
	@Override
	public void dispose() {
		world.dispose();
		batch.dispose();
		debugRenderer.dispose();
	}
	public void platform(float y){
		// Create our body definition
		BodyDef groundBodyDef =new BodyDef();  
		// Set its world position
		groundBodyDef.position.set(new Vector2(0, y));  

		// Create a body from the defintion and add it to the world
		Body groundBody = world.createBody(groundBodyDef);  

		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();  
		// Set the polygon shape as a box which is twice the size of our view port and 20 high
		// (setAsBox takes half-width and half-height as arguments)
		groundBox.setAsBox(600, 1);
		// Create a fixture from our polygon shape and add it to our ground body  
		groundBody.createFixture(groundBox, 0.0f); 
		// Clean up after ourselves
		groundBox.dispose();
	}
	
	
}
