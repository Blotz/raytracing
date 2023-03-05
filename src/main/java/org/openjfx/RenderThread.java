package org.openjfx;

import javafx.animation.Timeline;

public class RenderThread extends Render implements Runnable {
    private Timeline timeline;
    private int[] pixelData;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private static volatile boolean isRunning = false;  // using a Boolean object, so we can pass it by reference
    
    public RenderThread(
      Timeline timeline,
      int[] pixelData,
      HittableList world,
      Vec camPosition, Vec camRotation,
      int numPasses, int samplesPerPixel, int maxDepth,
      int x1, int x2, int y1, int y2
    ) {
        super(world, camPosition, camRotation, numPasses, samplesPerPixel, maxDepth);
        this.timeline = timeline;
        this.pixelData = pixelData;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    
    @Override
    public void run() {
        RenderThread.isRunning = true;
        long s1 = System.currentTimeMillis();
        render(pixelData, x1, x2, y1, y2);
        long s2 = System.currentTimeMillis();
        System.out.println("Total Render time: " + (s2 - s1) + "ms");
        RenderThread.isRunning = false;
        
        // Stop the timeline and update the image one last time
        timeline.stop();
    }
    
    public static boolean getIsRunning() {
        return RenderThread.isRunning;
    }
    
    public static void stop() {
        RenderThread.isRunning = false;
        System.out.println("Render thread stopped" + RenderThread.isRunning);
    }
}
