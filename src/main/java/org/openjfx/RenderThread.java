package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RenderThread extends Render implements Runnable {
    private ImageView imageView;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    
    public RenderThread(
      ImageView imageView,
      HittableList world,
      Vec camPosition, Vec camRotation,
      int x1, int x2, int y1, int y2
    ) {
        super(world, camPosition, camRotation);
        this.imageView = imageView;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    
    @Override
    public void run() {
        // Get the parent of the image view so we can disable it
        Parent root = imageView.getParent();
        
        // Create our pixel data and copy image
        int[] pixelData = new int[App.imageWidth * App.imageHeight];
        WritableImage copyImage = new WritableImage(App.imageWidth, App.imageHeight);
        PixelWriter copyPixelWriter = copyImage.getPixelWriter();
    
        // Timeline to update the image every 100ms
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateImage(pixelData, copyPixelWriter, copyImage);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        
        // Disable the ui and start updating the image
        root.setDisable(true);
        updateImage(pixelData, copyPixelWriter, copyImage);  // Update the image once before starting the timeline
        timeline.play();
        
        long s1 = System.currentTimeMillis();
        render(pixelData, x1, x2, y1, y2);
        long s2 = System.currentTimeMillis();
        System.out.println("Render time: " + (s2 - s1) + "ms");
        
        // Stop the timeline and update the image one last time
        timeline.stop();
        updateImage(pixelData, copyPixelWriter, copyImage);
        root.setDisable(false);
    }
    
    /**
     * Updates the image
     * @param pixelData A temporary array to store the pixel data
     * @param pixelWriter The target image writer
     * @param rwimage The target image
     */
    private void updateImage(int[] pixelData, PixelWriter pixelWriter, WritableImage rwimage) {
        pixelWriter.setPixels(
          0, 0, App.imageWidth, App.imageHeight,
          PixelFormat.getIntArgbInstance(),
          pixelData, 0, App.imageWidth
        );
        this.imageView.setImage(rwimage);
    }
}
