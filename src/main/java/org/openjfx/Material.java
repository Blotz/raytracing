package org.openjfx;

public interface Material {
    boolean scatter(Ray rayIn, HitRecord hitRecord, Vec attenuation, Ray scattered);
    
    double r();
    double g();
    double b();
    void setColor(double r, double g, double b);
}
