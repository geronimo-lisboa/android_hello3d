package dongeronimo.com.testejpct;

import android.content.Context;
import android.graphics.Bitmap;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
/**
 * O terreno gerado a partir de um heightmap. A partir de um bitmap passado pro construtor constrói
 * o terreno. O bitmap deve ser em tons de cinza porque a cor do componente de menor precedência do pixel
 * será usada pra gerar as altitudes do mapa.
 * O objeto 3d resultante do algoritmo de heightmap está disponivel no método getSurface().
 * Uma vez criado o mapa ele é imutável.*/
public class Terrain {
    private Context context;
    private boolean alredyBuilt = false;
    private Bitmap heightmapBitmap;
    private float heightValues[][];
    private Object3D superficie;
    private GLSLShader shader;

    private void buildTerrain(){
        //Construção da geometria
        final int bmpLargura = heightmapBitmap.getWidth();
        final int bmpAltura = heightmapBitmap.getHeight();
        final int numeroDeFaces = (bmpLargura-1) * (bmpAltura-1);
        final int numeroDeTriangulos = numeroDeFaces * 2;
        superficie = new Object3D(numeroDeTriangulos);
        //percorre o heightmap criando as faces.
        for(int y=0; y<bmpAltura-1; y++){
            for(int x=0; x<bmpLargura-1; x++){
                //Triangulo 01
                SimpleVector ponto01 = new SimpleVector(x,heightValues[x][y], y );
                SimpleVector ponto02 = new SimpleVector(x,heightValues[x][y+1], y+1);
                SimpleVector ponto03 = new SimpleVector(x+1,heightValues[x+1][y], y );
                //Triangulo 02
                SimpleVector ponto04 = new SimpleVector(x,heightValues[x][y+1], y+1);
                SimpleVector ponto05 = new SimpleVector(x+1,heightValues[x+1][y+1], y+1 );
                SimpleVector ponto06 = new SimpleVector(x+1,heightValues[x+1][y], y);
                //Adiciona à superficie
                superficie.addTriangle(ponto01,0,0, ponto02,0,1, ponto03,1,0);
                superficie.addTriangle(ponto04,0,1, ponto05,1,1, ponto06,1,0);
            }
        }
        superficie.setCulling(false);
        superficie.calcBoundingBox();
        superficie.calcCenter();
        superficie.build();
        superficie.strip();
        superficie.build();
        //Construção do shader
        //Setagem do shader na superficie.
        String vertexShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_vertex_shader));
        String fragShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_fragment_shader));
        shader = new GLSLShader(vertexShaderSrc, fragShaderSrc);
        superficie.setShader(shader);
        //tá pronto
        alredyBuilt = true;
    }

    public Terrain(Bitmap heightmapBmp, Context ctx){
        this.context = ctx;
        //Passagem dos dados do bitmap pra matrix do heightmap.
        heightmapBitmap = heightmapBmp;
        final int bmpLargura = heightmapBmp.getWidth();
        final int bmpAltura = heightmapBmp.getHeight();
        heightValues = new float[bmpAltura][bmpAltura];
        for(int y = 0; y<bmpAltura; y++){
            for (int x=0; x<bmpLargura; x++){
                //Pega o dado de cor e extrai o 1o componente (só preciso de 1, já que o mapa é cinza.
                final int colorRawData = heightmapBmp.getPixel(x, y);
                final int mask = 0b00000000_00000000_00000000_11111111;
                int color = colorRawData & mask;
                //O nivel do mar é hardcoded no momento pra 10 e um fator de escala p 0.5
                heightValues[x][y] = (color*1.0f) * 0.025f;
            }
        }
        //Ao final disso eu tenho um array de floats com o heightmap, posso já começar a usar pra montar a superficie
    }

    public Object3D getSurface(){
        if(!alredyBuilt)
        {
            buildTerrain();
        }
        return superficie;
    }


}