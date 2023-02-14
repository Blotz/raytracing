package org.openjfx;

public interface Hittable {
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec);
}

