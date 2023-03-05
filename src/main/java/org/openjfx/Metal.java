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
        // get the reflected ray
        Vec reflected = reflect(Vec.unitVector(rayIn.direction()), hitRecord.normal);
        // set the scattered ray to the reflected ray plus a random vector
        scattered.set(hitRecord.p, reflected.add(Vec.randomInUnitSphere().mult(fuzz)));
        // set color
        attenuation.set(albedo);
        
        // if the dot product of the scattered ray and the normal is less than 0
        // then the ray is pointing into the surface
        // so we don't want to scatter the ray
        return Vec.dot(scattered.direction(), hitRecord.normal) > 0;  // v1 dot v2 == cos(theta)
    }
    
    private static Vec reflect(Vec v, Vec n) {
        // b is v dot normal
        // v is the incoming ray
        
        // reflection direction = v + 2( v dot n ) * n
        // use -v instead of v because we are reflecting the ray
        // -v + 2( -v dot n ) * n = v - 2( v dot n ) * n
        
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
