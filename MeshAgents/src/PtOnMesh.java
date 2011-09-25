import toxi.geom.*;
import toxi.geom.mesh.*;

public class PtOnMesh {
  //------------PROPERTIES
  Vec2D loc;
  Vec2D vel;
  WEFace onFace;
  float es;
  WingedEdge onEdge;
  float interp;
  int count;
  float minVel;
  float speed;
  Vec3D meshPos;
  World world;
  
  //------------CONSTRUCTOR
  PtOnMesh (World W, Vec2D VEL, WEFace F) {
    vel = VEL;
    speed = 1;
    vel.normalizeTo(speed);
    onFace = F;
    Vec3D c =onFace.getCentroid();
    loc = new Vec2D(c.x, c.y);
    interp = 0;
    count = 0;
    minVel = 0;
    es = (float) 0.3;
    meshPos = new Vec3D(0,0,0);
    world = W;
    
    //initialise map reference
    world.links.put(onFace,this);
  } 
  //------------METHODS

  public void update() {
    count =0;
    //reset velocity
    minVel = 0;
    vel.normalizeTo(speed);

    if (checkEdge()) {
      updatePos(onFace);
    }
    //if not, check to make sure still on the right face
    else {
      if (checkFace(loc.add(vel))) {
        //move
        loc.addSelf(vel);
      } 
      else {
        //rotate vel a little bit and subtract from pos
        loc.subSelf(vel);
        vel.rotate(((float)0.1));
      }
    }
  }

  public Boolean checkEdge() {
    //create a line from the current pos to the possible
    //next position.
    Vec2D nextPt = loc.add(vel);
    Line2D trace = new Line2D(loc, nextPt);
    float minDist = 0;
    boolean found = false;
    //find all intersections with edges and use the one furthest away 
    //(skips entire face)

    for (int i=0;i<onFace.edges.size();i++) {
      //convert to 2d and do edge intersection
      WingedEdge e = onFace.edges.get(i);
      Vec2D stPt = e.a.to2DXY();
      Vec2D endPt = e.b.to2DXY();
      Line2D edgeLine = new Line2D(stPt, endPt);
      Line2D.LineIntersection isec=trace.intersectLine(edgeLine);

      //if found intersection then change the face association and vectors
      if (isec.getType()==Line2D.LineIntersection.Type.INTERSECTING) {
        //get intersection point
        Vec2D pos=isec.getPos();
        //check if further than last other intersections
        if (loc.distanceTo(pos)>minDist) {
          //reset min distance
          minDist = loc.distanceTo(pos);
          //find percentage of this point along the edge
          //set properties
          interp = stPt.distanceTo(pos)/edgeLine.getLength();
          onEdge = e;
          found = true;
        }
      }
    }
    //if no intersections found return false
    if (found==true) {
      minVel = minDist;
      return true;
    }
    else {
      return false;
    }
  }


  public void updatePos(WEFace lastFace) {
    //create reference vector relative to face centroid  
    //get index of the current face in the edge associations
    int o = onEdge.faces.indexOf(onFace);
    Vec2D currentEdgeVec = new Vec2D((onEdge.b.x-onEdge.a.x), (onEdge.b.y-onEdge.a.y));
    Vec2D c = currentEdgeVec.getPerpendicular();
    //find the other face and set it as current

    if (onEdge.faces.size()>1) {
      if (o==0) {
        o =1;
      }
      else {
        o=0;
      }
    }
    else {
      //hitting an edge with no shared faces. bounce.
      vel.reflect(currentEdgeVec.normalize());
      loc.addSelf(vel);
      return;
    }

    onFace = onEdge.faces.get(o);
    
    //update map references
    world.links.remove(lastFace,this);
    world.links.put(onFace,this);
    
    //find the shared edge
    for (WingedEdge e: onFace.edges) {
      //check if they share the same face
      if (e.faces.contains(lastFace)) {
        //dont bother with same edge
        //set location on new edge
        Vec2D edgeVec = new Vec2D((e.b.x-e.a.x), (e.b.y-e.a.y));
        Vec2D n = edgeVec.getPerpendicular();
        Vec2D minN = n.copy();
        minN.normalizeTo(1);

        Vec2D edgeEnd = e.a.to2DXY();

        //if perp vector is inside face, then use
        //st -> end. otherwise flip.
        if (e == onEdge) {
          edgeVec.scaleSelf(interp);
        }
        else {
          if (checkFace(edgeVec.add(minN))) {
            edgeVec.scaleSelf(interp);
          }
          else {
            edgeVec.scaleSelf(1-interp);
          }
        }

        //reset edge reference
        onEdge = e;
        loc = edgeEnd.add(edgeVec);

        //update velocity based on face uvs
        updateVel(c, n);
        return;
      }
    }
  }

  public void updateVel(Vec2D C, Vec2D N) {
    //do recursive check
    count++;
    if (count>8) {
      vel=new Vec2D(0, 0);
      return;
    }

    vel.normalizeTo(speed-minVel);

    //remember velocity and normalize vectors
    float l = vel.magnitude();
    C.normalizeTo(l);
    N.normalizeTo(l);
    //create angles relative to edges
    float angleA = vel.heading()-C.heading();
    float angleB = -((float)Math.PI-angleA);
    vel = N.copy();
    vel.rotate(angleA);

    //check if velA puts ptOnMesh on face
    if (checkFace(loc.add(vel))) {
      loc.addSelf(vel);
    } 
    else {
      //if not, check to see if velA pushed over another edge
      if (checkEdge()) {
        updatePos(onFace);
      }
      else {
        //check to see if velB puts ptOnMesh on face
        vel = N.copy();
        vel.rotate(angleB);
        if (checkFace(loc.add(vel))) {
          loc.addSelf(vel);
        }
        else {
          //if not check to see velB pushing it over another edge
          if (checkEdge()) {
            updatePos(onFace);
          }
        }
      }
    }
  }

  public Boolean checkFace(Vec2D push) {

    Vec2D a = onFace.a.to2DXY();
    Vec2D b = onFace.b.to2DXY();
    Vec2D c = onFace.c.to2DXY();

    if (push.isInTriangle(a, b, c)) {
      return true;
    }
    else {
      return false;
    }
  }

  public void to3d() {
    TriangleIntersector tPt = new TriangleIntersector(onFace.toTriangle());
    Vec3D cPos = new Vec3D(loc.x, loc.y, 0);

    Ray3D cR = new Ray3D(cPos, new Vec3D(0, 0, 1));

    if (tPt.intersectsRay(cR)) {
      IsectData3D ptData = tPt.getIntersectionData();
      meshPos = ptData.pos.copy();
    }
    else {
      cPos = new Vec3D(loc.x, loc.y, 500);
      cR = new Ray3D(cPos, new Vec3D(0, 0, -1));
      if (tPt.intersectsRay(cR)) {
        IsectData3D ptData = tPt.getIntersectionData();
        meshPos = ptData.pos.copy();
      }
    }
  }
}
