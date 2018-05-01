package dongeronimo.com.testejpct;

import android.util.Log;

/**
 * https://en.wikipedia.org/wiki/Image_gradient
 * Encapsula o cálculo do gradiente e da magnitude do gradiente do mapa. Os cálculos são feitos no
 * construtor e são imutáveis depois de feitos.
 * O mapa é uma função F(x,y) = h onde h é a altura. Sendo uma função 2d a derivada dela é parcial.
 * O gradiente da imagem é um vetor de suas derivadas parciais dI = [dI/dX, dI/dY]. A magnitude do gradiente
 * é a magnitude desse vetor*/
public class GradientCalculator {
    private float data[][];
    private float gX[][];
    private float gY[][];
    public GradientCalculator(float data[][]){
        this.data = data;
        gX = new float[data.length][data[0].length];
        gY = new float[data.length][data[0].length];
        //cálculo do gradiente horizontal
        for(int y=0; y<data.length; y++){
            for(int x=0; x<data[y].length; x++){
                float f0 = x==0?data[x][y]:data[x-1][y];//isso aqui é pra lidar com a borda esquerda
                float f1 = data[x][y];
                float f2 = x==(data[y].length-1)? data[x][data[y].length-1]: data[x+1][y];//isso aqui é pra lidar com a borda direita
                float dIdX = f0 * -1.0f + f1 * 0.0f + f2 * 1.0f;
                gX[x][y] = dIdX;
            }
        }
        //cálculo do gradiente vertical
        for(int y=0; y<data.length; y++){
            for(int x=0; x<data[y].length; x++){
                float f0 = y==0?data[x][y]: data[x][y-1];
                float f1 = data[x][y];
                float f2 = y==(data.length-1)?data[x][y]:data[x][y+1];
                float dIdY = f0 * -1.0f + f1 * 0.0f + f2 * 1.0f;
                gY[x][y] = dIdY;
            }
        }
        Log.d("aa","calc grad");
        //cálculo da magnitude do gradiente
    }
}
