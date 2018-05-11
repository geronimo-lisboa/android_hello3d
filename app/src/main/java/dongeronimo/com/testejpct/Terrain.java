package dongeronimo.com.testejpct;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * O terreno gerado a partir de um heightmap. A partir de um bitmap passado pro construtor constrói
 * o terreno. O bitmap deve ser em tons de cinza porque a cor do componente de menor precedência do pixel
 * será usada pra gerar as altitudes do mapa.
 * O objeto 3d resultante do algoritmo de heightmap está disponivel no método getSurface().
 * Uma vez criado o mapa ele é imutável.*/
public class Terrain {
    private SimpleVector terrainDiffuse = new SimpleVector(0.5, 0.4, 0.001);

    private Context context;
    private boolean alredyBuilt = false;
    private Bitmap heightmapBitmap;
    private float heightValues[][];
    private Object3D superficie;
    private GLSLShader shader;
    private GradientCalculator gradientCalculator;

    public void setTestLightPos(SimpleVector v){
        shader.setUniform("testLightPos", v);
    }

    public void setDiffuse(SimpleVector rgb){
        shader.setUniform("diffuse", rgb);
    }

    private void buildTerrain(){
        //Construção da geometria
        final int bmpLargura = heightmapBitmap.getWidth();
        final int bmpAltura = heightmapBitmap.getHeight();
        final int numeroDeFaces = (bmpLargura-1) * (bmpAltura-1);
        final int numeroDeTriangulos = numeroDeFaces * 2;

        //calculo do centro do mundo
        final float worldOffsetX =  bmpLargura/2 * -1.0f;
        final float worldOffsetZ =  bmpAltura/2 * -1.0f;
        long tt = 0;
        long t0 = System.currentTimeMillis();

        HashMap<Pair<Integer, Integer>, SimpleVector> coordinates = new HashMap<>();//Uma associação entre as coordenadas e suas posições no heigtmap

        //percorre o heightmap criando as faces.
        for(int y=0; y<bmpAltura-1; y++) {
            for (int x = 0; x < bmpLargura - 1; x++) {
                //Triangulo 01
                SimpleVector ponto01 = new SimpleVector(x + worldOffsetX, heightValues[x][y], y + worldOffsetZ);
                if ( coordinates.get(new Pair<>(x,y)) == null) coordinates.put(new Pair<>(x,y), ponto01);
                SimpleVector ponto02 = new SimpleVector(x + worldOffsetX, heightValues[x][y + 1], y + 1 + worldOffsetZ);
                if ( coordinates.get(new Pair<>(x,y+1)) == null) coordinates.put(new Pair<>(x,y+1), ponto02);
                SimpleVector ponto03 = new SimpleVector(x + 1 + worldOffsetX, heightValues[x + 1][y], y + worldOffsetZ);
                if ( coordinates.get(new Pair<>(x+1,y)) == null) coordinates.put(new Pair<>(x+1,y), ponto03);
                //Triangulo 02
                SimpleVector ponto04 = new SimpleVector(x + worldOffsetX, heightValues[x][y + 1], y + 1 + worldOffsetZ);
                if ( coordinates.get(new Pair<>(x,y+1)) == null) coordinates.put(new Pair<>(x,y+1), ponto04);
                SimpleVector ponto05 = new SimpleVector(x + 1 + worldOffsetX, heightValues[x + 1][y + 1], y + 1 + worldOffsetZ);
                if ( coordinates.get(new Pair<>(x+1,y+1)) == null)coordinates.put(new Pair<>(x+1,y+1), ponto05);
                SimpleVector ponto06 = new SimpleVector(x + 1 + worldOffsetX, heightValues[x + 1][y], y + worldOffsetZ);
                if ( coordinates.get(new Pair<>(x+1,y)) == null)coordinates.put(new Pair<>(x+1,y), ponto06);
            }
        }//Com isso a lista de coordenadas está populada
        //Cálculo das normais
        List<SimpleVector> normals = new ArrayList<>();
        for(int y=0; y<bmpAltura; y++) {
            for (int x = 0; x < bmpLargura ; x++) {
                final Pair<Integer, Integer> currentPos = new Pair<>(x,y);
                final Pair<Integer, Integer> north = new Pair<>(x, y - 1);
                final Pair<Integer, Integer> south = new Pair<>(x, y + 1);
                final Pair<Integer, Integer> east = new Pair<>( x - 1, y);
                final Pair<Integer, Integer> west = new Pair<>( x+1,y);
                //lembrando de tratar os casos especiais das bordas - mesma altitude mas afastando na direção apropriada.
                SimpleVector cCurrent = coordinates.get(currentPos);
                SimpleVector cNorth = coordinates.get(north);
                if(cNorth == null)
                    cNorth = cCurrent.calcAdd(new SimpleVector(0,0,-1));
                SimpleVector cSouth = coordinates.get(south);
                if(cSouth == null)
                    cSouth = cCurrent.calcAdd(new SimpleVector(0,0,1));
                SimpleVector cEast = coordinates.get(east);
                if(cEast == null);
                    cEast = cCurrent.calcAdd(new SimpleVector(-1, 0,0));
                SimpleVector cWest = coordinates.get(west);
                if(cWest == null)
                    cWest = cCurrent.calcAdd(new SimpleVector(1, 0, 0));
                //Agora eu sei os vetores ao redor do vetor atual. A normal do vetor atual será o produto vetorial
                //do vetor norte-sul e do vetor leste-oeste
                final SimpleVector V = cNorth.calcSub(cSouth);
                final SimpleVector U = cWest.calcSub(cEast);
                final SimpleVector cross = V.calcCross(U);
                normals.add(cross);
            }
        }
        int sz0 = coordinates.size();
        int sz1 = normals.size();

        //Agora cria o objeto 3d com esses dados
        superficie = new Object3D(1); //(coordinates, normals, uvs, indices, TextureManager.TEXTURE_NOTFOUND);

        long t1 = System.currentTimeMillis();
        Log.d("TEMPO_PASSAGEM_GEO", (t1-t0)+" ms");
        tt = tt + (t1-t0);
        t0 = System.currentTimeMillis();
        //superficie.cullingIsInverted();
        superficie.calcBoundingBox();
        superficie.calcCenter();
        superficie.strip();
        //superficie.build();
        t1 = System.currentTimeMillis();
        Log.d("TEMPO_BUILD_DA_API", (t1-t0)+" ms");//
        tt = tt + (t1-t0);
        Log.d("TEMPO_TOTAL",(t1-t0)+" ms");
        //Construção do shader
        //Setagem do shader na superficie.
        String vertexShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_vertex_shader));
        String fragShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_fragment_shader));
        shader = new GLSLShader(vertexShaderSrc, fragShaderSrc);
        Matrix tTrans = superficie.getTranslationMatrix();
        Matrix tRot = superficie.getRotationMatrix();
        tRot.matMul(tTrans);
        tRot = tRot.transpose();
        shader.setUniform("modelMatrix", tRot);

        superficie.setShader(shader);
        setDiffuse(terrainDiffuse);
        //tá pronto
        alredyBuilt = true;
    }

    public void setCameraPosition(SimpleVector cp){
        shader.setUniform("cameraPosition", cp);
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
                heightValues[ln][col] = (color*1.0f) * 0.1f;
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