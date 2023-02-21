package org.openjfx;

import java.util.Objects;

public class Sphere implements Hittable {
    Vec center;
    double radius;
    Material material;
    
    public Sphere(Vec center, double radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }
    
    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        
        // Find out if it hit the sphere!
        Vec oc = Vec.sub(r.origin(), center); // oc = A - C // not sure why this doesnt work :P
    
        double a = r.direction().lengthSquared();      // a = B.B
        double halfB = Vec.dot(oc, r.direction());     // halfB = 2 * B.(A-C)
        double c = oc.lengthSquared() - radius*radius; // c = (A-C).(A-C) - r^2
        
        double discriminant = halfB*halfB - a*c;       // discriminant = b^2 - 4ac
        
        if (discriminant < 0) {
            // Miss
            return false;
        }
        double sqrtD = Math.sqrt(discriminant);
        
        // Find the nearest root that lies in the acceptable range.
        double root = (-halfB - sqrtD) / a;
        if (root < tMin || tMax < root) {
            root = (-halfB + sqrtD) / a;
            if (root < tMin || tMax < root)
                return false;
        }
        
        rec.t = root;
        rec.p = r.at(rec.t);
        Vec outwardNormal = Vec.sub(rec.p, center).div(radius);
        rec.setFaceNormal(r, outwardNormal);
        rec.material = material;
        
        return true;
    }
    
    public double r() {
        return this.material.r();
    }
    public double g() {
        return this.material.g();
    }
    public double b() {
        return this.material.b();
    }
    
    public void setColor(double r, double g, double b) {
        this.material.setColor(r, g, b);
    }
    
    @Override
    public String toString() {
        return "Sphere{" +
          "center=" + center +
          ", radius=" + radius +
          '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sphere sphere = (Sphere) o;
        return Double.compare(sphere.radius, radius) == 0 && center.equals(sphere.center);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(center, radius);
    }
    
    public void setCenter(Vec vec) {
        this.center = vec;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }
}
