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
    
    public static void render(WritableImage image) {
        PixelWriter pixelWriter = image.getPixelWriter();
        
        for (int j=(int) image.getHeight()-1; j>=0; --j) {
            System.out.print(String.format("\rScanlines remaining: %d ", j));
            for (int i=0; i< image.getWidth(); ++i) {
                // u and v are the offset from the center of the viewport
                double u = ((double) i) / (image.getWidth()-1);
                double v = ((double) j) / (image.getHeight()-1);
                
                // ray from the camera to the pixel
                Vec direction = new Vec(0f, 0f, 0f).add(lowerLeftCorner).add(Vec.mult(horizontal, u)).add(Vec.mult(vertical, v)).sub(origin);
                Ray r = new Ray(origin, direction);
                
                Vec pixel_color = rayColor(r);
                pixelWriter.setArgb(i, j, Vec.writeColor(pixel_color));
                
            }
        }
        System.out.println(String.format("%nDone!")); // end line
    }
    
    static Vec rayColor(Ray r) {
        Vec unit_direction = Vec.unitVector(r.direction());
        double t = 0.5 * (unit_direction.y() + 1.0);
        return Vec.add(new Vec(1.0, 1.0, 1.0).mult(1.0-t), new Vec(0.5, 0.7, 1.0).mult(t));
    }
}
