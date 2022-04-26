package com.company;

// класс вершины полигона
public class Vertice {
    // номер координаты вершины
    private int v;
    // номер текстурной координаты
    private int vt;
    // номер нормали
    private int vn;

    public Vertice(){
        this.v=0;
        this.vt=0;
        this.vn=0;
    }

    public Vertice(int v, int vt, int vn){
        this.v=v;
        this.vt=vt;
        this.vn=vn;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getVt() {
        return vt;
    }

    public void setVt(int vt) {
        this.vt = vt;
    }

    public int getVn() {
        return vn;
    }

    public void setVn(int vn) {
        this.vn = vn;
    }
}