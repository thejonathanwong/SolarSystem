package SolarSystem.Model;



/**
 * Created by jonathan on 5/7/2015.
 */
public class RigidBody {

    public float mass;
//    public Matrix3f IBody;
//    public Matrix3f InvIBody;

    // state variables
    public float x, y, z;
//    public Matrix3f R;
//    public Matrix3f P;
//    public Matrix3f L;

    // derived quantities
//    public Matrix3f Iinv;
    public float vx, vy, vz;

    // computed variables
//    public float fx, fy, fz;
//    public float tx, ty, tz;


    public RigidBody(float mass, float x, float y, float z) {
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
