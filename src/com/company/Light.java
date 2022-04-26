package com.company;

// класс света в трёхмерном пространстве
public class Light {
    // вектор направления света. может быть любой длины
    private Coord direction;
    // цвет света. для получения яркости [0,1] необходимо каждую компоненту разделить на 255
    private Color color;

    public Light(){
        this.direction=new Coord(0,0,1);
        this.color=new Color(255);
    }

    public Light(Coord direction, Color color){
        this.direction=direction;
        this.color=color;
    }

    public Coord getDirection() {
        return direction;
    }

    public void setDirection(Coord direction) {
        this.direction = direction;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}