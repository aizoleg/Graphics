package com.company;

// математические методы
public class MathTools {
    // барицентрические координаты точки для данного треугольника
    public static Coord barycentric(int x, int y, Coord xtri, Coord ytri){
        double x0=xtri.getX();
        double x1=xtri.getY();
        double x2=xtri.getZ();
        double y0=ytri.getX();
        double y1=ytri.getY();
        double y2=ytri.getZ();
        return new Coord(
                ((x1 - x2)*(y - y2) - (y1 - y2)*(x - x2)) / ((x1 - x2)*(y0 - y2) - (y1 - y2)*(x0 - x2)),
                ((x2 - x0)*(y - y0) - (y2 - y0)*(x - x0)) / ((x2 - x0)*(y1 - y0) - (y2 - y0)*(x1 - x0)),
                ((x0 - x1)*(y - y1) - (y0 - y1)*(x - x1)) / ((x0 - x1)*(y2 - y1) - (y0 - y1)*(x2 - x1))
        );
    }

    // значение нормали треугольника
    public static Coord normal(Coord c0, Coord c1, Coord c2){
        double x0=c0.getX();
        double x1=c1.getX();
        double x2=c2.getX();
        double y0=c0.getY();
        double y1=c1.getY();
        double y2=c2.getY();
        double z0=c0.getZ();
        double z1=c1.getZ();
        double z2=c2.getZ();
        return new Coord(
                (y2-y0)*(z1-z0)-(z2-z0)*(y1-y0),
                (x1-x0)*(z2-z0)-(x2-x0)*(z1-z0),
                (x2-x0)*(y1-y0)-(x1-x0)*(y2-y0)
        );
    }

    // нормализованное скалярное произведение. -1 - противонаправлены, 0 - перпендикулярны, 1 - сонаправлены
    public static double normDotProduct(Coord n, Coord v){
        double nx=n.getX();
        double ny=n.getY();
        double nz=n.getZ();
        double vx=v.getX();
        double vy=v.getY();
        double vz=v.getZ();
        return (nx* vx+ny*vy+nz*vz)/Math.sqrt((nx*nx+ny*ny+nz*nz)*(vx*vx+vy*vy+vz*vz));
    }

    // поворот точки вокруг всех осей. Сначала x, потом y, потом z
    public static Coord rotate(Coord a, double alpha, double beta, double gamma){
        double sa=Math.sin(alpha);
        double ca=Math.cos(alpha);
        double sb=Math.sin(beta);
        double cb=Math.cos(beta);
        double sg=Math.sin(gamma);
        double cg=Math.cos(gamma);

        double[][] mx=new double[][]{
                {1,0,0},
                {0,ca,sa},
                {0,-sa,ca}};
        double[][] my=new double[][]{
                {cb,0,sb},
                {0,1,0},
                {-sb,0,cb}};
        double[][] mz=new double[][]{
                {cg,sg,0},
                {-sg,cg,0},
                {0,0,1}};
        double[][] input=new double[][]{{a.getX(),a.getY(),a.getZ()}};

        double[][] output=matMul(input,matMul(mx,matMul(my,mz,3,3,3),3,3,3),1,3,3);

        return new Coord(output[0][0],output[0][1],output[0][2]);
    }

    // умножение матриц a(x*y) и b(y*z)
    public static double[][] matMul(double[][] a, double[][] b, int x, int y, int z){
        double[][] rez=new double[x][z];
        for(int i=0; i<x; i++)
            for(int j=0; j< z; j++)
                for(int k=0; k<y; k++)
                    rez[i][j]+=a[i][k]*b[k][j];
        return rez;
    }

    // сложение матриц a(x*y) и b(x*y)
    public static double[][] matSum(double[][] a, double[][] b, int x, int y){
        double[][] rez=new double[x][y];
        for(int i=0; i<x; i++)
            for(int j=0; j<y; j++)
                rez[i][j]=a[i][j]+b[i][j];
        return rez;
    }

    // умножение матрицы a(x*y) на число b
    public static double[][] scalMul(double[][] a, int x, int y, double b){
        double[][] rez=new double[x][y];
        for(int i=0; i<x; i++)
            for(int j=0; j<y; j++)
                rez[i][j]=a[i][j]*b;
        return rez;
    }

    // обратная матрица
    public static double[][] inversion(double[][] A, int N) {
        double temp;

        double[][] E = new double[N][N];

        double[][] A1 = new double[N][N];
        for(int i=0; i<N; i++)
            for(int j=0; j<N; j++)
                A1[i][j]=A[i][j];

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                E[i][j] = 0f;
                if (i == j)
                    E[i][j] = 1f;
            }

        for (int k = 0; k < N; k++) {
            temp = A1[k][k];
            for (int j = 0; j < N; j++) {
                A1[k][j] /= temp;
                E[k][j] /= temp;
            }

            for (int i = k + 1; i < N; i++) {
                temp = A1[i][k];
                for (int j = 0; j < N; j++) {
                    A1[i][j] -= A1[k][j] * temp;
                    E[i][j] -= E[k][j] * temp;
                }
            }
        }

        for (int k = N - 1; k > 0; k--) {
            for (int i = k - 1; i >= 0; i--) {
                temp = A1[i][k];
                for (int j = 0; j < N; j++) {
                    A1[i][j] -= A1[k][j] * temp;
                    E[i][j] -= E[k][j] * temp;
                }
            }
        }

        return E;
    }

    // точка в новой системе координат. Получает на вход координаты точки, обратную матрицу перехода, начало координат в новой системе
    public static Coord ncs(Coord coord, double[][] a, double[][] o){
        double[][] oldCoord=new double[][]{
                {coord.getX()},
                {coord.getY()},
                {coord.getZ()}};

        double[][] rez=matSum(matMul(a,oldCoord,3,3,1),o,3,1);

        return new Coord(rez[0][0],rez[1][0],rez[2][0]);
    }
}