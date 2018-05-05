package dongeronimo.com.testejpct;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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
    private GradientCalculator gradientCalculator;

    private void buildTerrain(){
        final long t0 = System.currentTimeMillis();
        //Construção da geometria
        final int bmpLargura = heightmapBitmap.getWidth();
        final int bmpAltura = heightmapBitmap.getHeight();
        final int numeroDeFaces = (bmpLargura-1) * (bmpAltura-1);
        final int numeroDeTriangulos = numeroDeFaces * 2;
        //superficie = new Object3D(numeroDeTriangulos);
        //calculo do centro do mundo
        final float worldOffsetX =  bmpLargura/2 * -1.0f;
        final float worldOffsetZ =  bmpAltura/2 * -1.0f;
        //As listas guardarão os dados que depois serão passados pra jpct.
        float coords[] = new  float[numeroDeTriangulos*9];
        float uvs[] = new float[numeroDeTriangulos*6];
        int indexes[] = new int[numeroDeTriangulos*3];

        int currentCoord = 0;
        int currentUv = 0;
        int currentIndex = 0;
        //percorre o heightmap criando as faces.
        for(int y=0; y<bmpAltura-1; y++){
            for(int x=0; x<bmpLargura-1; x++){
                //Triangulo 01
                SimpleVector ponto01 = new SimpleVector(x + worldOffsetX,heightValues[x][y], y+worldOffsetZ );
                SimpleVector ponto02 = new SimpleVector(x+ worldOffsetX,heightValues[x][y+1], y+1+worldOffsetZ);
                SimpleVector ponto03 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y], y +worldOffsetZ);
                //Triangulo 02
                SimpleVector ponto04 = new SimpleVector(x+ worldOffsetX,heightValues[x][y+1], y+1+worldOffsetZ);
                SimpleVector ponto05 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y+1], y+1 +worldOffsetZ);
                SimpleVector ponto06 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y], y+worldOffsetZ);
                ////Adiciona às listas.
                //vertice 1
                coords[currentCoord + 0] = ponto01.x;
                coords[currentCoord + 1] = ponto01.y;
                coords[currentCoord + 2] = ponto01.z;
                uvs[currentUv + 0 ]=0;
                uvs[currentUv + 1 ]=0;
                indexes[currentIndex]=currentIndex;
                //avança os contadores
                currentCoord = currentCoord+3;
                currentUv = currentUv+2;
                currentIndex = currentIndex+1;
                //Vertice 2
                coords[currentCoord + 0] = ponto02.x;
                coords[currentCoord + 1] = ponto02.y;
                coords[currentCoord + 2] = ponto02.z;
                uvs[currentUv + 0 ]=0;
                uvs[currentUv + 1 ]=1;
                indexes[currentIndex]=currentIndex;
                //avança os contadores
                currentCoord = currentCoord+3;
                currentUv = currentUv+2;
                currentIndex = currentIndex+1;
                //Vertice 3
                coords[currentCoord + 0] = ponto03.x;
                coords[currentCoord + 1] = ponto03.y;
                coords[currentCoord + 2] = ponto03.z;
                uvs[currentUv + 0 ]=1;
                uvs[currentUv + 1 ]=0;
                indexes[currentIndex]=currentIndex;
                //avança os contadores
                currentCoord = currentCoord+3;
                currentUv = currentUv+2;
                currentIndex = currentIndex+1;
                //Vertice 4
                coords[currentCoord + 0] = ponto04.x;
                coords[currentCoord + 1] = ponto04.y;
                coords[currentCoord + 2] = ponto04.z;
                uvs[currentUv + 0 ]=0;
                uvs[currentUv + 1 ]=1;
                indexes[currentIndex]=currentIndex;
                //avança os contadores
                currentCoord = currentCoord+3;
                currentUv = currentUv+2;
                currentIndex = currentIndex+1;
                //Vertice 5
                coords[currentCoord + 0] = ponto05.x;
                coords[currentCoord + 1] = ponto05.y;
                coords[currentCoord + 2] = ponto05.z;
                uvs[currentUv + 0 ]=1;
                uvs[currentUv + 1 ]=1;
                indexes[currentIndex]=currentIndex;
                //avança os contadores
                currentCoord = currentCoord+3;
                currentUv = currentUv+2;
                currentIndex = currentIndex+1;
                //Vertice 6
                coords[currentCoord + 0] = ponto06.x;
                coords[currentCoord + 1] = ponto06.y;
                coords[currentCoord + 2] = ponto06.z;
                uvs[currentUv + 0 ]=1;
                uvs[currentUv + 1 ]=0;
                indexes[currentIndex]=currentIndex;
                //avança os contadores
                currentCoord = currentCoord+3;
                currentUv = currentUv+2;
                currentIndex = currentIndex+1;
            }
        }
        //passa as listas pro objeto
        superficie = new Object3D(coords, uvs, indexes, 0);
        superficie.setCulling(false);
        superficie.calcBoundingBox();
        superficie.calcCenter();
        superficie.strip();
        superficie.compile(false, true);
        superficie.build();

        //Construção do shader
        //Setagem do shader na superficie.
        String vertexShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_vertex_shader));
        String fragShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_fragment_shader));
        shader = new GLSLShader(vertexShaderSrc, fragShaderSrc);
        superficie.setShader(shader);
        //tá pronto

        alredyBuilt = true;
        final long t = System.currentTimeMillis();
        Log.d("TEMPO_GASTO", ""+(t - t0));

    }

    public Terrain(Bitmap heightmapBmp, Context ctx){
        this.context = ctx;
        //Passagem dos dados do bitmap pra matrix do heightmap.
        heightmapBitmap = heightmapBmp;
        final int bmpLargura = heightmapBmp.getWidth();
        final int bmpAltura = heightmapBmp.getHeight();
        heightValues = new float[bmpAltura][bmpAltura];
        for(int ln = 0; ln <bmpAltura; ln++){
            for (int col=0; col<bmpLargura; col++){
                //Pega o dado de cor e extrai o 1o componente (só preciso de 1, já que o mapa é cinza.
                final int colorRawData = heightmapBmp.getPixel(col, ln);
                final int mask = 0b00000000_00000000_00000000_11111111;
                int color = colorRawData & mask;
                //O nivel do mar é hardcoded no momento pra 10 e um fator de escala p 0.5
                heightValues[ln][col] = (color*1.0f) * 0.025f;
            }
        }
        //Ao final disso eu tenho um array de floats com o heightmap, posso já começar a usar pra montar a superficie
        gradientCalculator = new GradientCalculator(heightValues);
    }

    public Object3D getSurface(){
        if(!alredyBuilt)
        {
            buildTerrain();
        }
        return superficie;
    }


}