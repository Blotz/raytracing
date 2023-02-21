package org.openjfx;

public interface Material {
    public boolean scatter(Ray rayIn, HitRecord hitRecord, Vec attenuation, Ray scattered);
}
