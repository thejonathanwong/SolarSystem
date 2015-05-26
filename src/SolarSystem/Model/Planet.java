package SolarSystem.Model;

import java.util.Random;
import org.lwjgl.opengl.GL11;

import SolarSystem.View.PSphere;
/**
 * Created by jonathan on 5/8/2015.
 */
public class Planet {

    public RigidBody rb; //contains information about position and velocity
    public float radius; //actual value of planet's orbital radius
    public float dr; // display radius

    private PSphere s; //actual sphere
    private float[] colour; //colour of the planet

    //scales values to viewable sizes
    public final float distanceScale = (float) (0.5 * Math.pow(10,-11));
    public final float radiusScale = (float) (0.2 * Math.pow(10,-8));

    public Planet(RigidBody rb, float radius) {
        this.rb = rb;
        this.radius = radius;
        this.dr = radius*radiusScale;
//        s = new PSphere(this.dr);
    }

    //constructor
    public Planet(float mass, float radius, float x, float y, float z) {
        this.rb = new RigidBody(mass, x, y, z);
        this.radius = radius;
        this.dr = radius*radiusScale;

        s = new PSphere(this.dr);

        //randomly generates colour
        Random rand = new Random();
        colour = new float[] {rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};
    }


    //draws the planet
    public void draw() {
        GL11.glPushMatrix();

        //sets the position based on values stored in the planet's RigidBody
        //values inside RigidBody determined by Runge-Kutta method of order 4
        GL11.glTranslatef(rb.x*distanceScale, rb.y*distanceScale, rb.z*distanceScale);
        GL11.glColor3f(colour[0], colour[1], colour[2]);

        s.draw(); //calls PSphere's draw method
        GL11.glPopMatrix();
    }

    //used for debugging
    public void printInfo() {
        System.err.println("planet pos" + rb.x*distanceScale + " " + rb.y*distanceScale + " " + rb.z*distanceScale);
        System.err.println("planet vel" + rb.vx*distanceScale + " " + rb.vy*distanceScale + " " + rb.vz*distanceScale);
    }
}
