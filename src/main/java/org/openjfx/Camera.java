package org.openjfx;

public class Camera {
    private static final double viwportHeight = 2.0f;
    private static final double viewportWidth = App.aspectRatio * viwportHeight;
    private static final double focalLength = 1.0f;
    private Vec horizontal;
    private Vec vertical;
    private Vec lowerLeftCorner;
    
    private static Vec position = new Vec(0, 0, 0);
    private static Vec rotation = new Vec(0, 0, 0);
    
    public Camera() {
        // Calculate the viewport
        horizontal = new Vec(viewportWidth, 0, 0);
        vertical = new Vec(0, viwportHeight, 0);
        lowerLeftCorner = Render.origin
          .sub(horizontal.mult(0.5))
          .sub(vertical.mult(0.5))
          .sub(new Vec(0, 0, focalLength));
    
        // Rotate the viewport
        horizontal      = rotate(rotation, horizontal);
        vertical        = rotate(rotation, vertical);
        lowerLeftCorner = rotate(rotation, lowerLeftCorner);
    }
    
    public static void setPosition(Vec position) {
        Camera.position = position;
    }
    
    public static void setRotation(Vec rotation) {
         Camera.rotation = rotation;
    }
    
    /**
     * Get a ray from the camera to the pixel
     * @param u offset from the center of the viewport
     * @param v offset from the center of the viewport
     * @return Ray from the camera to the pixel
     */
    public Ray getRay(double u, double v) {
        // u and v are the offset from the center of the viewport
        // Camara is at (0,0,0) and the viewport is centered at (0,0,1)
        // u and v are floats between 0 and 1 and represent the percentage of the viewport
        Vec direction = position.add(lowerLeftCorner)
          .add(Vec.mult(horizontal, u))
          .add(Vec.mult(vertical, v))
          .sub(position);
        
        // direction = rotate(direction);
        return new Ray(position, direction);
    }
    
    /**
     * Rotate a vector
     * @param rotation pitch yaw roll
     * @param direction vector to rotate
     * @return
     */
    private static Vec rotate(Vec rotation, Vec direction) {
        // rotate pitch yaw roll
        // https://en.wikipedia.org/wiki/Rotation_matrix#General_rotations
        // https://en.wikipedia.org/wiki/Euler_angles#Rotation_matrix
        // https://en.wikipedia.org/wiki/Rotation_matrix#In_three_dimensions
        
        double pitch = rotation.x();
        double yaw   = rotation.y();
        double roll  = rotation.z();
        
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