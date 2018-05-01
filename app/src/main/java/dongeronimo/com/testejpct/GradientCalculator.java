package dongeronimo.com.testejpct;

/**
 * https://en.wikipedia.org/wiki/Image_gradient
 * Encapsula o cálculo do gradiente e da magnitude do gradiente do mapa. Os cálculos são feitos no
 * construtor e são imutáveis depois de feitos.
 * O mapa é uma função F(x,y) = h onde h é a altura. Sendo uma função 2d a derivada dela é parcial.
 * O gradiente da imagem é um vetor de suas derivadas parciais dI = [dI/dX, dI/dY]. A magnitude do gradiente
 * é a magnitude desse vetor*/
public class GradientCalculator {
    private float data[][];
    //O valor dos componentes horizontais do gradiente (dIdX)
    private float g_x[][];
    //o valor dos componentes verticais do gradiente(dIdY);
    private float g_y[][];
    //A magnitude do gradiente
    private float mag[][];
    //A direção do gradiente
    private float direction[][];
    public GradientCalculator(float data[][]){
        this.data = data;
        g_x = new float[data.length][data[0].length];
        g_y = new float[data.length][data[0].length];
        mag = new float[data.length][data[0].length];
        direction = new float[data.length][data[0].length];
        //cálculo do gradiente horizontal
        for(int ln=0; ln<data.length; ln++){
            for(int col = 0; col < data[ln].length; col++){
                //Cálculo do gradiente horizontal
                float _x0 = col==0 ? data[ln][col] : data[ln][col-1];//isso aqui é pra lidar com a borda esquerda
                float _x = data[ln][col];
                float _x1 = col ==(data[ln].length-1)? data[ln][col]: data[ln][col+1];//isso aqui é pra lidar com a borda direita
                float dIdX = _x0 * -1.0f + _x * 0.0f + _x1 * 1.0f;
                g_x[ln][col] = dIdX;
                //cálculo do gradiente vertical
                float _y0 = ln==0 ? data[ln][col] : data[ln-1][col];
                float _y = data[ln][col];
                float _y1 = ln == (data.length-1) ? data[ln][col]: data[ln+1][col];//isso aqui é pra lidar com a borda direita
                float dIdY = _y0 * -1.0f + _y * 0.0f + _y1 * 1.0f;
                g_y[ln][col] = dIdY;
                //cálculo da magnitude do gradiente.
                float magDxy = (float) Math.sqrt( dIdY *dIdY + dIdX* dIdX);
                mag[ln][col] = magDxy;
                //A direção do gradiente
                float tetha = (float) Math.atan2(dIdY, dIdX);
                direction[ln][col] = tetha;
            }
        }
    }
    public float[][] getG_x() {
        return g_x;
    }

    public float[][] getG_y() {
        return g_y;
    }

    public float[][] getData() {
        return data;
    }

    public float[][] getDirection() {
        return direction;
    }

    public float[][] getMag() {
        return mag;
    }
}
