package org.openjfx;

public class HitRecord {
    public Vec p;  // point of intersection
    public Vec normal;  // normal at point of intersection
    public double t;  // parameter of ray at point of intersection
    public boolean frontFace;  // is the normal pointing outward?
    
    public void setFaceNormal(Ray r, Vec outwardNormal) {
        frontFace = Vec.dot(r.direction(), outwardNormal) < 0;
        if (frontFace) {
            normal = outwardNormal;
        } else {
            normal = outwardNormal.neg();
        }
    }
}