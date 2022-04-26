package com.company;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

// методы для работы с obj-объектом
public class SimpleObjUtils {

    // парсинг объекта из .obj файла
    public static SimpleObj SimpleObjFromFile(String filename) throws Exception {

        // объект, в который будут внесены данные из файла
        SimpleObj simpleObj=new SimpleObj();
        // строка в файле
        String str;
        // сканер
        Scanner lineScanner;

        FileReader fr= new FileReader(filename);
        Scanner fileScanner = new Scanner(fr);

        // проходим по всем строкам
        while (fileScanner.hasNextLine()) {
            // считываем текущую строку
            str=fileScanner.nextLine();
            // пропускаем короткие строки
            if(str.length()<3) continue;
            // если это строка вершины
            if(str.charAt(0)=='v'&&str.charAt(1)==' '){
                lineScanner = new Scanner(str.substring(1)).useLocale(Locale.ENGLISH);
                // считываем трёхмерные координаты
                simpleObj.addCoord(new Coord(lineScanner.nextDouble(),lineScanner.nextDouble(),lineScanner.nextDouble()));
            }
            // если это строка текстурной координаты
            if(str.charAt(0)=='v'&&str.charAt(1)=='t'){
                lineScanner = new Scanner(str.substring(2)).useLocale(Locale.ENGLISH);
                // считываем двумерные координаты
                simpleObj.addTCoord(new Coord2(lineScanner.nextDouble(),lineScanner.nextDouble()));
            }
            // если это строка нормали
            if(str.charAt(0)=='v'&&str.charAt(1)=='n'){
                lineScanner = new Scanner(str.substring(2)).useLocale(Locale.ENGLISH);
                // считываем трёхмерные координаты
                simpleObj.addNormal(new Coord(lineScanner.nextDouble(),lineScanner.nextDouble(),lineScanner.nextDouble()));
            }
            // если это строка полигона, парсим ее вручную
            else if(str.charAt(0)=='f'){
                // полигон
                Polygon currPolygon=new Polygon();
                // значения для вершины
                int v=0, vt=0, vn=0;
                int k=0; // переключатель v/vt/vn
                // является ли число отрицательным
                boolean isNeg=false;
                // текущее число
                int currInt=0;
                // проходим по всей строке
                for(int i=2; i<str.length(); i++){
                    char currChar=str.charAt(i);
                    // если закончили считывать текущий int
                    if(currChar==' '||currChar=='/'){
                        // если мы считали число
                        if(currInt!=0){
                            // если оно отрицательное - меняем знак
                            if(isNeg) {
                                currInt*=-1;
                                isNeg=false;
                            }
                            // понимаем, за какой компонент вершины отвечает данное число
                            if(k==0) v=currInt;
                            else if(k==1) vt=currInt;
                            else vn=currInt;
                            // обнуляем текущее число
                            currInt=0;
                        }
                        // если закончили считывать вершину - сохраняем её
                        if(currChar==' '){
                            k=0;
                            if(v!=0)
                                currPolygon.addVertice(new Vertice(v, vt, vn));
                            v=0;
                            vt=0;
                            vn=0;
                        }
                        // иначе считываем следующую компоненту
                        else k++;
                    }
                    // если текущий символ '-' - запоминаем это
                    else if(currChar=='-') isNeg = true;
                        // если считываем цифру, записываем ее в текущее число
                    else if(currChar>='0'&&currChar<='9')
                        currInt=currInt*10+currChar-'0';
                }
                // после считывания строки сохраняем последнюю вершину при необходимости
                if(currInt!=0){
                    if(isNeg) currInt*=-1;
                    // понимаем, за какой компонент вершины отвечает данное число
                    if(k==0) v=currInt;
                    else if(k==1) vt=currInt;
                    else vn=currInt;
                    currInt=0;
                }
                // сохраняем последнюю считанную вершину
                if(v!=0)
                    currPolygon.addVertice(new Vertice(v, vt, vn));
                // добавляем полигон в список полигонов
                simpleObj.addPolygon(currPolygon);
            }
        }

        fr.close();
        return simpleObj;
    }

    // создание картинки со всеми вершинами объекта
    public static Picture SimpleObjToPicture0(SimpleObj simpleObj, int w, int h, double scale, int xShift, int yShift){
        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        // проходим по всем вершинам и рисуем соответствующий пиксель
        for(int i=0; i<coords.size();i++)
            PictureUtils.drawPixel(picture, (int)Math.round(coords.get(i).getX()*scale+xShift), (int)Math.round(coords.get(i).getY()*scale+yShift), new Color(255,255,255));
        return picture;
    }

    // создание картинки со всеми рёбрами
    public static Picture SimpleObjToPicture1(SimpleObj simpleObj, int w, int h, double scale, int xShift, int yShift){
        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // соединяем последовательно все соседние вершины отрезками
            for(int j=0; j<vertices.size()-1; j++){
                int v0=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                int v1=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                PictureUtils.drawLine3(picture,
                        (int)Math.round(coords.get(v0).getX()*scale+xShift),
                        (int)Math.round(coords.get(v0).getY()*scale+yShift),
                        (int)Math.round(coords.get(v1).getX()*scale+xShift),
                        (int)Math.round(coords.get(v1).getY()*scale+yShift),
                        new Color(255,255,255)
                );
            }
            // соединяем последовательно все вершины с первой отрезками
            for(int j=2; j<vertices.size(); j++){
                int v0=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
                int v1=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                PictureUtils.drawLine3(picture,
                        (int)Math.round(coords.get(v0).getX()*scale+xShift),
                        (int)Math.round(coords.get(v0).getY()*scale+yShift),
                        (int)Math.round(coords.get(v1).getX()*scale+xShift),
                        (int)Math.round(coords.get(v1).getY()*scale+yShift),
                        new Color(255,255,255)
                );
            }
        }
        return picture;
    }

    // создание картинки из цветных полигонов
    public static Picture SimpleObjToPicture2(SimpleObj simpleObj, int w, int h, double scale, int xShift, int yShift){
        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        final Random random = new Random();
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // рисуем все треугольники с двумя соседними вершинами и нулевой вершиной
            int v0=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
            for(int j=1; j<vertices.size()-1; j++){
                int v1=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                int v2=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                PictureUtils.drawTriangle(picture,
                        new Coord(coords.get(v0).getX()*scale+xShift, coords.get(v1).getX()*scale+xShift, coords.get(v2).getX()*scale+xShift),
                        new Coord(coords.get(v0).getY()*scale+yShift, coords.get(v1).getY()*scale+yShift, coords.get(v2).getY()*scale+yShift),
                        new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            }
        }
        return picture;
    }

    // без полигонов повернутых от камеры
    public static Picture SimpleObjToPicture3(SimpleObj simpleObj, int w, int h, double scale, int xShift, int yShift){
        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        final Random random = new Random();
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // рисуем все треугольники с двумя соседними вершинами и нулевой вершиной
            int v0=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
            for(int j=1; j<vertices.size()-1; j++){
                int v2=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                int v1=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                if(MathTools.normDotProduct(MathTools.normal(coords.get(v0),coords.get(v1),coords.get(v2)),new Coord(0,0,-1))<0)
                    PictureUtils.drawTriangle(picture,
                            new Coord(coords.get(v0).getX()*scale+xShift, coords.get(v1).getX()*scale+xShift, coords.get(v2).getX()*scale+xShift),
                            new Coord(coords.get(v0).getY()*scale+yShift, coords.get(v1).getY()*scale+yShift, coords.get(v2).getY()*scale+yShift),
                            new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            }
        }
        return picture;
    }

    // базовое освещение
    public static Picture SimpleObjToPicture4(SimpleObj simpleObj, int w, int h, double scale, int xShift, int yShift){
        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        final Random random = new Random();
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // рисуем все треугольники с двумя соседними вершинами и нулевой вершиной
            int v0=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
            for(int j=1; j<vertices.size()-1; j++){
                int v2=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                int v1=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                double normal=MathTools.normDotProduct(MathTools.normal(coords.get(v0),coords.get(v1),coords.get(v2)),new Coord(0,0,-1));
                if(normal<0)
                    PictureUtils.drawTriangle(picture,
                            new Coord(coords.get(v0).getX()*scale+xShift, coords.get(v1).getX()*scale+xShift, coords.get(v2).getX()*scale+xShift),
                            new Coord(coords.get(v0).getY()*scale+yShift, coords.get(v1).getY()*scale+yShift, coords.get(v2).getY()*scale+yShift),
                            new Color((int)Math.round(-255*normal), (int)Math.round(-255*normal), (int)Math.round(-255*normal)));
            }
        }
        return picture;
    }

    // с z-буффером
    public static Picture SimpleObjToPicture5(SimpleObj simpleObj, int w, int h, double scale, int xShift, int yShift){
        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // рисуем все треугольники с двумя соседними вершинами и нулевой вершиной
            int v0=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
            for(int j=1; j<vertices.size()-1; j++){
                int v2=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                int v1=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                double normal=MathTools.normDotProduct(MathTools.normal(coords.get(v0),coords.get(v1),coords.get(v2)),new Coord(0,0,-1));
                if(normal<0)
                    PictureUtils.drawTriangleZ(picture,
                            new Coord(coords.get(v0).getX()*scale+xShift, coords.get(v1).getX()*scale+xShift, coords.get(v2).getX()*scale+xShift),
                            new Coord(coords.get(v0).getY()*scale+yShift, coords.get(v1).getY()*scale+yShift, coords.get(v2).getY()*scale+yShift),
                            new Coord(-coords.get(v0).getZ(), -coords.get(v1).getZ(), -coords.get(v2).getZ()),
                            new Color((int)Math.round(-255*normal), (int)Math.round(-255*normal), (int)Math.round(-255*normal)));
            }
        }
        return picture;
    }

    // проективное преобразование
    public static Picture SimpleObjToPicture6(SimpleObj simpleObj, int w, int h, double ax, double ay, double x0, double y0, double zShift){
        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // рисуем все треугольники с двумя соседними вершинами и нулевой вершиной
            int v0=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
            for(int j=1; j<vertices.size()-1; j++){
                int v2=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                int v1=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                double normal=MathTools.normDotProduct(MathTools.normal(coords.get(v0),coords.get(v1),coords.get(v2)),new Coord(0,0,-1));
                if(normal<1){
                    double z0=-coords.get(v0).getZ()+zShift;
                    double z1=-coords.get(v1).getZ()+zShift;
                    double z2=-coords.get(v2).getZ()+zShift;
                    int color=(int)Math.round(-255*normal);
                    if(color<0) color=0;
                    PictureUtils.drawTriangleZ(picture,
                            new Coord(coords.get(v0).getX()*ax/z0+x0, coords.get(v1).getX()*ax/z1+x0, coords.get(v2).getX()*ax/z2+x0),
                            new Coord(coords.get(v0).getY()*ax/z0+y0, coords.get(v1).getY()*ax/z1+y0, coords.get(v2).getY()*ax/z2+y0),
                            new Coord(z0, z1, z2),
                            new Color(color, color, color));
                }
            }
        }
        return picture;
    }

    // поворот объекта
    public static void RotateSimpleObj(SimpleObj simpleObj, double alpha, double beta, double gamma){
        ArrayList<Coord> origCoords=simpleObj.getCoords();
        ArrayList<Coord> newCoords=new ArrayList<Coord>();
        ArrayList<Coord> origNormals=simpleObj.getNormals();
        ArrayList<Coord> newNormals=new ArrayList<Coord>();
        for (int i=0; i< origCoords.size(); i++)
            newCoords.add(MathTools.rotate(origCoords.get(i),alpha,beta,gamma));
        for (int i=0; i< origNormals.size(); i++)
            newNormals.add(MathTools.rotate(origNormals.get(i),alpha,beta,gamma));
        simpleObj.setCoords(newCoords);
        simpleObj.setNormals(newNormals);
    }

    // тонировка Гуро
    public static Picture SimpleObjToPicture7(SimpleObj simpleObj, int w, int h, Camera camera, Light light){
        double ex=1/Math.tan(camera.getXAngle()/2);
        double ey=1/Math.tan(camera.getYAngle()/2);
        double n=camera.getN();
        double f=camera.getF();
        Coord camDirection=MathTools.rotate(new Coord(0,0,-1), camera.getAlpha(), camera.getBeta(),0);

        Coord xe=MathTools.rotate(new Coord(1,0,0), camera.getAlpha(), camera.getBeta(), 0);
        Coord ye=MathTools.rotate(new Coord(0,1,0), camera.getAlpha(), camera.getBeta(), 0);
        Coord ze=MathTools.rotate(new Coord(0,0,1), camera.getAlpha(), camera.getBeta(), 0);
        double[][] A=new double[][]{
                {xe.getX(), ye.getX(), ze.getX()},
                {xe.getY(), ye.getY(), ze.getY()},
                {xe.getZ(), ye.getZ(), ze.getZ()}};
        double[][] AInv=MathTools.inversion(A,3);
        double[][] newO = new double[][]{
                {camera.getPosition().getX()},
                {camera.getPosition().getY()},
                {camera.getPosition().getZ()}};
        double[][] oldO = MathTools.matMul(MathTools.scalMul(AInv,3,3,-1),newO, 3, 3, 1);

        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Coord> normals=simpleObj.getNormals();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // рисуем все треугольники с двумя соседними вершинами и нулевой вершиной
            int[] v=new int[3];
            int[] vn=new int[3];
            v[0]=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
            vn[0]=vertices.get(0).getVn()>0?vertices.get(0).getVn()-1:coords.size()+vertices.get(0).getVn();
            for(int j=1; j<vertices.size()-1; j++){
                v[2]=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                v[1]=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                vn[2]=vertices.get(j).getVn()>0?vertices.get(j).getVn()-1:coords.size()+vertices.get(j).getVn();
                vn[1]=vertices.get(j+1).getVn()>0?vertices.get(j+1).getVn()-1:coords.size()+vertices.get(j+1).getVn();
                Coord normal=MathTools.normal(coords.get(v[0]),coords.get(v[1]),coords.get(v[2]));
                Coord[] vNormals=new Coord[3];
                double[] brightness=new double[3];
                for(int k=0; k<3; k++){
                    vNormals[k]=normals.get(vn[k]);
                    brightness[k]=-MathTools.normDotProduct(vNormals[k],light.getDirection());
                }
                if(MathTools.normDotProduct(normal,camDirection)<=2){
                    Coord[] tri=new Coord[3];
                    for(int k=0; k<3; k++){
                        Coord coord=MathTools.ncs(coords.get(v[k]),AInv,oldO);
                        double distance = Math.abs(coord.getZ());
                        tri[k]=new Coord();
                        tri[k].setX((1+ex*coord.getX()/distance)*w/2);
                        tri[k].setY((1+ey*coord.getY()/distance)*w/2);
                        tri[k].setZ((f+n)/(f-n)-2*f*n/((f-n)*distance));
                    }

                    PictureUtils.drawTriangleZg(picture, tri, brightness, light.getColor());
                }
            }
        }
        return picture;
    }

    // текстуры
    public static Picture SimpleObjToPicture8(SimpleObj simpleObj, int w, int h, Camera camera, Light light, String textureFilename) throws IOException {
        // загружаем текстуру из файла
        Picture texture=PictureUtils.loadPicture(textureFilename);

        // расчитываем необходимые параметры камеры
        double ex=1/Math.tan(camera.getXAngle()/2);
        double ey=1/Math.tan(camera.getYAngle()/2);
        double n=camera.getN();
        double f=camera.getF();
        Coord camDirection=MathTools.rotate(new Coord(0,0,-1), camera.getAlpha(), camera.getBeta(),0);

        // находим все необходимые объекты для проекции точек на камеру
        Coord xe=MathTools.rotate(new Coord(1,0,0), camera.getAlpha(), camera.getBeta(), 0);
        Coord ye=MathTools.rotate(new Coord(0,1,0), camera.getAlpha(), camera.getBeta(), 0);
        Coord ze=MathTools.rotate(new Coord(0,0,1), camera.getAlpha(), camera.getBeta(), 0);
        double[][] A=new double[][]{
                {xe.getX(), ye.getX(), ze.getX()},
                {xe.getY(), ye.getY(), ze.getY()},
                {xe.getZ(), ye.getZ(), ze.getZ()}};
        double[][] AInv=MathTools.inversion(A,3);
        double[][] newO = new double[][]{
                {camera.getPosition().getX()},
                {camera.getPosition().getY()},
                {camera.getPosition().getZ()}};
        double[][] oldO = MathTools.matMul(MathTools.scalMul(AInv,3,3,-1),newO, 3, 3, 1);

        Picture picture=new Picture(w,h);
        ArrayList<Coord> coords=simpleObj.getCoords();
        ArrayList<Coord> normals=simpleObj.getNormals();
        ArrayList<Coord2> tCoords=simpleObj.gettCoords();
        ArrayList<Polygon> polygons= simpleObj.getPolygons();
        // проходим по всем полигонам
        for(int i=0; i<polygons.size();i++){
            ArrayList<Vertice> vertices = polygons.get(i).getVertices();
            // рисуем все треугольники с двумя соседними вершинами и нулевой вершиной
            int[] v=new int[3];
            int[] vn=new int[3];
            int[] vt=new int[3];
            v[0]=vertices.get(0).getV()>0?vertices.get(0).getV()-1:coords.size()+vertices.get(0).getV();
            vn[0]=vertices.get(0).getVn()>0?vertices.get(0).getVn()-1:normals.size()+vertices.get(0).getVn();
            vt[0]=vertices.get(0).getVt()>0?vertices.get(0).getVt()-1:tCoords.size()+vertices.get(0).getVt();
            for(int j=1; j<vertices.size()-1; j++){
                v[2]=vertices.get(j).getV()>0?vertices.get(j).getV()-1:coords.size()+vertices.get(j).getV();
                v[1]=vertices.get(j+1).getV()>0?vertices.get(j+1).getV()-1:coords.size()+vertices.get(j+1).getV();
                vn[2]=vertices.get(j).getVn()>0?vertices.get(j).getVn()-1:normals.size()+vertices.get(j).getVn();
                vn[1]=vertices.get(j+1).getVn()>0?vertices.get(j+1).getVn()-1:normals.size()+vertices.get(j+1).getVn();
                vt[2]=vertices.get(j).getVt()>0?vertices.get(j).getVt()-1:tCoords.size()+vertices.get(j).getVt();
                vt[1]=vertices.get(j+1).getVt()>0?vertices.get(j+1).getVt()-1:tCoords.size()+vertices.get(j+1).getVt();
                // общая нормаль треугольника
                Coord normal=MathTools.normal(coords.get(v[0]),coords.get(v[1]),coords.get(v[2]));
                // нормали вершин треугольника
                Coord[] triNormals=new Coord[3];
                // текстурные координаты вершин треугольника
                Coord2[] triTCoords=new Coord2[3];
                // значение яркости вершин треугольника
                double[] brightness=new double[3];
                for(int k=0; k<3; k++){
                    triTCoords[k]=tCoords.get(vt[k]);
                    triNormals[k]=normals.get(vn[k]);
                    brightness[k]=-MathTools.normDotProduct(triNormals[k],light.getDirection());
                }
                // тут можно сразу отсечь часть граней, но в данном случае нелицевые грани будут нарисованы черным цветом
                if(MathTools.normDotProduct(normal,camDirection)<=1){
                    // координаты точек треугольника в системе координат камеры
                    Coord[] tri=new Coord[3];
                    for(int k=0; k<3; k++){
                        Coord coord=MathTools.ncs(coords.get(v[k]),AInv,oldO);
                        tri[k]=new Coord();
                        // расстояние по оси z камеры
                        double distance=Math.abs(coord.getZ());
                        // координаты треугольника с учетом проективныч искажений
                        tri[k].setX((1+ex*coord.getX()/distance)*w/2);
                        tri[k].setY((1+ey*coord.getY()/distance)*h/2);
                        tri[k].setZ((f+n)/(f-n)-2*f*n/((f-n)*distance));
                    }

                    // отрисовываем треугольник
                    PictureUtils.drawTriangleZt(picture, tri, brightness, light.getColor(),triTCoords,texture);
                }
            }
        }
        return picture;
    }

}