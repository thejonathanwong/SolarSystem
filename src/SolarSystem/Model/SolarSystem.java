package SolarSystem.Model;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Disk;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by jonathan on 5/8/2015.
 */
public class SolarSystem {

    public ArrayList<Planet> planets; //arraylist of planets
    public Star star;

    private final float h = 10000f; //time step
                                    //its large to speed up animation
    private boolean orbits = true;

    // basic constructor calling initSol method
    public SolarSystem() { initSol(); }


    // method to build our solar system
    public void initSol() {

        //masses in kg
        float sunM = (float) (1.989e30);
        float mercuryM = (float) (328.5e21);
        float venusM = (float) (4.867e24);
        float earthM = (float) (5.972e24);
        float marsM = (float) (639e21);
        float jupiterM = (float) (1.898e27);
        float saturnM = (float) (568.3e24);
        float uranusM = (float) (86.81e24);
        float neptuneM = (float) (102.4e24);
//        float plutoM = (float) (1.309e22);


        //radius of planet in metres
        double Mm = Math.pow(10,6);
        float sunR = (float) (695.8 * Mm);
        float mercuryR = (float) (2.44 * Mm);
        float venusR = (float) (6.052 * Mm);
        float earthR = (float) (6.371 * Mm);
        float marsR = (float) (3.39 * Mm);
        float jupiterR = (float) (69.911 * Mm);
        float saturnR = (float) (58.232 * Mm);
        float uranusR = (float) (25.362 * Mm);
        float neptuneR = (float) (24.622 * Mm);
//        float plutoR = (float) (1.184 * Mm);


        //starting positions in metres
        //all planets start on the positive x axis
        double hMkm = Math.pow(10,11);
        float mercuryP = (float) (0.5791 * hMkm);
        float venusP = (float) (1.082 * hMkm);
        float earthP = (float) (1.495 * hMkm);
        float marsP = (float) (2.279 * hMkm);
        float jupiterP = (float) (7.785 * hMkm);
        float saturnP = (float) (14.33 * hMkm);
        float uranusP = (float) (28.77 * hMkm);
        float neptuneP = (float) (45.03 * hMkm);
//        float plutoP = (float) (59.063 * hMkm);



        //creates celestial objects
        Star sol = new Star(sunM, sunR);
        Planet mercury = new Planet(mercuryM, mercuryR, mercuryP, 0, 0);
        Planet venus = new Planet(venusM, venusR, venusP, 0, 0);
        Planet earth = new Planet(earthM, earthR, earthP, 0, 0);
        Planet mars = new Planet(marsM, marsR, marsP, 0, 0);
        Planet jupiter = new Planet(jupiterM, jupiterR, jupiterP, 0, 0);
        Planet saturn = new Planet(saturnM, saturnR, saturnP, 0, 0);
        Planet uranus = new Planet(uranusM, uranusR, uranusP, 0, 0);
        Planet neptune = new Planet(neptuneM, neptuneR, neptuneP, 0, 0);
//        Planet pluto = new Planet(plutoM, plutoR, plutoP, 0, 0);

        star = sol;

        //adds planets to Planet array list
        planets = new ArrayList<Planet>();
        planets.add(mercury);
        planets.add(venus);
        planets.add(earth);
        planets.add(mars);
        planets.add(jupiter);
        planets.add(saturn);
        planets.add(uranus);
        planets.add(neptune);
//        planets.add(pluto);

        //calculates inital velocity
        setInitV();
    }

    //sets initial velocity for each planet based on
    // a = G*M/r^2
    // a = v^2/r
    public void setInitV() {
        if(star != null && planets.size() > 0) {
            double num = star.getG()*star.getMass();
            for(Planet p : planets) {
                p.rb.vx = 0;
                p.rb.vy = 0;
                p.rb.vz = (float) (Math.sqrt(num/p.rb.x));
            }
        }
    }

//	//lighting the planets
//	private void plighting() {
//		float spec[] = new float[]{0.1f,0.1f,0.1f,1};
//		FloatBuffer mat_specular = BufferUtils.createFloatBuffer(4).put(spec);//FloatBuffer.wrap(spec);//.allocate(4).put(spec);
//		mat_specular.flip();
//
//		float mat_shininess = 10;
//
//		float em[] = {0.01f,0.01f,0.01f,1};
//		FloatBuffer emission = BufferUtils.createFloatBuffer(4).put(em);
//		emission.flip();
//
//		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, emission);
//		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, mat_specular);
//		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, mat_shininess);
//
//	}
//
//	//lighting for the sun
//	//has star that emits light
//	private void slighting() {
//		//emit yellow
//		float em[] = {0.8f,0.8f,0,1};
//		FloatBuffer emission = BufferUtils.createFloatBuffer(4).put(em);
//		emission.flip();
//
//		//position within the star
//		float lp[] = {0,0,0,1};
//		FloatBuffer light_position = BufferUtils.createFloatBuffer(4).put(lp);//FloatBuffer.wrap(lp);
//		light_position.flip();
//
//		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, light_position);
//		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, emission);
//
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		GL11.glEnable(GL11.GL_LIGHTING);
//		GL11.glEnable(GL11.GL_LIGHT0);
//	}

//	//draws entire system
//	public void draw(float t) {
//		slighting();
//		star.s.draw();
//		plighting();
//		for(Planet p : planets) {
//            RigidBody rb = p.rb;
//
//            //calls runge-kutta method of order 4 to calc next step
//            rk(rb);
////                p.printInfo();
//			p.draw();
//
//            //draw orbital rings
//            if(orbits) {
//                GL11.glPushMatrix();
//                GL11.glRotatef(90, 1, 0, 0);
//                GL11.glColor3f(1f, 1f, 1f);
//                Disk d = new Disk();
//                float scale = p.distanceScale;
//                float orbitRad = (float) Math.sqrt(rb.x * rb.x * scale * scale + rb.z * rb.z * scale * scale);
//                float thick = 0.005f;
//                if(p.dr < 0.01) {
//                    thick = 0.001f;
//                }
//                d.draw(orbitRad - thick, orbitRad, 100, 4);
//                GL11.glPopMatrix();
//            }
//		}
//	}


    // runge-kutta method of order 4
    // takes in velocity and position at current time and uses runge-kutta to calculate
    // its position and velocity at the next time step
    //
    // basic method is as follows where x0 is the starting value and h is a time step
    // k1 = x0 * h;
    // k2 = (x0 + k1/2) * h;
    // k3 = (x0 + k2/2) * h;
    // k4 = (x0 + k3) * h;
    // x1 = x0 + 1/6 * (k1 + 2*k2 + 2*k3 + k4);
    //
    // does this component-wise
    public void rk(RigidBody rb) {
        //initial value for position
        float x0 = rb.x;
        float z0 = rb.z;
        float rr = (float) (Math.sqrt(x0*x0 + z0*z0)); // radius of orbit

        //initial value for velocity
        float vx0 = rb.vx;
        float vz0 = rb.vz;

        //k1 for x coordinate
        //uses a helper method to calculate acceleration given a position
        //v = a(position, velocity, time) * h;
        //x = v * h;
        float vxk1 = h * calcAccel(true, x0, z0, rr);
        float pxk1 = h * vx0;

        //k1 for z coordinate
        float vzk1 = h * calcAccel(false, x0,z0,rr);
        float pzk1 = h * vz0;



        //k2
        float vxk2 = h * calcAccel(true, x0+pxk1*0.5f, z0 + pzk1*0.5f, rr);
        float pxk2 = h * (vx0 + vxk1*0.5f);

        float vzk2 = h * calcAccel(false, x0+pxk1*0.5f, z0 + pzk1*0.5f, rr);
        float pzk2 = h * (vz0 + vzk1*0.5f);



        //k3
        float vxk3 = h * calcAccel(true, x0+pxk2*0.5f, z0 + pzk2*0.5f, rr);
        float pxk3 = h * (vx0 + vxk2*0.5f);

        float vzk3 = h * calcAccel(false, x0+pxk2*0.5f, z0 + pzk2*0.5f, rr);
        float pzk3 = h * (vz0 + vzk2*0.5f);



        //k4
        float vxk4 = h * calcAccel(true, x0+pxk3, z0 + pzk3, rr);
        float pxk4 = h * (vx0 + vxk3);

        float vzk4 = h * calcAccel(false, x0+pxk3, z0 + pzk3, rr);
        float pzk4 = h * (vz0 + vzk3);



        //created actual variables for debugging
        float pxout = x0 + (pxk1 + 2*pxk2 + 2*pxk3 + pxk4)/6;
        float pzout = z0 + (pzk1 + 2*pzk2 + 2*pzk3 + pzk4)/6;
        float vxout = vx0 + (vxk1 + 2*vxk2 + 2*vxk3 + vxk4)/6;
        float vzout = vz0 + (vzk1 + 2*vzk2 + 2*vzk3 + vzk4)/6;

        //sets position and velocity to updated values
        rb.x = pxout;
        rb.z = pzout;
        rb.vx = vxout;
        rb.vz = vzout;
    }

    // calculates component of acceleration. true evaluates x component,
    // false evaluates z component
    //
    // ax = a * cos(t);
    // cos(t) = x/r;
    // a = G * M / r^2;
    //
    // ax = G * M * x / r^3
    public float calcAccel(boolean i, float x, float z, float r) {
        float p = z;
        if(i) { p = x; }
        return star.calcGrav(x,z) * p/r;
    }

//    public void toggleOrbits() {
//        orbits = !orbits;
//    }
}
