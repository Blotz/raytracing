package org.openjfx;

public interface Material {
    Material LAMBERTIAN = new Lambertian(new Vec(0,0,0));
    Material DIFFUSE_LIGHT = new DiffuseLight(new Vec(0,0,0));
    Material METAL = new Metal(new Vec(0,0,0), 0);
    Material DIELECTRIC = new Dielectric(new Vec(0,0,0), 0);
    
    
    default boolean scatter(Ray rayIn, HitRecord hitRecord, Vec attenuation, Ray scattered) {return false;}
    default Vec emitted() { return new Vec(0,0,0);};
    double r();
    double g();
    double b();
    void setColor(double r, double g, double b);
    String toString();
}
