package com.company;

// класс цвета
public class Color {
    // значение каждой компоненты должно лежать в [0,255]
    private int r;
    private int g;
    private int b;

    public Color() {
        r = 0;
        g = 0;
        b = 0;
    }

    public Color(int r, int g, int b) {
        this.b = b;
        this.r = r;
        this.g = g;
    }

    public Color(int a) {
        this.b = a;
        this.r = a;
        this.g = a;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}