package org.openjfx;

public interface Material {
    default boolean scatter(Ray rayIn, HitRecord hitRecord, Vec attenuation, Ray scattered) {return false;}
    default Vec emitted() { return new Vec(0,0,0);};
    double r();
    double g();
    double b();
    void setColor(double r, double g, double b);
    String toString();
}
