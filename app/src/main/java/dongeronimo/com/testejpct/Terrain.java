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
import com.threed.jpct.TextureManager;

import java.util.ArrayList;
import java.util.List;

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

        float coords[] = new float[numeroDeTriangulos * 9];
        float norms[] = new float[numeroDeTriangulos * 9];
        int indexes[] = new int[numeroDeTriangulos * 3];
        float uvs[] = new float[numeroDeTriangulos * 6];
        List<Pair<Integer, Integer>> xyMap = new ArrayList<>();
        for(int i=0; i<uvs.length;i++)
            uvs[i] = 0.0f;
        int coordIndex = 0;
        int pointId = 0;
        for(int y=0; y<bmpAltura-1; y++){
            for(int x=0; x<bmpLargura-1; x++){
                ////Triangulo 01
                SimpleVector ponto01 = new SimpleVector(x + worldOffsetX,heightValues[x][y], y+worldOffsetZ );
                coords[coordIndex + 0] = ponto01.x;
                coords[coordIndex + 1] = ponto01.y;
                coords[coordIndex + 2] = ponto01.z;
                norms[coordIndex + 0 ] = 0;
                norms[coordIndex + 1 ] = 1;
                norms[coordIndex + 2 ] = 0;
                indexes[pointId] = pointId;
                pointId++;
                coordIndex = coordIndex + 3;
                xyMap.add(new Pair<Integer, Integer>(x,y));

                SimpleVector ponto02 = new SimpleVector(x+ worldOffsetX,heightValues[x][y+1], y+1+worldOffsetZ);
                coords[coordIndex + 0] = ponto02.x;
                coords[coordIndex + 1] = ponto02.y;
                coords[coordIndex + 2] = ponto02.z;
                norms[coordIndex + 0 ] = 0;
                norms[coordIndex + 1 ] = 1;
                norms[coordIndex + 2 ] = 0;
                indexes[pointId] = pointId;
                pointId++;
                coordIndex = coordIndex + 3;
                xyMap.add(new Pair<Integer, Integer>(x,y+1));

                SimpleVector ponto03 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y], y +worldOffsetZ);
                coords[coordIndex + 0] = ponto03.x;
                coords[coordIndex + 1] = ponto03.y;
                coords[coordIndex + 2] = ponto03.z;
                norms[coordIndex + 0 ] = 0;
                norms[coordIndex + 1 ] = 1;
                norms[coordIndex + 2 ] = 0;
                indexes[pointId] = pointId;
                pointId++;
                coordIndex = coordIndex + 3;
                xyMap.add(new Pair<Integer, Integer>(x+1,y));

                //Triangulo 02
                SimpleVector ponto04 = new SimpleVector(x+ worldOffsetX,heightValues[x][y+1], y+1+worldOffsetZ);
                coords[coordIndex + 0] = ponto04.x;
                coords[coordIndex + 1] = ponto04.y;
                coords[coordIndex + 2] = ponto04.z;
                norms[coordIndex + 0 ] = 0;
                norms[coordIndex + 1 ] = 1;
                norms[coordIndex + 2 ] = 0;
                indexes[pointId] = pointId;
                pointId++;
                coordIndex = coordIndex + 3;
                xyMap.add(new Pair<Integer, Integer>(x,y+1));

                SimpleVector ponto05 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y+1], y+1 +worldOffsetZ);
                coords[coordIndex + 0] = ponto05.x;
                coords[coordIndex + 1] = ponto05.y;
                coords[coordIndex + 2] = ponto05.z;
                norms[coordIndex + 0 ] = 0;
                norms[coordIndex + 1 ] = 1;
                norms[coordIndex + 2 ] = 0;
                indexes[pointId] = pointId;
                pointId++;
                coordIndex = coordIndex + 3;
                xyMap.add(new Pair<Integer, Integer>(x+1,y+1));

                SimpleVector ponto06 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y], y+worldOffsetZ);
                coords[coordIndex + 0] = ponto06.x;
                coords[coordIndex + 1] = ponto06.y;
                coords[coordIndex + 2] = ponto06.z;
                norms[coordIndex + 0 ] = 0;
                norms[coordIndex + 1 ] = 1;
                norms[coordIndex + 2 ] = 0;
                indexes[pointId] = pointId;
                pointId++;
                coordIndex = coordIndex + 3;
                xyMap.add(new Pair<Integer, Integer>(x+1,y));
            }
        }

        for(int i=0; i<xyMap.size(); i++){
            Pair<Integer, Integer> currentXY = xyMap.get(i);
            Pair<Integer, Integer> north = new Pair<>(currentXY.first, currentXY.second-1);
            Pair<Integer, Integer> south = new Pair<>(currentXY.first, currentXY.second+1);
            Pair<Integer, Integer> east = new Pair<>(currentXY.first-1, currentXY.second);
            Pair<Integer, Integer> west = new Pair<>(currentXY.first+1, currentXY.second);

            //Pega as coordenadas
            SimpleVector pNorth, pSouth, pEast, pWest;
            try {
                int idNorth = xyMap.indexOf(north);
                pNorth = idNorth == -1 ? new SimpleVector(coords[i * 3 + 0], coords[i * 3 + 1], coords[i * 3 + 2] - 1) :
                        new SimpleVector(coords[idNorth * 3 + 0], coords[idNorth * 3 + 1], coords[idNorth * 3 + 2]);
            }catch (ArrayIndexOutOfBoundsException ex){
                Log.e("erro", currentXY.toString());
                throw ex;
            }
            try {
                int idSouth = xyMap.indexOf(south);
                pSouth = idSouth == -1 ? new SimpleVector(coords[i * 3 + 0], coords[i * 3 + 1], coords[i * 3 + 2] + 1) :
                        new SimpleVector(coords[idSouth * 3 + 0], coords[idSouth * 3 + 1], coords[idSouth * 3 + 2]);
            }catch (ArrayIndexOutOfBoundsException ex){
                Log.e("erro", currentXY.toString());
                throw ex;
            }
            try {
                int idEast = xyMap.indexOf(east);
                pEast = idEast == -1 ? new SimpleVector(coords[i * 3 + 0] - 1, coords[i * 3 + 1], coords[i * 3 + 2]) :
                        new SimpleVector(coords[idEast * 3 + 0], coords[idEast * 3 + 1], coords[idEast * 3 + 2]);
            }catch (ArrayIndexOutOfBoundsException ex){
                Log.e("erro", currentXY.toString());
                throw ex;
            }
            try {
                int idWest = xyMap.indexOf(west);
                pWest = idWest == -1 ? new SimpleVector(coords[i * 3 + 0] + 1, coords[i * 3 + 1], coords[i * 3 + 2]) :
                        new SimpleVector(coords[idWest * 3 + 0], coords[idWest * 3 + 1], coords[idWest * 3 + 2]);
            }catch (ArrayIndexOutOfBoundsException ex){
                Log.e("erro", currentXY.toString());
                throw ex;
            }
            //faz as contas
            SimpleVector U = pWest.calcSub(pEast);
            U.normalize();
            SimpleVector V = pSouth.calcSub(pNorth);
            V.normalize();
            SimpleVector newNormal = V.calcCross(U);
            //...
            //altera a matriz de normais
            norms[i*3 + 0] = newNormal.x;
            norms[i*3 + 1] = newNormal.y;
            norms[i*3 + 2] = newNormal.z;
        }
        Log.d("simcity","passou?");

//        //percorre o heightmap criando as faces.
//        for(int y=0; y<bmpAltura-1; y++){
//            for(int x=0; x<bmpLargura-1; x++){
//                //Triangulo 01
//                SimpleVector ponto01 = new SimpleVector(x + worldOffsetX,heightValues[x][y], y+worldOffsetZ );
//                SimpleVector ponto02 = new SimpleVector(x+ worldOffsetX,heightValues[x][y+1], y+1+worldOffsetZ);
//                SimpleVector ponto03 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y], y +worldOffsetZ);
//                //Triangulo 02
//                SimpleVector ponto04 = new SimpleVector(x+ worldOffsetX,heightValues[x][y+1], y+1+worldOffsetZ);
//                SimpleVector ponto05 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y+1], y+1 +worldOffsetZ);
//                SimpleVector ponto06 = new SimpleVector(x+1+ worldOffsetX,heightValues[x+1][y], y+worldOffsetZ);
//                //Adiciona à superficie
//                superficie.addTriangle(ponto01,0,0, ponto02,0,1, ponto03,1,0);
//                superficie.addTriangle(ponto04,0,1, ponto05,1,1, ponto06,1,0);
//            }
//        }
        superficie = new Object3D(coords,norms,uvs,indexes, TextureManager.TEXTURE_NOTFOUND);
        long t1 = System.currentTimeMillis();
        Log.d("TEMPO_PASSAGEM_GEO", (t1-t0)+" ms");
        tt = tt + (t1-t0);
        t0 = System.currentTimeMillis();
        superficie.setCulling(true);
        superficie.calcBoundingBox();
        superficie.calcCenter();
        superficie.strip();
        superficie.touch();
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
        shader.setUniform("modelMatrix", tRot);
        superficie.setShader(shader);
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


    public void setTestLightPos(SimpleVector testLightPos) {
        shader.setUniform("testLightPos", testLightPos);
    }
}