package SolarSystem.View;

import org.lwjgl.opengl.GL11;

public class PSphere {
	
	/*
	 * code from this class has been adapted from the code
	 * given at the end of the chapter of the openGL red book
	 * http://www.glprogramming.com/red/chapter02.html
	 */
	private float radius;
	private int depth = 3;
	private int dl; //displayList of draw commands
	private boolean wire = false;
	
	//
	//
	//constructors
	public PSphere() {
		radius = 1;
		build();
	}
	
	public PSphere(float radius) {
		this.radius = radius;
		build();
	}

	public PSphere(float radius, int depth) {
		this.depth = depth;
		this.radius = radius;
		build();
	}
	
	//
	//
	//public methods
	public float getRadius() {
		return this.radius;
	}

	public void setRadius(float rad) {
		this.radius = rad;
		build();
	}
	
	//switch between normal and wire view
	public void toggleWire() {
//	public void setWire(boolean wire) {
//		this.wire = wire;
		this.wire = !this.wire;
		build();
	}
	
	public void setDepth(int d) {
		this.depth = d;
	}
	

	//actual openGL draw method
	private void drawTriangle(float v1[], float v2[], float v3[]) { 
		if(v1.length != 3 || v2.length != 3 || v3.length != 3) {
			System.out.println("Cannot draw triangle: Not 3 elements in a vertex");

		}
//		if(wire) {
//			GL11.glBegin(GL11.GL_LINE_STRIP);
//		} else {
			GL11.glBegin(GL11.GL_TRIANGLES); 
//		}
		GL11.glNormal3f(v1[0],v1[1],v1[2]); GL11.glVertex3f(v1[0],v1[1],v1[2]);    
		GL11.glNormal3f(v2[0],v2[1],v2[2]); GL11.glVertex3f(v2[0],v2[1],v2[2]);    
		GL11.glNormal3f(v3[0],v3[1],v3[2]); GL11.glVertex3f(v3[0],v3[1],v3[2]);    
		GL11.glEnd(); 
	}

	//normalizes
	private void normalize(float v[]) {    
		float d = (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]); 
		if (d == 0.0) {
			System.out.println("Zero length vector");
			//		      error("zero length vector");    
			System.exit(1);
			return;
		}
		d /= this.radius;
		v[0] /= d; v[1] /= d; v[2] /= d; 
	}

	//subdivides the given triangle into smaller triangles 
	//does this depth times
	private void subdivide(float v1[], float v2[], float v3[], long depth)
	{
		float v12[] = new float[3], v23[] = new float[3], v31[] = new float[3];
		int i;

		if (depth == 0) {
			drawTriangle(v1, v2, v3);
			return;
		}
		for (i = 0; i < 3; i++) {
			v12[i] = v1[i]+v2[i];
			v23[i] = v2[i]+v3[i];
			v31[i] = v3[i]+v1[i];
		}
		//		System.out.println(depth);
		normalize(v12);
		normalize(v23);
		normalize(v31);
		subdivide(v1, v12, v31, depth-1);
		subdivide(v2, v23, v12, depth-1);
		subdivide(v3, v31, v23, depth-1);
		subdivide(v12, v23, v31, depth-1);
	}

	//builds displayList command
	private void build() {
		if(wire) 
			setDepth(2);
		else
			setDepth(3);

		float X = this.radius * ((float) 0.525731112119133606 );
		float Z = this.radius * ((float) 0.850650808352039932 );
		//		X *= this.radius;
		//		Z *= this.radius;
		float vdata[][] = {    
				{-X, 0, Z}, {X, 0, Z}, {-X, 0, -Z}, {X, 0, -Z},    
				{0, Z, X}, {0, Z, -X}, {0, -Z, X}, {0, -Z, -X},    
				{Z, X, 0}, {-Z, X, 0}, {Z, -X, 0}, {-Z, -X, 0} 
		};
		int tindices[][] = { 
				{0,4,1}, {0,9,4}, {9,5,4}, {4,5,8}, {4,8,1},    
				{8,10,1}, {8,3,10}, {5,3,8}, {5,2,3}, {2,7,3},    
				{7,10,3}, {7,6,10}, {7,11,6}, {11,0,6}, {0,1,6}, 
				{6,1,10}, {9,0,11}, {9,11,2}, {9,2,5}, {7,2,11} 
		};
		dl = GL11.glGenLists(1);
		GL11.glNewList(dl, GL11.GL_COMPILE);
		for (int index = 0; index < 20; index++) {    
			subdivide(vdata[tindices[index][0]],vdata[tindices[index][1]],vdata[tindices[index][2]],this.depth);
		}
		GL11.glEndList();
	}
	
	//actual draw method
	public void draw() {
		GL11.glCallList(dl);
	}
}
