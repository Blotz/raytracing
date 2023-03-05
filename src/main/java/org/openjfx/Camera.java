package org.openjfx;

public class Camera {
    private static final double viwportHeight = 2.0f;
    private static final double viewportWidth = App.aspectRatio * viwportHeight;
    private static final double focalLength = 1.0f;
    private static final double fieldOfView = 90;
    private Vec horizontal;
    private Vec vertical;
    private Vec lowerLeftCorner;
    private Vec camPosition;
    private Vec camRotation;
    private final boolean isLookAt;
    
    public Camera(Vec camPosition, Vec camRotation, boolean isLookAt) {
        this.camPosition = camPosition;
        this.camRotation = camRotation;
    
        System.out.println("lookAt: " + isLookAt);
        this.isLookAt = isLookAt;
        // Calculate the viewport
        if (!isLookAt) {
            horizontal = new Vec(viewportWidth, 0, 0);
            vertical = new Vec(0, viwportHeight, 0);
            lowerLeftCorner = new Vec(0, 0, 0)
              .sub(Vec.mult(horizontal, 0.5))
              .sub(Vec.mult(vertical, 0.5))
              .sub(new Vec(0, 0, focalLength));
    
            // Rotate the viewport
            horizontal = Vec.rotate(this.camRotation, horizontal);
            vertical = Vec.rotate(this.camRotation, vertical);
            lowerLeftCorner = Vec.rotate(this.camRotation, lowerLeftCorner);
        } else {
            // Calculate the viewport
            Vec lookAt = this.camRotation;
            Vec lookFrom = this.camPosition;
            
            Vec VPN = Vec.sub(lookFrom, lookAt).normalize();
            Vec VUV = Vec.cross(new Vec(0, 1, 0), VPN).normalize();
            Vec VRV = Vec.cross(VPN, VUV).normalize();
            
            horizontal = Vec.mult(VUV, viewportWidth);
            vertical = Vec.mult(VRV, viwportHeight);
            
            lowerLeftCorner = new Vec(0f,0f,0f)
              .sub(Vec.mult(horizontal, 0.5))
              .sub(Vec.mult(vertical, 0.5))
              .sub(VPN);
        }
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
        
        Vec direction = new Vec(0f,0f,0f)
          .add(lowerLeftCorner)
          .add(Vec.mult(horizontal, u))
          .add(Vec.mult(vertical, v));
//          .sub(camPosition);
        
        // direction = rotate(direction);
        return new Ray(this.camPosition, direction);
    }
}
