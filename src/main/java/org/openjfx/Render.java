package org.openjfx;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Render {
    public static void render(WritableImage image) {
        PixelWriter pixelWriter = image.getPixelWriter();
        
        for (int j = (int) image.getHeight()-1; j>=0; --j) {
            System.out.print(String.format("\rScanlines remaining: %d ", j));
            for (int i=0; i< image.getWidth(); ++i) {
                double r = (double) i / (image.getWidth()-1);
                double g = (double) j / (image.getHeight()-1);
                double b = 0.25;
                
                int a = 255;
                int ir = (int) (255.999 * r);
                int ig = (int) (255.999 * g);
                int ib = (int) (255.999 * b);

                pixelWriter.setArgb(i, j, (a << 24) | (ir << 16) | (ig << 8) | ib);
            }
        }
        System.out.println(); // end line
    }
}
