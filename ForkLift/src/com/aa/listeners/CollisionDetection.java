package com.aa.listeners;

import com.aa.entities.Bomb;
import com.aa.entities.Forklift;
import com.aa.entities.Level;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;




public class CollisionDetection implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		//System.err.println("Collision");
	    Body a = contact.getFixtureA().getBody();
	    Body b = contact.getFixtureB().getBody();
	    Object objA = a.getUserData();
	    Object objB = b.getUserData();
	    /*
	    if( objA instanceof Bullet)
	    	System.err.println("ObjA is Bullet");
	    if( objA instanceof Tank)
	    	System.err.println("ObjA is Tank");
	    if( objB instanceof Bullet)
	    	System.err.println("ObjB is Bullet");
	    if( objB instanceof Tank)
	    	System.err.println("ObjB is Tank");
	   */
	    
	    if(objA instanceof Forklift && objB instanceof Bomb)
	    {
	    	//System.err.println("Forklift-Bomb Collision");
	    	Bomb myBomb = (Bomb)objB;	
	    	myBomb.stopTimer();
	    	 /*((Tank)objA).hitCount++;
	    	 int count = ((Tank)objA).hitCount;
	    	 System.err.println("Hits: " + count);*/
	    	 
	    	//((Bomb)objB).touchingForklift = true;
	    	
	
	    }else if(objA instanceof Level && objB instanceof Bomb)
	    {
	    	//System.err.println("Level-Bomb Collision");
	    	Bomb myBomb = (Bomb)objB;	
	    	myBomb.resetTimer();
	    	
	    }
		
		
	}
	@Override
	public void endContact(Contact contact) {
		if(contact.getFixtureA() != null && contact.getFixtureB() != null)
		{
		    Body a = contact.getFixtureA().getBody();
		    Body b = contact.getFixtureB().getBody();
		    Object objA = a.getUserData();
		    Object objB = b.getUserData();

		    if(objA instanceof Forklift && objB instanceof Bomb)
		    {
		    	System.err.println("Forklift-Bomb Collision");
		    	Bomb myBomb = (Bomb)objB;	
		    	myBomb.stopTimer();	
		    }
		}
		
	}
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
