package dongeronimo.com.testejpct;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Implementação do renderer. É usado pela HelloWorld (no glView que tem lá)*/
public class MyRenderer implements GLSurfaceView.Renderer {
    private GL10 lastGl = null;
    private FrameBuffer fb = null;
    private World world = null;
    private Light sun = null;
    private Texture texture = null;
    private Object3D ground;
    private Context context;
    private float touchTurn;
    private float touchTurnUp;
    private Terrain terrain;
    private GLSLShader testeShader;

    public void setTouchTurn(float v){
        touchTurn = v;
    }
    public void setTouchTurnUp(float v){
        touchTurnUp = v;
    }

    public MyRenderer(Context ctx){
        context = ctx;
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        //Atualização/criação do framebuffer
        if(lastGl!=gl10){//Cria o framebuffer
            Log.i("Hello world", "init buffer");
            if(fb!=null){
                fb.dispose();
            }
            fb = new FrameBuffer(w, h);//Cria o framebuffer
            fb.setVirtualDimensions(fb.getWidth(), fb.getHeight());
            lastGl = gl10;
        }else{//Não precisa recriar o framebuffer, só redimensionar
            fb.resize(w,h);
            fb.setVirtualDimensions(w,h);
        }
        //criação do mundo se ele não tiver sido criado
        if(world==null){
            world = new World();
            world.setAmbientLight(20,20,20);
            sun = new Light(world);
            sun.setIntensity(250,250,250);

            ///Pega o bitmap do terreno e gera os dados pro heightmap
            //Isso aqui é necessário para que o android não escale meu bitmap com o tamanho de tela quando
            //carregá-lo (comportamento padrão), uma vez que eu preciso do bitmap como ele é.
            BitmapFactory.Options heightmapLoadOption = new BitmapFactory.Options();
            heightmapLoadOption.inScaled = false;
            //A carga do bitmap propriamente dita é aqui,
            Bitmap heightmapBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.terreno_teste_01, heightmapLoadOption);
            final int bmpLargura = heightmapBmp.getWidth();
            final int bmpAltura = heightmapBmp.getHeight();
            for(int y = 0; y<bmpAltura; y++){
                for (int x=0; x<bmpLargura; x++){
                    //Pega o dado de cor e extrai o 1o componente (só preciso de 1, já que o mapa é cinza.
                    final int colorRawData = heightmapBmp.getPixel(x, y);
                    final int mask = 0b00000000_00000000_00000000_11111111;
                    int color = colorRawData & mask;
                    Logger.log("cor = "+color);
                }
            }

            //cria a textura
            //texture = new Texture(rescale(convert(context.getResources().getDrawable(R.drawable.imagem)), 256,256));
            //TextureManager.getInstance().addTexture("texture", texture);
            //criação do terreno
            ground = Terrain.Generate(context.getResources().getDrawable(R.drawable.imagem));

            String vertexShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_vertex_shader));
            String fragShaderSrc = Loader.loadTextFile(context.getResources().openRawResource(R.raw.teste_fragment_shader));
            testeShader = new GLSLShader(vertexShaderSrc, fragShaderSrc);
            ground.setShader(testeShader);
             world.addObject(ground);
            Camera cam = world.getCamera();
            cam.moveCamera(Camera.CAMERA_MOVEOUT, 15);
            cam.lookAt(SimpleVector.ORIGIN);

            SimpleVector sv = new SimpleVector();
            sv.set(SimpleVector.ORIGIN);
            sv.y -= 100;
            sv.z -= 100;
            sun.setPosition(sv);
            MemoryHelper.compact();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if(touchTurn!=0){
            ground.rotateX(touchTurn);
            touchTurn = 0;
        }
        if(touchTurnUp!=0){
            ground.rotateY(touchTurnUp);
            touchTurnUp = 0;
        }

        // Draw the main screen
        fb.clear(RGBColor.GREEN);
        world.renderScene(fb);
        world.draw(fb);
        fb.display();

    }
}
