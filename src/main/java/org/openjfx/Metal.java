package org.openjfx;

public class Metal implements Material {
    
    private Vec albedo;
    private double fuzz;
    
    public Metal(Vec albedo, double fuzz) {
        this.albedo = albedo;
        this.fuzz = fuzz;
        
    }
    @Override
    public boolean scatter(Ray rayIn, HitRecord hitRecord, Vec attenuation, Ray scattered) {
        Vec reflected = reflect(Vec.unitVector(rayIn.direction()), hitRecord.normal);
        scattered.set(hitRecord.p, reflected.add(Vec.randomInUnitSphere().mult(fuzz)));
        attenuation.set(albedo);
        return Vec.dot(scattered.direction(), hitRecord.normal) > 0;
    }
    
    private static Vec reflect(Vec v, Vec n) {
        return Vec.sub(v, (Vec.mult(n, 2f * Vec.dot(v,n))));
    }
    
    @Override
    public double r() {
        return this.albedo.x();
    }
    
    @Override
    public double g() {
        return this.albedo.y();
    }
    
    @Override
    public double b() {
        return this.albedo.z();
    }
    
    @Override
    public void setColor(double r, double g, double b) {
        this.albedo.set(new Vec(r, g, b));
    }
    
    public double getFuzz() {
        return fuzz;
    }
    @Override
    public String toString() {
        return "Metal{" +
          "albedo=" + albedo +
          ", fuzz=" + fuzz +
          '}';
    }
}
