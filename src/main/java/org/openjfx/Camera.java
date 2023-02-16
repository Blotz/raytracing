package org.openjfx;

public class Camera {
    public static final double viwportHeight = 2.0f;
    public static final double viewportWidth = App.aspectRatio * viwportHeight;
    public static final double focalLength = 1.0f;
    public static final Vec horizontal = new Vec(viewportWidth, 0f, 0f);
    public static final Vec vertical = new Vec(0f, viwportHeight, 0f);
    public static final Vec lowerLeftCorner = Render.origin.sub(Vec.div(horizontal, 2f))
      .sub(Vec.div(vertical, 2f))
      .sub(new Vec(0f, 0f, focalLength));
    
    public static Vec rotation = new Vec(-0.5f, 0f, 0f); // Camera rotation
    public static Vec position = new Vec(0f, 0f, 0f); // Camera position1
    
    public Camera() {
    }
    
    public Ray getRay(double u, double v) {
        // u and v are the offset from the center of the viewport
        // Camara is at (0,0,0) and the viewport is centered at (0,0,1)
        // u and v are floats between 0 and 1 and represent the percentage of the viewport
        Vec direction = Camera.position.add(lowerLeftCorner)
          .add(Vec.mult(horizontal, u))
          .add(Vec.mult(vertical, v))
          .sub(Camera.position);
        
        direction = rotate(direction);
        return new Ray(Camera.position, direction);
    }
    private static Vec rotate(Vec direction) {
        // rotate pitch yaw roll
        // https://en.wikipedia.org/wiki/Rotation_matrix#General_rotations
        // https://en.wikipedia.org/wiki/Euler_angles#Rotation_matrix
        // https://en.wikipedia.org/wiki/Rotation_matrix#In_three_dimensions
        
        double pitch = Camera.rotation.x();
        double yaw = Camera.rotation.y();
        double roll = Camera.rotation.z();
        
        double sinPitch = Math.sin(pitch);
        double cosPitch = Math.cos(pitch);
        
        double sinYaw = Math.sin(yaw);
        double cosYaw = Math.cos(yaw);
        
        double sinRoll = Math.sin(roll);
        double cosRoll = Math.cos(roll);
        
        double x = direction.x();
        double y = direction.y();
        double z = direction.z();
        
        // pitch
        double x1 = x;
        double y1 = y * cosPitch - z * sinPitch;
        double z1 = y * sinPitch + z * cosPitch;
        
        // yaw
        double x2 = x1 * cosYaw + z1 * sinYaw;
        double y2 = y1;
        double z2 = -x1 * sinYaw + z1 * cosYaw;
        
        // roll
        double x3 = x2 * cosRoll - y2 * sinRoll;
        double y3 = x2 * sinRoll + y2 * cosRoll;
        double z3 = z2;
        
        return new Vec(x3, y3, z3);
    }
}
