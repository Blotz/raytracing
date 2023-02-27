package org.openjfx;

public interface Hittable {
    /**
     * Check if the ray hits the object
     * @param r Ray
     * @param tMin Minimum distance to check
     * @param tMax Maximum distance to check
     * @param rec HitRecord
     * @return true if the ray hits the object
     */
    boolean hit(Ray r, double tMin, double tMax, HitRecord rec);
}

