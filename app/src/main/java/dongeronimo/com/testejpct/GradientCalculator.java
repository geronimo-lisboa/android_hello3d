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
        for(int ln=0; ln<data.length; ln++){
            for(int col = 0; col < data[ln].length; col++){
                float f0 = col==0 ? data[ln][col] : data[ln][col-1];//isso aqui é pra lidar com a borda esquerda
                float f1 = data[ln][col];
                float f2 = col ==(data[ln].length-1)? data[ln][col]: data[ln][col+1];//isso aqui é pra lidar com a borda direita
                float dIdX = f0 * -1.0f + f1 * 0.0f + f2 * 1.0f;
                gX[ln][col] = dIdX;
            }
        }
        //cálculo do gradiente vertical
        for(int ln=0; ln<data.length; ln++){
            for(int col = 0; col < data[ln].length; col++){
                float f0 = ln==0 ? data[ln][col] : data[ln-1][col];
                float f1 = data[ln][col];
                float f2 = ln == (data.length-1) ? data[ln][col]: data[ln+1][col];//isso aqui é pra lidar com a borda direita
                float dIdY = f0 * -1.0f + f1 * 0.0f + f2 * 1.0f;
                gY[ln][col] = dIdY;
            }
        }
        Log.d("aa","calc grad");
        //cálculo da magnitude do gradiente
    }
}
