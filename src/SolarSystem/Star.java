package SolarSystem;

/**
 * Created by jonathan on 5/8/2015.
 */
public class Star {

    private float mass;
    private float radius;
    private final float G = (float) (6.67384 * Math.pow(10, -11));

    public PSphere s;
    private float dr; //display radius
    private static final float radiusScale = (float) (0.2 * Math.pow(10,-9));


    //constructor
    public Star(float m, float r) {
        mass = m;
        radius = r;
        dr = radius*radiusScale;
        s = new PSphere(dr);
    }

    //calculates acceleration due to the star at a given position
    public float calcGrav(double x, double z) {
        // Newton's Universal Law of Gravitation
        // F = G * M * m / r^2
        // a = G * M / r^2

        float grav = (float) (-1*(G*mass)/(x*x + z*z));
//        System.err.println("calcGrav: " + grav + " " + x*x + " " + z*z);

        return grav;
    }

    public float calcGrav(double x) {
        float grav = 0;
        if(x != 0) {
            grav = (float) (-1 * (G * mass) / (x * x));
        }
        System.err.println("calcGrav grav: " + grav + " " + G + " " + mass + " " + x + " " + x*x);
        return grav;
    }

    public float getMass() {
        return mass;
    }

    public float getG() {
        return G;
    }
}
