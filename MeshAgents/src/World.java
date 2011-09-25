import processing.core.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import java.util.*;
import toxi.processing.*;
import org.apache.commons.collections.map.*;

public class World {
	 //------------PROPERTIES
	WETriangleMesh bounds;
	ArrayList<Agent> agentPop;
	PApplet parent; // The parent PApplet that we will render ourselves onto
	ToxiclibsSupport gfx;
	MultiValueMap links;
	
	World(MeshAgents p, WETriangleMesh BOUNDS, ArrayList<Agent> POP) {
		parent = p;
		bounds = BOUNDS;
		agentPop = POP;
		gfx=new ToxiclibsSupport(parent);
		links = new MultiValueMap();
		
	}
	 //------------METHODS
	public void run(){
	   updatePop();
	   
	   //----------------DRAW MESH WIRES
	   /*parent.noFill();
	   parent.stroke(25);
	   gfx.mesh(bounds,false,0);
	   */
	   //----------------
	 }
	 
	 
	 public void updatePop(){
	  for (int i =0;i<agentPop.size();i++){
	    Agent a = (Agent) agentPop.get(i);
	    a.run();
	  }
	 }
	 
	 public void populate(){
	   //create a population of agents
	   for (int i =0;i<7000;i++){
	     int f = (int)parent.random(0,bounds.faces.size()-1);
	     WEFace F = (WEFace) bounds.faces.get(f);
	     Agent a = new Agent(parent,this, new Vec2D(parent.random(-1,1),parent.random(-1,1)),F);
	     agentPop.add(a);
	   }
	 }

}