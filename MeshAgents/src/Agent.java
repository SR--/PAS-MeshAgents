import toxi.geom.*;
import toxi.geom.mesh.*;
import java.util.*;

import processing.core.*;

public class Agent extends PtOnMesh {

  //your additional properties
  ArrayList<Vec3D> trails;
  PApplet parent;
  int aColour;
  
  Agent(PApplet p, World W, Vec2D VEL, WEFace F) {
    super(W, VEL, F);

    //you additional constructors
    parent = p;
    trails = new ArrayList<Vec3D>();
    aColour = 0;
    
  }

  //your methods
  public void run() {
    
    //p[ut your methods here
    flock();


    //you must call this method to constrain the
    //agent to the mesh
    update();
    to3d();
    
   //render();
   drawTrails();
  }
  
  public void flock() {
	    //some quick and crappy flocking
	    Vec2D accel = new Vec2D(0, 0);
	    
	    //loop through other agents on the face! yew
	    ArrayList<PtOnMesh> neighbours = (ArrayList<PtOnMesh>) world.links.getCollection(onFace);
	    
	    //only loop through the first 50 neighbours
	    int loopMax = neighbours.size();
	    aColour = 0;
	    
	    if(loopMax>=50){
	    	loopMax = 50; //constrain to maximum 50 agents
	    }
	    
	    for (int i = 0; i< loopMax;i++) {
	      //get an agent
	      Agent a = (Agent) neighbours.get(i);

	      //get the distance
	      float d = meshPos.distanceTo(a.meshPos);
	      //only work if d larger than 0
	      if (d>0.1) {
	        //attract
	        if (d<80) {
	        	aColour+=2;
	          Vec3D diff = a.meshPos.copy();
	          diff.subSelf(meshPos);
	          diff.scaleSelf(1/d);
	          //repel
	          if (d<30) {
	            diff.invert();
	            diff.scaleSelf(0.3f);
	          }

	          accel.addSelf(diff.to2DXY());

	          //allign
	          Vec2D otherAgentVel = a.vel.copy();
	          otherAgentVel.scaleSelf(0.8f);
	          accel.addSelf(otherAgentVel);
	        }
	      }
	    }
	    //move
	    accel.normalizeTo(0.25f);
	    vel.addSelf(accel);
	  }
  
  public void drawTrails(){
    trails.add(meshPos);
    if(trails.size()>20){
      trails.remove(0);
    }
    //parent.beginShape();
    for (int i = 0;i<trails.size();i++){
      Vec3D t = (Vec3D) trails.get(i);
  	parent.stroke(((200/((float)trails.size()))*(float)i)+50);
     parent.point(t.x,t.y,t.z);
    }
    //parent.endShape();//
  }
  public void render(){
		  parent.stroke(aColour,aColour*2,aColour*3+50);
		  parent.point(meshPos.x,meshPos.y,meshPos.z);
  }
}