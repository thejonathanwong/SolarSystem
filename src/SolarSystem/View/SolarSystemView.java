package SolarSystem.View;

import SolarSystem.Model.Planet;
import SolarSystem.Model.RigidBody;
import SolarSystem.Model.SolarSystem;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Disk;

import java.nio.FloatBuffer;

/**
 * Created by jonathan on 5/25/2015.
 */
public class SolarSystemView {

    private SolarSystem s;

    private static boolean orbits = true;

    public SolarSystemView(SolarSystem solarSystem) {
        this.s = solarSystem;
    }

    public void setSS(SolarSystem s) {
        this.s = s;
    }

    //lighting the planets
    private void plighting() {
        float spec[] = new float[]{0.1f,0.1f,0.1f,1};
        FloatBuffer mat_specular = BufferUtils.createFloatBuffer(4).put(spec);//FloatBuffer.wrap(spec);//.allocate(4).put(spec);
        mat_specular.flip();

        float mat_shininess = 10;

        float em[] = {0.01f,0.01f,0.01f,1};
        FloatBuffer emission = BufferUtils.createFloatBuffer(4).put(em);
        emission.flip();

        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, emission);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, mat_specular);
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, mat_shininess);

    }

    //lighting for the sun
    //has star that emits light
    private void slighting() {
        //emit yellow
        float em[] = {0.8f,0.8f,0,1};
        FloatBuffer emission = BufferUtils.createFloatBuffer(4).put(em);
        emission.flip();

        //position within the star
        float lp[] = {0,0,0,1};
        FloatBuffer light_position = BufferUtils.createFloatBuffer(4).put(lp);//FloatBuffer.wrap(lp);
        light_position.flip();

        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, light_position);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, emission);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
    }

    //draws entire system
    public void draw(float t) {
        slighting();
        s.star.s.draw();
        plighting();
        for(Planet p : s.planets) {
            RigidBody rb = p.rb;

            //calls runge-kutta method of order 4 to calc next step
            s.rk(rb);
//                p.printInfo();
            p.draw();

            //draw orbital rings
            if(orbits) {
                GL11.glPushMatrix();
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glColor3f(1f, 1f, 1f);
                Disk d = new Disk();
                float scale = p.distanceScale;
                float orbitRad = (float) Math.sqrt(rb.x * rb.x * scale * scale + rb.z * rb.z * scale * scale);
                float thick = 0.005f;
                if(p.dr < 0.01) {
                    thick = 0.001f;
                }
                d.draw(orbitRad - thick, orbitRad, 100, 4);
                GL11.glPopMatrix();
            }
        }
    }

    public void draw(){

    }

    public void toggleOrbits() {
        orbits = !orbits;
    }
}
