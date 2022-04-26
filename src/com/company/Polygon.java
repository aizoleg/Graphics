package com.company;

import java.util.ArrayList;

// класс полигона
public class Polygon {

    // массив вершин
    private ArrayList<Vertice> vertices;

    public Polygon(){
        vertices=new ArrayList<Vertice>();
    }

    public ArrayList<Vertice> getVertices() {
        return vertices;
    }

    public void addVertice(Vertice vertice) {
        vertices.add(vertice);
    }
}