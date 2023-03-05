package org.openjfx;

public class Dielectric implements Material {
    private double refIdx;
    private Vec albedo;
    
    public Dielectric(Vec albedo, double refIdx) {
        this.refIdx = refIdx;
        this.albedo = albedo;
    }
    
    @Override
    public boolean scatter(Ray rayIn, HitRecord hitRecord, Vec attenuation, Ray scattered) {
        double refractionRatio;
        // we need to know if the ray is entering or exiting the material
        if (hitRecord.frontFace) {
            refractionRatio = 1.0 / refIdx;
        } else {
            refractionRatio = refIdx;
        }
        
        Vec unitDirection = Vec.unitVector(rayIn.direction());
        
        // Calculating the total internal reflection
        // this is when something becomes a mirror at a low enough angle
        
        // sin(theta) = sqrt(1 - cos(theta)^2)
        double cosTheta = Math.min(Vec.dot(unitDirection.neg(), hitRecord.normal), 1.0);
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
        // if the n1/n2 * sin(theta) is greater than 1, then the ray cannot refract and will reflect
        boolean cannotRefract = refractionRatio * sinTheta > 1.0;
        
        
        Vec direction;
        // reflectivity is the probability that the ray will reflect
        // so there is always a chance that the ray will refract depending on the cos theta
        if (cannotRefract || reflectance(cosTheta, refractionRatio) > Math.random()) {
            // reflect
            direction = reflect(unitDirection, hitRecord.normal);
        } else {
            // refract
            direction = refract(unitDirection, hitRecord.normal, refractionRatio);
        }
        // set the scattered ray
        scattered.set(hitRecord.p, direction);
        // set the attenuation
        attenuation.set(albedo);
        
        return true;
    }
    
    private static Vec refract(Vec uv, Vec n, double refractRatio) {
        // snell's law
        // n1 sin(theta1) = n2 sin(theta2)
        
        // to get the refracted ray, we need to find the perpendicular and parallel components
        // of the refracted ray
        
        // perpendicular component
        //  = (n1/n2) * (R - n*cos(theta))
        double cosTheta = Math.min(Vec.dot(uv.neg(), n), 1.0);
        Vec perpRefract = Vec.mult(Vec.add(uv, Vec.mult(n, cosTheta)), refractRatio);
        
        // parallel component
        // = -sqrt(1 - perpendicular component length squared * n)
        Vec parallelRefrac = Vec.mult(n, -Math.sqrt(Math.abs(1.0 - perpRefract.lengthSquared())));
        
        return Vec.add(perpRefract, parallelRefrac);
    }
    
    private static Vec reflect(Vec v, Vec n) {
        // copy from Metal.class
        return Vec.sub(v, (Vec.mult(n, 2f * Vec.dot(v,n))));
    }
    
    private static double reflectance(double cosine, double refIdx) {
        // shlick approximation for reflectance
        // Calculates the probability that the ray will reflect
        double r0 = (1 - refIdx) / (1 + refIdx);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
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
    
    @Override
    public String toString() {
        return "Dielectric{" +
                "refIdx=" + refIdx +
                ", albedo=" + albedo +
                '}';
    }
    
    public double getRefractiveIndex() {
        return this.refIdx;
    }
}
