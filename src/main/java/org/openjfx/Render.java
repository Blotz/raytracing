package org.openjfx;

public class Render {
    public static final double infinity = Double.POSITIVE_INFINITY;
    private Camera camera;
    private HittableList world;
    
    private int numPasses;
    private int samplesPerPixel;
    private int maxDepth;
    public Render(HittableList world, Vec camPosition, Vec camRotation, boolean isLookAt, int numPasses, int samplesPerPixel, int maxDepth) {
        this.camera = new Camera(camPosition, camRotation, isLookAt);
        this.world = world;
        this.numPasses = numPasses;
        this.samplesPerPixel = samplesPerPixel;
        this.maxDepth = maxDepth;
    }
    
    /**
     * Render the image
     *
     * @param pixelData Image to render
     */
    public void render(int[] pixelData, int x1, int x2, int y1, int y2) {
        double[][] rawPixelData = new double[App.imageWidth * App.imageHeight][3];
        System.out.println(String.format("Rendering from (%d, %d) to (%d, %d)", x1, y1, x2, y2));
        long start = System.currentTimeMillis();
        for (int p=1; p<=this.numPasses; ++p) {
            System.out.print(String.format("Pass %d/%d. %d samples per pixel. ", p, this.numPasses, p*this.samplesPerPixel));
            long s1 = System.currentTimeMillis();
            // Generating all the ray casts for the image. one per pixel atm!
            for (int j = y2 - 1; j >= y1; --j) {
                // System.out.println();
                // System.out.print(String.format("\rScanlines remaining: %d ", j));
                // check if the render thread is still running]
                if (!RenderThread.getIsRunning()) {
                    System.out.println("Render thread stopped");
                    return;
                }
                
                for (int i = x1; i < x2; ++i) {
                    // storing the sum raw pixel data for compute in passes
                    double[] rawPixel = rawPixelData[i + (App.imageHeight - 1 - j) * App.imageWidth];
                    
                    for (int s = 0; s < this.samplesPerPixel; ++s) {
                        // u and v are the offset from the center of the viewport
                        // Camara is at (0,0,0) and the viewport is centered at (0,0,1)
                        // u and v are floats between 0 and 1 and represent the percentage of the viewport
                        double u = ((double) i + Math.random()) / (App.imageWidth - 1);
                        double v = ((double) j + Math.random()) / (App.imageHeight - 1);
                
                        // ray from the camera to the pixel
                        Ray r = this.camera.getRay(u, v);
                        // Calculate the color of the pixel based on the ray
                        Vec color = rayColor(r, world, this.maxDepth);
                        
                        rawPixel[0] += color.x();
                        rawPixel[1] += color.y();
                        rawPixel[2] += color.z();
                    }
            
            
                    // Write that color to the pixel
                    pixelData[i + (App.imageHeight - 1 - j) * App.imageWidth] = Vec.writeColor(rawPixel, this.samplesPerPixel, p);
                }
            }
            long s2 = System.currentTimeMillis();
            System.out.println(String.format("completed in: %dms", s2-s1));
            // print predicted time to finish
            long elapsed = s2 - start;
            long remaining = (long) ((elapsed / (double) p) * (this.numPasses - p));
            // convert to minutes and seconds
            remaining /= 1000;
            long minutes = remaining / 60;
            long seconds = remaining % 60;
            System.out.println(String.format("Estimated time remaining: %d:%02d", minutes, seconds));
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
            return new Vec(0.0,0.0,0.0);
//            return new Vec(0.7,0.7,0.7);
        }
        
        // scattered ray
        Ray scattered = new Ray(new Vec(0,0,0), new Vec(0,0,0));
        Vec attenuation = new Vec(0,0,0);
        Vec emitted = rec.material.emitted();
        
        // if the ray is absorbed, return the emitted color
        if (!rec.material.scatter(r, rec, attenuation, scattered)) {
            return emitted;
        }
        
        // return the emitted color + the color of the scattered ray
        return Vec.add(emitted, Vec.mult(attenuation, rayColor(scattered, world, depth-1)));
    }
}
