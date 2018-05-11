package dongeronimo.com.testejpct;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureManager;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * O terreno gerado a partir de um heightmap. A partir de um bitmap passado pro construtor constrói
 * o terreno. O bitmap deve ser em tons de cinza porque a cor do componente de menor precedência do pixel
 * será usada pra gerar as altitudes do mapa.
 * O objeto 3d resultante do algoritmo de heightmap está disponivel no método getSurface().
 * Uma vez criado o mapa ele é imutável.
 */
public class Terrain {
    private SimpleVector terrainDiffuse = new SimpleVector(0.5, 0.4, 0.001);

    private Context context;
    private boolean alredyBuilt = false;
    private Bitmap heightmapBitmap;
    private float heightValues[][];
    private Object3D superficie;
    private GLSLShader shader;
    private GradientCalculator gradientCalculator;

    public void setTestLightPos(SimpleVector v) {
        shader.setUniform("testLightPos", v);
    }

    public void setDiffuse(SimpleVector rgb) {
        shader.setUniform("diffuse", rgb);
    }

    private void buildTerrain() {
        //Construção da geometria
        final int bmpLargura = heightmapBitmap.getWidth();
        final int bmpAltura = heightmapBitmap.getHeight();
        final int numeroDeFaces = (bmpLargura - 1) * (bmpAltura - 1);
        final int numeroDeTriangulos = numeroDeFaces * 2;

        //calculo do centro do mundo
        final float worldOffsetX = bmpLargura / 2 * -1.0f;
        final float worldOffsetZ = bmpAltura / 2 * -1.0f;
        long tt = 0;
        long t0 = System.currentTimeMillis();
        class PointStruct implements Comparable<PointStruct>{
            public final int index;
            public final SimpleVector coord;
            public PointStruct(final int i, final SimpleVector v){
                index = i;
                coord = v;
            }
            public boolean equals(Object o){
                if (!(o instanceof PointStruct))return  false;
                else{
                    PointStruct _o = (PointStruct)o;
                    return (this.index==_o.index && this.coord == _o.coord);
                }
            }
            public int hashCode(){
                return Objects.hash(index, coord);
            }

            @Override
            public int compareTo(@NonNull PointStruct pointStruct) {
                return ((Integer)index).compareTo(pointStruct.index);
            }
        }
        HashMap<Pair<Integer, Integer>, PointStruct> coordinates = new HashMap<>();//Uma associação entre as coordenadas e suas posições no heigtmap
        int indexCount = 0;
        List<Integer> lstIndexes = new ArrayList<>();
        //percorre o heightmap criando as faces.
        for (int y = 0; y < bmpAltura - 1; y++) {
            for (int x = 0; x < bmpLargura - 1; x++) {
                //Triangulo 01
                SimpleVector ponto01 = new SimpleVector(x + worldOffsetX, heightValues[x][y], y + worldOffsetZ);
                //if (coordinates.get(new Pair<>(x, y)) == null) {
                    coordinates.put(new Pair<>(x, y), new PointStruct(indexCount, ponto01));
                    lstIndexes.add(indexCount);
                    indexCount++;
                //}
                SimpleVector ponto02 = new SimpleVector(x + worldOffsetX, heightValues[x][y + 1], y + 1 + worldOffsetZ);
                //if (coordinates.get(new Pair<>(x, y + 1)) == null) {
                    coordinates.put(new Pair<>(x, y + 1), new PointStruct(indexCount, ponto02));
                    lstIndexes.add(indexCount);
                    indexCount++;
                //}
                SimpleVector ponto03 = new SimpleVector(x + 1 + worldOffsetX, heightValues[x + 1][y], y + worldOffsetZ);
                //if (coordinates.get(new Pair<>(x + 1, y)) == null) {
                    coordinates.put(new Pair<>(x + 1, y), new PointStruct(indexCount, ponto03));
                    lstIndexes.add(indexCount);
                    indexCount++;
                //}
                //Triangulo 02
                SimpleVector ponto04 = new SimpleVector(x + worldOffsetX, heightValues[x][y + 1], y + 1 + worldOffsetZ);
                //if (coordinates.get(new Pair<>(x, y + 1)) == null) {
                    coordinates.put(new Pair<>(x, y + 1), new PointStruct(indexCount, ponto04));
                    lstIndexes.add(indexCount);
                    indexCount++;
                //}
                SimpleVector ponto05 = new SimpleVector(x + 1 + worldOffsetX, heightValues[x + 1][y + 1], y + 1 + worldOffsetZ);
                //if (coordinates.get(new Pair<>(x + 1, y + 1)) == null) {
                    coordinates.put(new Pair<>(x + 1, y + 1), new PointStruct(indexCount, ponto05));
                    lstIndexes.add(indexCount);
                    indexCount++;
                //}
                SimpleVector ponto06 = new SimpleVector(x + 1 + worldOffsetX, heightValues[x + 1][y], y + worldOffsetZ);
                //if (coordinates.get(new Pair<>(x + 1, y)) == null) {
                    coordinates.put(new Pair<>(x + 1, y), new PointStruct(indexCount, ponto06));
                    lstIndexes.add(indexCount);
                    indexCount++;
                //}
            }
        }
        HashMap<Pair<Integer, Integer>, PointStruct> normals2 = new HashMap<>();
        for (Map.Entry<Pair<Integer, Integer>, PointStruct> current : coordinates.entrySet()) {
            //Para cada coordenada calcular sua normal
            final Pair<Integer, Integer> currentPos = current.getKey();
            final PointStruct currentValue = current.getValue();
            final int x = currentPos.first;
            final int y = currentPos.second;
            final Pair<Integer, Integer> north = new Pair<>(x, y - 1);
            final Pair<Integer, Integer> south = new Pair<>(x, y + 1);
            final Pair<Integer, Integer> east = new Pair<>(x - 1, y);
            final Pair<Integer, Integer> west = new Pair<>(x + 1, y);
            //agora que eu tenho os pares pras quatro posições, pego-os na lista de coordenadas
            SimpleVector cNorth = coordinates.get(north)!=null ? coordinates.get(north).coord : currentValue.coord.calcAdd(new SimpleVector(0, 0, -1));
            SimpleVector cSouth = coordinates.get(south)!=null ? coordinates.get(south).coord : currentValue.coord.calcAdd(new SimpleVector(0, 0, 1));
            SimpleVector cEast =  coordinates.get(east)!=null ? coordinates.get(east).coord : currentValue.coord.calcAdd(new SimpleVector(-1, 0, 0));
            SimpleVector cWest = coordinates.get(west)!=null ? coordinates.get(west).coord : currentValue.coord.calcAdd(new SimpleVector(1, 0, 0));
            //Calculo o produto vetorial
            SimpleVector V = cNorth.calcSub(cSouth);
            V.normalize();
            SimpleVector U = cWest.calcSub(cEast);
            U.normalize();
            SimpleVector cross = V.calcCross(U);
            cross.normalize();
            //guardo na lista de normais.
            normals2.put(new Pair<>(x, y), new PointStruct(currentValue.index, cross));
        }
        Assert.assertEquals("normais e component tem que ter o mesmo tamanho", normals2.size(), coordinates.size());
        //Hora de preparar os dados pra JPCT. Os dados são ordenáveis agora, isso é importante pq a jpct usa opengl e opengl
        //se importa com ordem
        List<PointStruct> _lCoords = new ArrayList<>();
        _lCoords.addAll(coordinates.values());
        List<PointStruct> _lNormals = new ArrayList<>();
        _lNormals.addAll(normals2.values());
        //Garante a ordenação das coleções
        Collections.sort(_lCoords);
        Collections.sort(_lNormals);
        float coordBuffer[] = new float[_lCoords.size()*3];
        float normalBuffer[] = new float[_lNormals.size()*3];
        int _indexBuffer[] = new int[_lCoords.size()];
        //percorre as coleções enchendo os buffers com os dados nelas
        for(int i=0; i<_lCoords.size(); i++){
            PointStruct _c = _lCoords.get(i);
            PointStruct _n = _lNormals.get(i);
            coordBuffer[i * 3 + 0] = _c.coord.x;
            coordBuffer[i * 3 + 1] = _c.coord.y;
            coordBuffer[i * 3 + 2] = _c.coord.z;
            normalBuffer[i * 3 + 0] = _n.coord.x;
            normalBuffer[i * 3 + 1] = _n.coord.y;
            normalBuffer[i * 3 + 2] = _n.coord.z;
            _indexBuffer[i] = _c.index;
        }
        //cria o buffer de uvs, que são inuteis no momento.
        float uvBuffer[] = new float[_lCoords.size() * 2];
        for(int i=0; i<uvBuffer.length; i++){
            uvBuffer[i] = 0;
        }

        //Agora cria o objeto 3d com esses dados
        superficie = new Object3D(coordBuffer, normalBuffer, uvBuffer, _indexBuffer, TextureManager.TEXTURE_NOTFOUND);

        long t1 = System.currentTimeMillis();
        Log.d("TEMPO_PASSAGEM_GEO", (t1 - t0) + " ms");
        tt = tt + (t1 - t0);
        t0 = System.currentTimeMillis();
        //superficie.cullingIsInverted();
        superficie.calcBoundingBox();
        superficie.calcCenter();
        superficie.strip();
        //superficie.build();
        t1 = System.currentTimeMillis();
        Log.d("TEMPO_BUILD_DA_API", (t1 - t0) + " ms");//
        tt = tt + (t1 - t0);
        Log.d("TEMPO_TOTAL", (t1 - t0) + " ms");
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

    public void setCameraPosition(SimpleVector cp) {
        shader.setUniform("cameraPosition", cp);
    }

    public Terrain(Bitmap heightmapBmp, Context ctx) {
        this.context = ctx;
        //Passagem dos dados do bitmap pra matrix do heightmap.
        heightmapBitmap = heightmapBmp;
        final int bmpLargura = heightmapBmp.getWidth();
        final int bmpAltura = heightmapBmp.getHeight();
        heightValues = new float[bmpAltura][bmpAltura];
        for (int ln = 0; ln < bmpAltura; ln++) {
            for (int col = 0; col < bmpLargura; col++) {
                //Pega o dado de cor e extrai o 1o componente (só preciso de 1, já que o mapa é cinza.
                final int colorRawData = heightmapBmp.getPixel(col, ln);
                final int mask = 0b00000000_00000000_00000000_11111111;
                int color = colorRawData & mask;
                //O nivel do mar é hardcoded no momento pra 10 e um fator de escala p 0.5
                heightValues[ln][col] = (color * 1.0f) * 0.1f;
            }
        }
        //Ao final disso eu tenho um array de floats com o heightmap, posso já começar a usar pra montar a superficie
        gradientCalculator = new GradientCalculator(heightValues);
    }

    public Object3D getSurface() {
        if (!alredyBuilt) {
            buildTerrain();
        }
        return superficie;
    }


}