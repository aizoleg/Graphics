package com.company;

import java.util.ArrayList;

// класс obj-объекта
public class SimpleObj {

    // массив координат вершин
    private ArrayList<Coord> coords;

    // массив нормалей вершин
    private ArrayList<Coord> normals;

    // массив координат текстуры
    private ArrayList<Coord2> tCoords;

    // массив полигонов
    private ArrayList<Polygon> polygons;

    public SimpleObj(){
        coords=new ArrayList<Coord>();
        normals=new ArrayList<Coord>();
        tCoords=new ArrayList<Coord2>();
        polygons=new ArrayList<Polygon>();
    }

    public ArrayList<Coord> getCoords() {
        return coords;
    }

    public void addCoord(Coord coord) {
        coords.add(coord);
    }

    public void setCoords(ArrayList<Coord> coords){ this.coords=coords;}

    public ArrayList<Coord> getNormals() {
        return normals;
    }

    public void addNormal(Coord normal) {
        normals.add(normal);
    }

    public ArrayList<Coord2> gettCoords() {
        return tCoords;
    }

    public void addTCoord(Coord2 tCoord) {
        tCoords.add(tCoord);
    }

    public void setNormals(ArrayList<Coord> normals) {
        this.normals = normals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }
}