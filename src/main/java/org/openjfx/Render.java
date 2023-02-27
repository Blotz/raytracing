package org.openjfx;

public class Render {
    public static final double infinity = Double.POSITIVE_INFINITY;
    public static final double pi = Math.PI;
    
    private Camera camera;
    private HittableList world;
    
    public Render(HittableList world, Vec camPosition, Vec camRotation) {
        this.camera = new Camera(camPosition, camRotation);
        this.world = world;
    }
    
    /**
     * Render the image
     * @param pixelData Image to render
     */
    public void render(int[] pixelData, int x1, int x2, int y1, int y2) {
        System.out.println(String.format("Rendering from (%d, %d) to (%d, %d)", x1, y1, x2, y2));
        
        // Generating all the ray casts for the image. one per pixel atm!
        for (int j=y2-1; j>=y1; --j) {
            // System.out.println();
            // System.out.print(String.format("\rScanlines remaining: %d ", j));
            for (int i=x1; i< x2; ++i) {
                Vec pixelColor = new Vec(0,0,0); // color of the pixel
                for (int s=0; s<App.samplesPerPixel; ++s) {
                    // u and v are the offset from the center of the viewport
                    // Camara is at (0,0,0) and the viewport is centered at (0,0,1)
                    // u and v are floats between 0 and 1 and represent the percentage of the viewport
                    double u = ((double) i + Math.random()) / (App.imageWidth-1);
                    double v = ((double) j + Math.random()) / (App.imageHeight-1);
    
                    // ray from the camera to the pixel
                    Ray r = this.camera.getRay(u, v);
                    // Calculate the color of the pixel based on the ray
                    pixelColor = Vec.add(pixelColor, rayColor(r, world, App.maxDepth));
                    
                }
                
                
                // Write that color to the pixel
                // System.out.print(String.format("(%d, %d)", i, j));
                // pixelWriter.setArgb(i, App.imageHeight-1 - j, Vec.writeColor(pixelColor, samplesPerPixel));
                pixelData[i + (App.imageHeight-1 - j) * App.imageWidth] = Vec.writeColor(pixelColor, App.samplesPerPixel);
            }
        }
        // System.out.println(String.format("%nDone!")); // end line
        System.out.println(String.format("Finished rendering from (%d, %d) to (%d, %d)", x1, y1, x2, y2));
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
    
        HitRecord rec = new HitRecord();
        
        if (!world.hit(r, 0.001, infinity, rec)) {
            // background color
            return new Vec(0.01,0.01,0.01);
            
            // Vec unit_direction = Vec.unitVector(r.direction());
            // double t = 0.5*(unit_direction.y() + 1.0);
    
            // return Vec.add(
            //   new Vec(1.0, 1.0, 1.0).mult(1.0-t),
            //   new Vec(0.5, 0.7, 1.0).mult(t)
            // );
        }
        
        // scattered ray
        Ray scattered = new Ray(new Vec(0,0,0), new Vec(0,0,0));
        Vec attenuation = new Vec(0,0,0);
        Vec emitted = rec.material.emitted();
        
        if (!rec.material.scatter(r, rec, attenuation, scattered)) {
            return emitted;
        }
        
        return Vec.add(emitted, Vec.mult(attenuation, rayColor(scattered, world, depth-1)));
    }
}
