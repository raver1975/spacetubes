package com.quailshillstudio;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

public class GroundFixture{
	private Array<float[]> verts = new Array<float[]>();
	private Array<Fixture> fixtureRefs;
//	private Body body;
	
	public GroundFixture(Array<float[]> verts){
		this.setVerts(verts);
	}

//	public Body getBody() {
//		return this.body;
//	}
//
//	public void setBody(Body nground) {
//		this.body = nground;
//	}
	
	public Array<Fixture> getFixtures() {
		return this.fixtureRefs;
	}
	
	public void setFixtures(Array<Fixture> fixtures) {
		this.fixtureRefs = fixtures;
	}

	public Array<float[]> getVerts() {
		return verts;
	}

	public void setVerts(Array<float[]> verts) {
		this.verts = verts;
	}
}
