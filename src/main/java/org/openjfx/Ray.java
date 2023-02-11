package org.openjfx;

public class Ray {
    Vec origin;
    Vec direction;
    
    public Ray(Vec origin, Vec direction) {
        this.origin = origin;
        this.direction = direction;
    }
    
    public Vec origin() {
        return this.origin;
    }
    
    public Vec direction() {
        return this.direction;
    }
    
    public Vec at(double t) {
        this.direction.mult(t);
        return this.origin.add(this.direction.mult(t));
        
    }
}
