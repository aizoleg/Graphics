package com.company;

// класс двумерной вещественной координаты
public class Coord2 {
    private double x;
    private double y;

    public Coord2(){
        x=0;
        y=0;
    }

    public Coord2(double x, double y){
        this.x=x;
        this.y=y;
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
}