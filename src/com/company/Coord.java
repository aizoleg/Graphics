package com.company;

// класс трёхмерной вещественной координаты. но может использоваться как просто набор трёх double
public class Coord {
    private double x;
    private double y;
    private double z;

    public Coord(){
        x=0;
        y=0;
        z=0;
    }

    public Coord(double x, double y, double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}