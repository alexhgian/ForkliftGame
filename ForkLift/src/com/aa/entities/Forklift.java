package com.aa.entities;



import pong.client.core.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

public class Forklift extends InputAdapter{
	private Body vehicle;
	private World world;
	private Body chassis, leftWheel, rightWheel, weightBody;
	WheelJoint leftAxis,rightAxis ;
	WheelJointDef leftAxisDef, rightAxisDef;
	BodyEditorLoader loader;
	
	DistanceJoint weightJoint;
	DistanceJointDef weightJointDef;
	
	private float scale = 20;
	
	private Sprite vehicleSprite, left, right;
	Texture texture, wheelTexture;
	
    public Forklift(World w, float newScale)
    {
    	world = w;
   		FixtureDef fixtureDef = new FixtureDef();
   		FixtureDef wheelFD = new FixtureDef();
   		
   	    texture = new Texture(Gdx.files.internal("gfx/forklift.png"));
   	    wheelTexture = new Texture(Gdx.files.internal("gfx/forklift_wheel.png"));
   	    vehicleSprite = new Sprite(texture);
   	    left = new Sprite(wheelTexture);
   	    right = new Sprite(wheelTexture);
   	    
   		fixtureDef.density = 2f;
   		fixtureDef.friction = .4f;
   		fixtureDef.restitution = 0.1f;
   		
   		
   		wheelFD.density = 2f;
   		wheelFD.friction = 200f;
   		wheelFD.restitution = 0.1f;   		
   		
   		scale = newScale;
    	createVehicle(w,wheelFD,0,0,scale);
    }
	private void createVehicle(World w, FixtureDef wheelFD, float x, float y, float scale) {
	    float xOffset = 42.5f*scale/100;
	    float yOffset = 20f*scale/100;
	    
	    System.err.println(xOffset);
		// 0. Create a loader for the file saved from the editor.
	    loader = new BodyEditorLoader(Gdx.files.internal("data/forklift.json"));
	 
	    // 1. Create a BodyDef, as usual.
	    BodyDef chassis = new BodyDef();
	    chassis.position.set(450, 35);
	    chassis.type = BodyType.DynamicBody;
	   
	    // 2. Create a FixtureDef, as usual.
	    FixtureDef fd = new FixtureDef();
	    fd.density = 0.5f;
	    fd.friction = 0.75f;
	    fd.restitution = 0.3f;
	 
	    // 3. Create a Body, as usual.
	    vehicle = world.createBody(chassis);
	    vehicle.setUserData(this);
	    // 4. Create the body fixture automatically by using the loader.
	    loader.attachFixture(vehicle, "vehicle", fd, scale);
	    
	    /*-------------Wheels-------------*/
		//left wheel
		CircleShape wheelShape = new CircleShape();
		wheelShape.setRadius(scale/11);
		
		wheelFD.shape = wheelShape;
		//left wheel
		leftWheel = w.createBody(chassis);
		leftWheel.createFixture(wheelFD);
		leftWheel.setUserData(new Wheel(0.039f*scale/20));
		//right wheel
		rightWheel = w.createBody(chassis);
		rightWheel.createFixture(wheelFD);
		rightWheel.setUserData(new Wheel(0.039f*scale/20));
		//left axis
		WheelJointDef axisDef = new WheelJointDef();
		axisDef.bodyA = vehicle;
		axisDef.bodyB = leftWheel;
		
		axisDef.frequencyHz = 4f;
		System.err.println("Density: " + fd.density);
		axisDef.maxMotorTorque = fd.density*300;
		axisDef.localAnchorA.set(xOffset, yOffset);
		axisDef.localAxisA.set(Vector2.Y);//Move freely in the y direction only		
		
		leftAxis = (WheelJoint) world.createJoint(axisDef);
		
		//right axis
		axisDef.bodyB = rightWheel;
		axisDef.localAnchorA.x *=+2.1;	
		rightAxis = (WheelJoint) world.createJoint(axisDef);		

		wheelShape.dispose();
		
		addWeight(vehicle);
	}
	
	public void addWeight(Body vehicleBody)
	{		
		//Create shape for FixtureDef
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(1f, 1f);
		
	    //Create FixtureDef for Body
	    FixtureDef fd = new FixtureDef();
	    fd.density = 0f;
	    //fd.friction = 0.5f;
	    //fd.restitution = 0.3f;
	    fd.shape = boxShape;
	    fd.isSensor = true;
	    
	    //Create BodyDef for the Body
	    BodyDef bodydef = new BodyDef();
	    bodydef.position.set(450, 20);
	    bodydef.type = BodyType.DynamicBody;
	
	    
	    //Create the Body in the world and createFixture
	    Body boxBody = world.createBody(bodydef);
	    boxBody.createFixture(fd);
	    
	    //Dispose of used shape
	    boxShape.dispose();
	    
	    // DistanceJoint
	    DistanceJointDef distanceJointDef = new DistanceJointDef();
	    distanceJointDef.bodyA = vehicleBody;
	    distanceJointDef.bodyB = boxBody;	    
	    distanceJointDef.length = 0;
	    
	    Vector2 center = vehicleBody.getLocalCenter();
	    distanceJointDef.localAnchorA.set(center.x, center.y-10);
	    
	    //Add create the joint between the two bodies
	    world.createJoint(distanceJointDef);	    
	}
	
	public Body getVehicle()
	{
		return vehicle;
	}
	
	public Vector2 getOrigin()
	{
		Vector2 modelOrigin = loader.getOrigin("vehicle", scale).cpy();
		//System.err.println("(x,y):(" + modelOrigin.x + "," + modelOrigin.y + ")");
		return modelOrigin;
	}
	
	public Sprite getSprite()
	{			
		return vehicleSprite;
	}
	
	
	public Sprite getUpdatedSprite()
	{
	    
		//Put this stuff inside forklift--------------//
		Vector2 origin = this.getOrigin();
		Body modelBody = this.getVehicle();
		
		Vector2 modelPos = modelBody.getPosition().sub(origin);		
		Sprite modelSprite = this.getSprite();
		
		//System.err.println("(x,y):(" + modelPos.x + "," + modelPos.y + ")");
		modelSprite.setPosition(modelPos.x, modelPos.y);
		modelSprite.setOrigin(origin.x, origin.y);
		modelSprite.setRotation(modelBody.getAngle() * MathUtils.radiansToDegrees);
		modelSprite.setScale(0.039f*scale/20);
		return modelSprite;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {

		case Keys.A:		
			leftAxis.enableMotor(true);
			leftAxis.setMotorSpeed(100);
			rightAxis.enableMotor(true);
			rightAxis.setMotorSpeed(100);
			break;
		case Keys.D:
			leftAxis.enableMotor(true);
			leftAxis.setMotorSpeed(-100);
			rightAxis.enableMotor(true);
			rightAxis.setMotorSpeed(-100);
			break;
		case Keys.S:
			leftAxis.enableMotor(true);
			leftAxis.setMotorSpeed(0);
			rightAxis.enableMotor(true);
			rightAxis.setMotorSpeed(0);
			break;
		default:
			return false;
		}
		return true;
	}
	public boolean keyUp(int keycode) {
		System.err.println("keyUp");

		
		leftAxis.enableMotor(false);
		rightAxis.enableMotor(false);
		return true;
	}

}
