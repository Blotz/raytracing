package org.openjfx;

public class DiffuseLight implements Material {
    private Vec emit;
    
    public DiffuseLight(Vec emit) {
        this.emit = emit;
    }
    @Override
    public Vec emitted() {
        return emit;
    }
    
    @Override
    public double r() {
        return this.emit.x();
    }
    
    @Override
    public double g() {
        return this.emit.y();
    }
    
    @Override
    public double b() {
        return this.emit.z();
    }
    
    @Override
    public void setColor(double r, double g, double b) {
        this.emit.set(new Vec(r, g, b));
    }
    
}
