package org.openjfx;

public class DiffuseLight implements Material {
    
    @Override
    public boolean scatter(Ray rayIn, HitRecord hitRecord, Vec attenuation, Ray scattered) {
        return false;
    }
    
    @Override
    public double r() {
        return 0;
    }
    
    @Override
    public double g() {
        return 0;
    }
    
    @Override
    public double b() {
        return 0;
    }
    
    @Override
    public void setColor(double r, double g, double b) {
    
    }
    
    public Vec emitted(double u, double v, Vec p) {
        return new Vec(0, 0, 0);
    }
}
