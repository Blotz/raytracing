package org.openjfx;

public class Lambertian implements Material {
    private Vec albedo;

    public Lambertian(Vec albedo) {
        this.albedo = albedo;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Vec attenuation, Ray scattered) {
        
        Vec scatterDirection = Vec.add(rec.normal, Vec.randomUnitVector());
        
        // Catch degenerate scatter direction
        if (scatterDirection.nearZero()) {
            scatterDirection = rec.normal;
        }
        scattered.set(rec.p, scatterDirection);
        attenuation.set(albedo);
        
        return true;
    }
}