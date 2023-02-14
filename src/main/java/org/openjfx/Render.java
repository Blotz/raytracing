package org.openjfx;

import javafx.scene.Camera;
import javafx.scene.image.PixelWriter;

public class Render {
    // camera
    public static final double viwportHeight = 2.0f;
    public static final double viewportWidth = App.aspectRatio * viwportHeight;
    public static final double focalLength = 1.0f;
    public static Vec origin = new Vec(0f, 0f, 0f); // Camera position
    public static Vec rotation = new Vec(-0.5f, 0f, 0f); // Camera rotation
    public static final Vec horizontal = new Vec(viewportWidth, 0f, 0f);
    public static final Vec vertical = new Vec(0f, viwportHeight, 0f);
    
    public static final Vec lowerLeftCorner = origin.sub(Vec.div(horizontal, 2f))
                                                    .sub(Vec.div(vertical, 2f))
                                                    .sub(new Vec(0f, 0f, focalLength));
    
    // World
    public static HittableList world = new HittableList()
      .add(new Sphere(new Vec(0,0,-4), 0.5))
      .add(new Sphere(new Vec(0,-100.5,-1), 100))
      .add(new Sphere(new Vec(0,0,-1), 0.05));
    
    public static final double infinity = Double.POSITIVE_INFINITY;
    public static final double pi = Math.PI;
    
    /**
     * Render the image
     * @param pixelWriter Image to render
     */
    public static void render(PixelWriter pixelWriter) {
        
        // Generating all the ray casts for the image. one per pixel atm!
        for (int j=App.imageHeight-1; j>=0; --j) {
            // System.out.println();
            System.out.print(String.format("\rScanlines remaining: %d ", j));
            for (int i=0; i< App.imageWidth; ++i) {
                // u and v are the offset from the center of the viewport
                // Camara is at (0,0,0) and the viewport is centered at (0,0,1)
                // u and v are floats between 0 and 1 and represent the percentage of the viewport
                double u = (double) i / (App.imageWidth-1);
                double v = (double) j / (App.imageHeight-1);
                
                // ray from the camera to the pixel
                Vec direction = origin.add(lowerLeftCorner)
                                      .add(Vec.mult(horizontal, u))
                                      .add(Vec.mult(vertical, v))
                                      .sub(origin);
                direction = rotate(direction);
                
                Ray r = new Ray(origin, direction);
                
                // Calculate the color of the pixel based on the ray
                Vec pixel_color = rayColor(r, world);
                
                // Write that color to the pixel
                // System.out.print(String.format("(%d, %d)", i, j));
                pixelWriter.setArgb(i, App.imageHeight-1 - j, Vec.writeColor(pixel_color));
            }
        }
        System.out.println(String.format("%nDone!")); // end line
    }
    
    private static Vec rotate(Vec direction) {
        // rotate pitch yaw roll
        // https://en.wikipedia.org/wiki/Rotation_matrix#General_rotations
        // https://en.wikipedia.org/wiki/Euler_angles#Rotation_matrix
        // https://en.wikipedia.org/wiki/Rotation_matrix#In_three_dimensions
        
        double pitch = rotation.x();
        double yaw = rotation.y();
        double roll = rotation.z();
        
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
    
    static double hitSphere(Vec center, double radius, Ray r) {
         Vec oc = Vec.sub(r.origin(), center); // oc = A - C // not sure why this doesnt work :P
        
        double a = r.direction().lengthSquared();      // a = B.B
        double halfB = Vec.dot(oc, r.direction());     // halfB = 2 * B.(A-C)
        double c = oc.lengthSquared() - radius*radius; // c = (A-C).(A-C) - r^2
        double discriminant = halfB*halfB - a*c;       // discriminant = b^2 - 4ac
        
        if (discriminant < 0) {
            return -1.0;
        } else {
            return (-halfB - Math.sqrt(discriminant)) / a;
        }
    }
    
    /**
     * Calculate the color of the pixel based on the ray
     * @param r Ray
     * @return Color of the pixel
     */
    static Vec rayColor(Ray r, HittableList world) {
        // double t = hitSphere(new Vec(0f, 0f, -1f), 0.5f, r);
        HitRecord rec = new HitRecord();
        if (world.hit(r, 0, infinity, rec)) {
            return Vec.add(rec.normal, new Vec(1,1,1)).mult(0.5);
        }
        
        Vec unit_direction = Vec.unitVector(r.direction());
        double t = 0.5*(unit_direction.y() + 1.0);
        
        return Vec.add(
          new Vec(1.0, 1.0, 1.0).mult(1.0-t),
          new Vec(0.5, 0.7, 1.0).mult(t)
        );
    }
    
    public static double degreesToRadians(double degrees) {
        return degrees * pi / 180f;
    }
}
