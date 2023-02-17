package org.openjfx;

import javafx.scene.image.PixelWriter;

public class Render {
    
    public static Vec origin = new Vec(0f, 0f, 0f);
    public static final int samplesPerPixel = 100;
    public static final int maxDepth = 50;
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
        Camera camera = new Camera();
        
        // Generating all the ray casts for the image. one per pixel atm!
        for (int j=App.imageHeight-1; j>=0; --j) {
            // System.out.println();
            System.out.print(String.format("\rScanlines remaining: %d ", j));
            for (int i=0; i< App.imageWidth; ++i) {
                Vec pixelColor = new Vec(0,0,0);
                for (int s=0; s<samplesPerPixel; ++s) {
                    // u and v are the offset from the center of the viewport
                    // Camara is at (0,0,0) and the viewport is centered at (0,0,1)
                    // u and v are floats between 0 and 1 and represent the percentage of the viewport
                    double u = ((double) i + Math.random()) / (App.imageWidth-1);
                    double v = ((double) j + Math.random()) / (App.imageHeight-1);
    
                    // ray from the camera to the pixel
                    Ray r = camera.getRay(u, v);
                    // Calculate the color of the pixel based on the ray
                    pixelColor = pixelColor.add(rayColor(r, world, maxDepth));
                    
                }
                
                
                // Write that color to the pixel
                // System.out.print(String.format("(%d, %d)", i, j));
                pixelWriter.setArgb(i, App.imageHeight-1 - j, Vec.writeColor(pixelColor, samplesPerPixel));
            }
        }
        System.out.println(String.format("%nDone!")); // end line
    }
    
    
    /**
     * Calculate the color of the pixel based on the ray
     * @param r Ray
     * @return Color of the pixel
     */
    static Vec rayColor(Ray r, HittableList world, int depth) {
        if (depth <= 0) {
            return new Vec(0,0,0);
        }
        // double t = hitSphere(new Vec(0f, 0f, -1f), 0.5f, r);
        HitRecord rec = new HitRecord();
        if (world.hit(r, 0.001, infinity, rec)) {
            Vec target = Vec.randomInUnitSphere().add(rec.normal).add(rec.p);
            // return Vec.add(rec.normal, new Vec(1,1,1)).mult(0.5);
            return rayColor(
              new Ray(rec.p, Vec.sub(target, rec.p)),
              world,
              depth-1
            ).mult(0.5);
        }
        
        Vec unit_direction = Vec.unitVector(r.direction());
        double t = 0.5*(unit_direction.y() + 1.0);
        
        return Vec.add(
          new Vec(1.0, 1.0, 1.0).mult(1.0-t),
          new Vec(0.5, 0.7, 1.0).mult(t)
        );
    }
    
    /**
     * Clamp a value between a min and max
     * @param x The value to clamp
     * @param min The minimum value x can be
     * @param max The maximum value x can be
     * @return The clamped value
     */
    public static double clamp(double x, double min, double max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }
        return x;
    }
}
