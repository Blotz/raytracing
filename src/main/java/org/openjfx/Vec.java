package org.openjfx;

import java.util.Objects;

/**
 * Vec
 * Used the raytracer tutorial from https://raytracing.github.io/books/RayTracingInOneWeekend.html
 */
public class Vec {
    private double x;
    private double y;
    private double z;
    
    
    public Vec(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double x() {
        return this.x;
    }
    public double y() {
        return this.y;
    }
    public double z() {
        return this.z;
    }
    
    public Vec neg() {
        return new Vec(-this.x, -this.y, -this.z);
    }
    
    public Vec add(Vec v) {
        return new Vec(
          this.x + v.x(),
          this.y + v.y(),
          this.z + v.z()
        );
    }
    public Vec sub(Vec v) {
        return new Vec(
          this.x - v.x(),
          this.y - v.y(),
          this.z - v.z()
        );
    }
    public Vec mult(double t) {
        return new Vec(
          this.x * t,
          this.y * t,
          this.z * t
        );
    }
    public Vec div(double t) {
        return new Vec(
          this.x / t,
          this.y / t,
          this.z / t
        );
    }
    
    public double lengthSquared() {
        return (this.x*this.x + this.y*this.y + this.z*this.z);
    }
    public double length() {
        return Math.sqrt(this.lengthSquared());
    }
    
    @Override
    public String toString() {
        return "Vec{" +
          "x=" + x +
          ", y=" + y +
          ", z=" + z +
          '}';
    }
    
    public static Vec add(Vec v1, Vec v2) {
        return new Vec(v1.x() + v2.x(), v1.y() + v2.y(), v1.z() + v2.z());
    }
    public static Vec sub(Vec v1, Vec v2) {
        return new Vec(v1.x() - v2.x(), v1.y() - v2.y(), v1.z() - v2.z());
    }
    public static Vec mult(Vec v1, Vec v2) {
        return new Vec(v1.x() * v2.x(), v1.y() * v2.y(), v1.z() * v2.z());
    }
    public static Vec mult(Vec v, double t) {
        return new Vec(v.x() * t, v.y() * t, v.z() * t);
    }
    public static Vec div(Vec v, double t) {
        return new Vec(v.x() / t, v.y() / t, v.z() / t);
    }
    
    public static double dot(Vec v1, Vec v2) {
        return v1.x() * v2.x() + v1.y() * v2.y() + v1.z() * v2.z();
    }
    public static Vec cross(Vec v1, Vec v2) {
        return new Vec(v1.y() * v2.z() - v1.z() * v2.y(),
                       v1.z() * v2.x() - v1.x() * v2.z(),
                       v1.x() * v2.y() - v1.y() * v2.x());
    }
    
    public static Vec unitVector(Vec v) {
        return Vec.div(v, v.length());
    }
    
    public static int writeColor(Vec color, int samplesPerPixel) {
        double r = color.x();
        double g = color.y();
        double b = color.z();
        
        double scale = 1.0 / samplesPerPixel;
        r = Math.sqrt(scale * r);
        g = Math.sqrt(scale * g);
        b = Math.sqrt(scale * b);
        
        // bit shift right to argb format
        return (255 << 24) | ((int)(255.999 * r) << 16) | ((int)(255.999 * g) << 8) | (int)(255.999 * b);
    }
    
    
    public static double randomDouble() {
        return Math.random();
    }
    public static double randomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }
    public static Vec random() {
        return new Vec(Math.random(), Math.random(), Math.random());
    }
    public static Vec random(double min, double max) {
        return new Vec(Math.random() * (max - min) + min, Math.random() * (max - min) + min, Math.random() * (max - min) + min);
    }
    public static Vec randomInUnitSphere() {
        while (true) {
            Vec p = Vec.random(-1, 1);
            if (p.lengthSquared() >= 1) continue;
            return p;
        }
    }
    
    public static Vec randomInHemisphere(Vec normal) {
        Vec inUnitSphere = Vec.randomInUnitSphere();
        if (Vec.dot(inUnitSphere, normal) > 0.0) {
            return inUnitSphere;
        } else {
            return inUnitSphere.neg();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec vec = (Vec) o;
        return Double.compare(vec.x, x) == 0 && Double.compare(vec.y, y) == 0 && Double.compare(vec.z, z) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
