package SolarSystem;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jonathan on 5/13/2015.
 */
public class FinalProject {
    String windowTitle = "3D Shapes";
    public boolean closeRequested = false;

    long lastFrameTime; // used to calculate delta

    float triangleAngle; // Angle of rotation for the triangles
    float quadAngle; // Angle of rotation for the quads
    float totalT;

    //booleans to control buttons
    static boolean orbit = true;
    static boolean pause = false;

    static SolarSystem s;

    int[] _skybox = new int[6];

    public void run() {

        createWindow();
        getDelta(); // Initialise delta timer
        initGL();
        s = new SolarSystem(); //builds solar system

        while (!closeRequested) {
            int d = getDelta();
            pollInput(d);
            updateLogic(d);
            renderGL(s);

            Display.update();
        }

        cleanup();
    }

    private void initGL() {

		/* OpenGL */
        int width = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();

        GL11.glViewport(0, 0, width, height); // Reset The Current Viewport
        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix
        GLU.gluPerspective(45.0f, ((float) width / (float) height), 0.1f, 100.0f); // Calculate The Aspect Ratio Of The Window
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix
        GL11.glLoadIdentity(); // Reset The Modelview Matrix

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        float ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        FloatBuffer lambient = BufferUtils.createFloatBuffer(4).put(ambient);//FloatBuffer.wrap(spec);//.allocate(4).put(spec);
        lambient.flip();
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, lambient);
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enables Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0f); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Test To Do
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // Really Nice Perspective Calculations

        //reads in skybox images and stores into texture
        BufferedImage[] skybox = new BufferedImage[6];
        try {
            //files for windows
            skybox[0] = ImageIO.read(new File("skybox/space_back6.png"));
            skybox[1] = ImageIO.read(new File("skybox/space_bottom4.png"));
            skybox[2] = ImageIO.read(new File("skybox/space_front5.png"));
            skybox[3] = ImageIO.read(new File("skybox/space_left2.png"));
            skybox[4] = ImageIO.read(new File("skybox/space_right1.png"));
            skybox[5] = ImageIO.read(new File("skybox/space_top3.png"));

            //files for linux
//            skybox[0] = ImageIO.read(new File("../../skybox/space_back6.png"));
//            skybox[1] = ImageIO.read(new File("../../skybox/space_bottom4.png"));
//            skybox[2] = ImageIO.read(new File("../../skybox/space_front5.png"));
//            skybox[3] = ImageIO.read(new File("../../skybox/space_left2.png"));
//            skybox[4] = ImageIO.read(new File("../../skybox/space_right1.png"));
//            skybox[5] = ImageIO.read(new File("../../skybox/space_top3.png"));

            //loop to load images into textures
            for(int i = 0; i < 6; ++i) {
                if (skybox[i] != null) {
                    _skybox[i] = loadTexture(skybox[i]);
                }
            }

        } catch (IOException e) {
            System.err.println("cant read image");
        }




        Camera.create();
    }

    private void updateLogic(int delta) {
        triangleAngle += 0.1f * delta; // Increase The Rotation Variable For The Triangles
        quadAngle -= 0.05f * delta; // Decrease The Rotation Variable For The Quads
        if(!pause) {
            totalT += 0.1f*delta;
        }
    }

    //adapted from http://www.java-gaming.org/index.php?topic=25516.0
    private static final int BYTES_PER_PIXEL = 4;
    public static int loadTexture(BufferedImage image){

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //Send texture data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID so we can bind it later again
        return textureID;
    }


//    static int count = 0;
    private void renderGL(SolarSystem s) {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
        GL11.glLoadIdentity(); // Reset The View
        GL11.glTranslatef(0.0f, 0.0f, -2.0f); // Move Right And Into The Screen


        Camera.apply();

        // renders the sky box
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        glColor3f(1,1,1);

        //size of box
        int box = 67;

        //back
        glBindTexture(GL_TEXTURE_2D, _skybox[0]);
        GL11.glBegin(GL11.GL_QUADS);
            glTexCoord2f(0,0); GL11.glVertex3f(-box,-box,-box);
            glTexCoord2f(1,0); GL11.glVertex3f(box,-box,-box);
            glTexCoord2f(1,1); GL11.glVertex3f(box,box,-box);
            glTexCoord2f(0,1); GL11.glVertex3f(-box,box,-box);
        GL11.glEnd();

        //bottom
        glBindTexture(GL_TEXTURE_2D, _skybox[1]);
        GL11.glBegin(GL11.GL_QUADS);
            glTexCoord2f(0,0); GL11.glVertex3f(-box,-box,box);
            glTexCoord2f(1,0); GL11.glVertex3f(box,-box,box);
            glTexCoord2f(1,1); GL11.glVertex3f(box,-box,-box);
            glTexCoord2f(0,1); GL11.glVertex3f(-box,-box,-box);
        GL11.glEnd();

        //front
        glBindTexture(GL_TEXTURE_2D, _skybox[2]);
        GL11.glBegin(GL11.GL_QUADS);
            glTexCoord2f(0,0); GL11.glVertex3f(-box,-box,box);
            glTexCoord2f(1,0); GL11.glVertex3f(box,-box,box);
            glTexCoord2f(1,1); GL11.glVertex3f(box,box,box);
            glTexCoord2f(0,1); GL11.glVertex3f(-box,box,box);
        GL11.glEnd();

        //left
        glBindTexture(GL_TEXTURE_2D, _skybox[3]);
        GL11.glBegin(GL11.GL_QUADS);
            glTexCoord2f(0,0); GL11.glVertex3f(-box,-box,-box);
            glTexCoord2f(1,0); GL11.glVertex3f(-box,-box,box);
            glTexCoord2f(1,1); GL11.glVertex3f(-box,box,box);
            glTexCoord2f(0,1); GL11.glVertex3f(-box,box,-box);
        GL11.glEnd();

        //right
        glBindTexture(GL_TEXTURE_2D, _skybox[4]);
        GL11.glBegin(GL11.GL_QUADS);
            glTexCoord2f(0,0); GL11.glVertex3f(box,-box,-box);
            glTexCoord2f(1,0); GL11.glVertex3f(box,-box,box);
            glTexCoord2f(1,1); GL11.glVertex3f(box,box,box);
            glTexCoord2f(0,1); GL11.glVertex3f(box,box,-box);
        GL11.glEnd();

        //top
        glBindTexture(GL_TEXTURE_2D, _skybox[5]);
        GL11.glBegin(GL11.GL_QUADS);
            glTexCoord2f(0,0); GL11.glVertex3f(-box,box,box);
            glTexCoord2f(1,0); GL11.glVertex3f(box,box,box);
            glTexCoord2f(1,1); GL11.glVertex3f(box,box,-box);
            glTexCoord2f(0,1); GL11.glVertex3f(-box,box,-box);
        GL11.glEnd();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
        /////////////////////////end of skybox//////////////////////////

        GL11.glRotatef(20, 1, 0, 0);//changes point of view
        s.draw(totalT);

    }

    /**
     * Poll Input
     */
    public void pollInput(int delta) {
        Camera.acceptInput(delta);
        // scroll through key events
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
                    closeRequested = true;
                else if (Keyboard.getEventKey() == Keyboard.KEY_P)
//					snapshot();
                    pause = !pause;
                else if (Keyboard.getEventKey() == Keyboard.KEY_O) {
                    //toggles orbit lines
                    s.toggleOrbits();
                } else if (Keyboard.getEventKey() == Keyboard.KEY_N) {
                    //new solar system
                    s = new SolarSystem();
                    GL11.glFlush();
                }
            }
        }

        if (Display.isCloseRequested()) {
            closeRequested = true;
        }
    }

    public void snapshot() {
        System.out.println("Taking a snapshot ... snapshot.png");

        GL11.glReadBuffer(GL11.GL_FRONT);

        int width = Display.getDisplayMode().getWidth();
        int height= Display.getDisplayMode().getHeight();
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );

        File file = new File("snapshot.png"); // The file to save to.
        String format = "PNG"; // Example: "PNG" or "JPG"
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int i = (x + (width * y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, format, file);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Calculate how many milliseconds have passed
     * since last frame.
     *
     * @return milliseconds passed since last frame
     */
    public int getDelta() {
        long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        int delta = (int) (time - lastFrameTime);
        lastFrameTime = time;

        return delta;
    }

    private void createWindow() {
        try {
            Display.setDisplayMode(new DisplayMode(960,540));
            //			Display.setDisplayMode(new DisplayMode(640, 480));
            Display.setVSyncEnabled(true);
            Display.setTitle(windowTitle);
            Display.create();
        } catch (LWJGLException e) {
            Sys.alert("Error", "Initialization failed!\n\n" + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Destroy and clean up resources
     */
    private void cleanup() {
        Display.destroy();
    }

    public static void main(String[] args) {
        new FinalProject().run();
    }

    public static class Camera {
        public static float moveSpeed = 0.006f;
        static int posLim = 30;

        private static float maxLook = 85;

        private static float mouseSensitivity = 0.05f;

        private static Vector3f pos;
        private static Vector3f rotation;

        public static void create() {
            pos = new Vector3f(0, 0, 0);
            rotation = new Vector3f(0, 0, 0);
        }

        public static void apply() {
            if (rotation.y / 360 > 1) {
                rotation.y -= 360;
            } else if (rotation.y / 360 < -1) {
                rotation.y += 360;
            }

            //System.out.println(rotation);
            GL11.glRotatef(rotation.x, 1, 0, 0);
            GL11.glRotatef(rotation.y, 0, 1, 0);
            GL11.glRotatef(rotation.z, 0, 0, 1);
            GL11.glTranslatef(-pos.x, -pos.y, -pos.z);
        }

        public static void acceptInput(float delta) {
            //System.out.println("delta="+delta);
            acceptInputRotate(delta);
            acceptInputMove(delta);
        }

        public static void acceptInputRotate(float delta) {
            if (Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
                float mouseDX = Mouse.getDX();
                float mouseDY = -Mouse.getDY();
                //System.out.println("DX/Y: " + mouseDX + "  " + mouseDY);
                rotation.y += mouseDX * mouseSensitivity * delta;
                rotation.x += mouseDY * mouseSensitivity * delta;
                rotation.x = Math.max(-maxLook, Math.min(maxLook, rotation.x));
            }
        }

        public static void acceptInputMove(float delta) {
            boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
            boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
            boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
            boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
            boolean keyFast = Keyboard.isKeyDown(Keyboard.KEY_Q);
            boolean keySlow = Keyboard.isKeyDown(Keyboard.KEY_E);
            boolean keyInit = Keyboard.isKeyDown(Keyboard.KEY_I);
            boolean keyFlyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
            boolean keyFlyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

            float speed;

            if (keyFast) {
                speed = moveSpeed * 5;
            } else if (keySlow) {
                speed = moveSpeed / 2;
            } else {
                speed = moveSpeed;
            }

            if(keyInit) { //resets camera view of press i
                pos.set(0,0,0);
                rotation.set(0,0,0);
            }

            speed *= delta;

//            if (keyFlyUp && pos.y < posLim) {
            if (keyFlyUp) {
                pos.y += speed;
            }
//            if (keyFlyDown && pos.y > -posLim) {
            if (keyFlyDown) {
                pos.y -= speed;
            }

//            if (keyDown && pos.x > -posLim && pos.z < posLim) {
            if (keyDown) {
                pos.x -= Math.sin(Math.toRadians(rotation.y)) * speed;
                pos.z += Math.cos(Math.toRadians(rotation.y)) * speed;
            }
//            if (keyUp && pos.x < posLim && pos.z > -posLim) {
            if (keyUp) {
                pos.x += Math.sin(Math.toRadians(rotation.y)) * speed;
                pos.z -= Math.cos(Math.toRadians(rotation.y)) * speed;
            }
//            if (keyLeft && pos.x > -posLim && pos.z < posLim) {
            if (keyLeft) {
                pos.x += Math.sin(Math.toRadians(rotation.y - 90)) * speed;
                pos.z -= Math.cos(Math.toRadians(rotation.y - 90)) * speed;
            }
//            if (keyRight && pos.x < posLim && pos.z > -posLim) {
            if (keyRight) {
                pos.x += Math.sin(Math.toRadians(rotation.y + 90)) * speed;
                pos.z -= Math.cos(Math.toRadians(rotation.y + 90)) * speed;
            }
            if(pos.x < -posLim) { pos.x = -posLim; }
            else if(pos.x > posLim) { pos.x = posLim; }
            if(pos.y < -posLim) { pos.y = -posLim; }
            else if(pos.y > posLim) { pos.y = posLim; }
            if(pos.z < -posLim) { pos.z = -posLim; }
            else if(pos.z > posLim) { pos.z = posLim; }
        }

        public static void setSpeed(float speed) {
            moveSpeed = speed;
        }

        public static void setPos(Vector3f pos) {
            Camera.pos = pos;
        }

        public static Vector3f getPos() {
            return pos;
        }

        public static void setX(float x) {
            pos.x = x;
        }

        public static float getX() {
            return pos.x;
        }

        public static void addToX(float x) {
            pos.x += x;
        }

        public static void setY(float y) {
            pos.y = y;
        }

        public static float getY() {
            return pos.y;
        }

        public static void addToY(float y) {
            pos.y += y;
        }

        public static void setZ(float z) {
            pos.z = z;
        }

        public static float getZ() {
            return pos.z;
        }

        public static void addToZ(float z) {
            pos.z += z;
        }

        public static void setRotation(Vector3f rotation) {
            Camera.rotation = rotation;
        }

        public static Vector3f getRotation() {
            return rotation;
        }

        public static void setRotationX(float x) {
            rotation.x = x;
        }

        public static float getRotationX() {
            return rotation.x;
        }

        public static void addToRotationX(float x) {
            rotation.x += x;
        }

        public static void setRotationY(float y) {
            rotation.y = y;
        }

        public static float getRotationY() {
            return rotation.y;
        }

        public static void addToRotationY(float y) {
            rotation.y += y;
        }

        public static void setRotationZ(float z) {
            rotation.z = z;
        }

        public static float getRotationZ() {
            return rotation.z;
        }

        public static void addToRotationZ(float z) {
            rotation.z += z;
        }

        public static void setMaxLook(float maxLook) {
            Camera.maxLook = maxLook;
        }

        public static float getMaxLook() {
            return maxLook;
        }

        public static void setMouseSensitivity(float mouseSensitivity) {
            Camera.mouseSensitivity = mouseSensitivity;
        }

        public static float getMouseSensitivity() {
            return mouseSensitivity;
        }
    }
}
