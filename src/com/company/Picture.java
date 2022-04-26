package com.company;

// класс двумерного изображения
public class Picture {
    // ширина и высота
    private int w;
    private int h;

    // начальное заполнение z-буфера
    private static double z0=Math.pow(10,4);

    // двумерный массив цветов
    private Color[][] colorArray;

    // z-буффер
    private double[][] zbuf;


    public Picture(int w, int h) {
        this.w = w;
        this.h = h;
        colorArray = new Color[w][h];
        initArray();
        zbuf=new double[w][h];
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
                zbuf[i][j]=z0;
    }

    public Picture(int w, int h, Color color) {
        this.w = w;
        this.h = h;
        colorArray = new Color[w][h];
        initArray(color);
        zbuf=new double[w][h];
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
                zbuf[i][j]=z0;
    }

    public void setW(int w){
        this.w=w;
    }

    public int getW(){
        return w;
    }

    public void setH(int h){
        this.h=h;
    }

    public int getH(){
        return h;
    }

    public void setColorArray(Color[][] colorArray){
        this.colorArray=colorArray;
    }

    public Color[][] getColorArray(){
        return colorArray;
    }

    public double getZbuf(int i, int j){return zbuf[i][j];}

    public void setZbuf(int i, int j, double z){this.zbuf[i][j]=z;}

    private void initArray() { initArray(null); }

    private void initArray(Color color) {
        if(color == null) color = new Color();
        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                colorArray[i][j] = color;
            }
        }
    }
}