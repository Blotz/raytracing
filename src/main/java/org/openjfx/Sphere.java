package org.openjfx;

public class Sphere implements Hittable {
    Vec center;
    double radius;
    
    public Sphere(Vec center, double radius) {
        this.center = center;
        this.radius = radius;
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
        
        return true;
    }
}
