//----------------------------------------------------IMPORTS
import processing.core.*;
import saito.objloader.*;
import peasy.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import java.util.*;
import processing.video.*;

public class MeshAgents extends PApplet {
	
	private static final long serialVersionUID = 1L;
	//----------------------------------------------------GLOBALS
	World world;
	OBJModel nMesh;
	WETriangleMesh mesh;
	PeasyCam cam;
	MovieMaker mm;
	
  public void setup() {
	  size(800,600,P3D);
	  cam = new PeasyCam(this,1000);
	  //mm = new MovieMaker(this, width, height, "drawing.mov", 30, MovieMaker.ANIMATION, MovieMaker.HIGH);

	  mesh= initMeshes("spiky.obj");

	  //create the world
	  world = new World(this, mesh, new ArrayList<Agent>());
	  world.populate();
  }

  public void draw() {
	  background(10);
	  stroke(2);
	  world.run();
	 // mm.addFrame();
	  if(frameCount == 50){
		  print(frameRate);
	  }
  }


  public void keyPressed() {
	  if (key == ' ') {
	    //mm.finish();  // Finish the movie if space bar is pressed!
	  }
	}
  
	//function for converting OBJModel to WETriangle mesh
	public WETriangleMesh initMeshes (String source) {
	  //create new WETriangleMesh instance to return
	  WETriangleMesh initMesh = new WETriangleMesh();
	  
	  //load in the model using the source string supplied
	  //to the function
	  nMesh = new OBJModel(this, source, "absolute");
	  //get the first segment in the mesh in order to access the faces
	  //this is just a nuance of the OBJModel class
	  Segment seg = nMesh.getSegment(0);
	  
	  //loop through the faces
	  for (int i = 0; i < seg.getFaceCount(); i++) {
	    //get a face
	    saito.objloader.Face face = seg.getFace(i);
	    //get an array of the vertices in the face (always a triangle)
	    PVector[] verts = face.getVertices();
	    
	    //convert the verts to Vec3D to construct a face for the
	    //new WETriangleMesh
	    Vec3D a = new Vec3D(verts[0].x, verts[0].y, verts[0].z);
	    Vec3D b = new Vec3D(verts[1].x, verts[1].y,verts[1].z);
	    Vec3D c = new Vec3D(verts[2].x, verts[2].y, verts[2].z);
	    initMesh.addFace(a, b, c);
	    
	  }
	  
	  return initMesh;
	  
	}
}