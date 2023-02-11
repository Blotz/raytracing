package org.openjfx;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Render {
    // camera
    public static final Vec origin = new Vec(0f, 0f, 0f);
    public static final double focalLength = 1.0f;
    public static final double viwportHeight = 2.0f;
    public static final double viewportWidth = App.aspectRatio * viwportHeight;
    public static final Vec vertical = new Vec(0f, viwportHeight, 0f);
    public static final Vec horizontal = new Vec(viewportWidth, 0f, 0f);
    public static final Vec lowerLeftCorner = new Vec(0f,0f,0f).add(Vec.div(horizontal, -2f))
                                                                       .add(Vec.div(vertical, -2f))
                                                                       .add(new Vec(0f, 0f, focalLength));
    
    /**
     * Render the image
     * @param pixelWriter Image to render
     */
    public static void render(PixelWriter pixelWriter) {
        
        // Generating all the ray casts for the image. one per pixel atm!
        for (int j=App.imageHeight-1; j>=0; --j) {
            System.out.print(String.format("\rScanlines remaining: %d ", j));
            for (int i=0; i< App.imageWidth; ++i) {
                // u and v are the offset from the center of the viewport
                // Camara is at (0,0,0) and the viewport is centered at (0,0,1)
                // u and v are floats between 0 and 1 and represent the percentage of the viewport
                double u = (double) i / (App.imageWidth-1);
                double v = (double) j / (App.imageHeight-1);
                
                // ray from the camera to the pixel
                Vec direction = new Vec(0f, 0f, 0f).add(lowerLeftCorner)
                                                           .add(Vec.mult(horizontal, u))
                                                           .add(Vec.mult(vertical, v))
                                                           .sub(origin);
                Ray r = new Ray(origin, direction);
                
                // Calculate the color of the pixel based on the ray
                Vec pixel_color = rayColor(r);
                // Write that color to the pixel
                pixelWriter.setArgb(i, j, Vec.writeColor(pixel_color));
                
            }
        }
        System.out.println(String.format("%nDone!")); // end line
    }
    
    /**
     * Calculate the color of the pixel based on the ray
     * @param r Ray
     * @return Color of the pixel
     */
    static Vec rayColor(Ray r) {
        if (hitSphere(new Vec(0, 0, -1), 0.5, r)) {
            return new Vec(1, 0, 0);
        }
        Vec unit_direction = Vec.unitVector(r.direction());
        double t = 0.5 * (unit_direction.y() + 1.0);
        return Vec.add(
          new Vec(1.0, 1.0, 1.0).mult(1.0-t),
          new Vec(0.5, 0.7, 1.0).mult(t)
        );
    }
    
    static boolean hitSphere(Vec center, double radius, Ray r) {
        Vec oc = r.origin().sub(center);
        double a = Vec.dot(r.direction(), r.direction());
        double b = 2.0 * Vec.dot(oc, r.direction());
        double c = Vec.dot(oc, oc) - radius*radius;
        double discriminant = b*b - 4*a*c;
        return (discriminant > 0);
    }
}
