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
	HashMap displayFaces;
	Vec3D lastP;
	
	World(MeshAgents p, WETriangleMesh BOUNDS, ArrayList<Agent> POP) {
		parent = p;
		bounds = BOUNDS;
		agentPop = POP;
		gfx=new ToxiclibsSupport(parent);
		links = new MultiValueMap();
		displayFaces = new HashMap(bounds.faces.size());
		lastP = new Vec3D(0,0,0);
	}
	 //------------METHODS
	public void run(){
	   
	   updatePop();
	   //----------------DRAW MESH
	   //parent.fill(0,0,0);
	  // parent.noStroke();
	   gfx.mesh(bounds,false,0);
	   
	   //----------------
	 }
	

	 
	public void updatePop(){
				// create one thread per agent
				for (int i =0;i<agentPop.size();i++) {
					Agent a = (Agent) agentPop.get(i);
					a.run();
				}

	 }
	
	/* multi thread experiment - agent must implement Runnable
	public void updatePop(){
		ExecutorService executor = Executors.newFixedThreadPool(8);
				// create one thread per agent
				for (int i =0;i<agentPop.size();i++) {
					Runnable worker = (Agent) agentPop.get(i);
					executor.execute(worker);
				}
				executor.shutdown();
				// Wait until all threads are finish
				while (!executor.isTerminated()) {

				}
	 }
	 */
	 
	 public void populate(){
	   //create a population of agents
	   for (int i =0;i<12000;i++){
	     int f = (int)parent.random(0,bounds.faces.size()-1);
	     WEFace F = (WEFace) bounds.faces.get(f);
	     Agent a = new Agent((MeshAgents)parent,this, new Vec2D(parent.random(-1,1),parent.random(-1,1)),F);
	     agentPop.add(a);
	   }
	 }
	 
}