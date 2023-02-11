package org.openjfx;

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
    public double get (int i) {
        switch (i) {
            case 0:
                return this.x;
            case 1:
                return this.y;
            case 2:
                return this.z;
            default:
                throw new IllegalArgumentException("Index must be 0, 1, or 2");
        }
    }
    
    public Vec neg() {
        return new Vec(-this.x, -this.y, -this.z);
    }
    
    public Vec add(Vec v) {
        this.x += v.x();
        this.y += v.y();
        this.z += v.z();
        return this;
    }
    public Vec sub(Vec v) {
        this.x -= v.x();
        this.y -= v.y();
        this.z -= v.z();
        return this;
    }
    public Vec mult(double t) {
        this.x *= t;
        this.y *= t;
        this.z *= t;
        return this;
    }
    public Vec div(double t) {
        this.x /= t;
        this.y /= t;
        this.z /= t;
        return this;
    }
    
    public double length_squared() {
        return (this.x*this.x + this.y*this.y + this.z*this.z);
    }
    public double length() {
        return Math.sqrt(this.length_squared());
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
    
    public static int writeColor(Vec color) {
        // bit shift right to argb format
        return (255 << 24) | ((int)(255.999 * color.x()) << 16) | ((int)(255.999 * color.y()) << 8) | (int)(255.999 * color.z());
    }
    
}
