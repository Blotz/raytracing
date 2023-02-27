package org.openjfx;

import java.util.ArrayList;

public class HittableList {
    private ArrayList<Hittable> objects = new ArrayList<Hittable>();
    
    public HittableList add(Hittable object) {
        objects.add(object);
        return this;
    }
    
    public ArrayList<Hittable> getObjects() {
        return objects;
    }
    
    /**
     * Check if the ray hits any object in the world
     * @param r Ray
     * @param tMin Minimum distance to check
     * @param tMax Maximum distance to check
     * @param rec HitRecord
     * @return
     */
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        HitRecord tempRec = new HitRecord();
        boolean hitAnything = false;
        double closestSoFar = tMax;
        
        for (Hittable object : objects) {
            if (object.hit(r, tMin, closestSoFar, tempRec)) {
                hitAnything = true;
                closestSoFar = tempRec.t;
                // update rec
                rec.t = tempRec.t;
                rec.p = tempRec.p;
                rec.normal = tempRec.normal;
                rec.frontFace = tempRec.frontFace;
                rec.material = tempRec.material;
            }
        }
        
        return hitAnything;
    }
    
    public void remove(Sphere sphere) {
        objects.remove(sphere);
    }
}
