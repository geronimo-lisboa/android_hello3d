package dongeronimo.com.testejpct;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.DebugUtils;
import android.util.Log;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import dongeronimo.com.testejpct.model.Mundo;

/**
 * Implementação do renderer. É usado pela HelloWorld (no glView que tem lá)*/
public class MyRenderer implements GLSurfaceView.Renderer {
    private GL10 lastGl = null;
    private FrameBuffer fb = null;
    private Mundo mundo;
    //private World world = null;
    //private Light sun = null;
    //Além de guardar a câmera do world serve pra encapsular um monte de operações e deve ser o meio
    //preferido de operar com a câmera.
    private CameraHelper cameraHelper;
    private Context context;
    //private Terrain terrain;
    public void setTouchTurn(float v){
        cameraHelper.addHorizontalRotation(v);
    }
    public void setTouchTurnUp(float v){
        cameraHelper.addVerticalRotation(v);
    }

    public MyRenderer(Context ctx){
        this.context = ctx;
    }

    public Context getContext(){
        return context;
    }

    public void setMundo(Mundo m){
        this.mundo = m;
    }

    public void createCamera(){
        //Cria a câmera
        cameraHelper = new CameraHelper(mundo.getWorldObject().getCamera(),
                new SimpleVector(0,0,0),
                new SimpleVector(0,50,-50),
                0, 0);
        cameraHelper.addShaderDataListener(new CameraHelper.ShaderDataListerner() {
            @Override
            public void apply(CameraHelper cameraHelper) {
                Log.d("Simcity", "Nao implementado");
                //Passa a posição da câmera pro shader
                mundo.setCameraPosition(cameraHelper.getCameraPosition());
                //terrain.setCameraPosition(cameraHelper.getCameraPosition());
            }
        });
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
        }
        else{//Não precisa recriar o framebuffer, só redimensionar
            fb.resize(w,h);
            fb.setVirtualDimensions(w,h);
        }
        //criação do mundo se ele não tiver sido criado
//        if(world==null){
//            world = new World();
//            world.setAmbientLight(20,20,20);
//            sun = new Light(world);
//            sun.setIntensity(250,250,250);
//
//            ///Pega o bitmap do terreno e gera os dados pro heightmap
//            //Isso aqui é necessário para que o android não escale meu bitmap com o tamanho de tela quando
//            //carregá-lo (comportamento padrão), uma vez que eu preciso do bitmap como ele é.
//            BitmapFactory.Options heightmapLoadOption = new BitmapFactory.Options();
//            heightmapLoadOption.inScaled = false;
//            //A carga do bitmap propriamente dita é aqui,
//            Bitmap heightmapBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapa_100, heightmapLoadOption);
//            //Criação do terreno
//            terrain = new Terrain(heightmapBmp, context);
//            world.addObject(terrain.getSurface());
//            //Cria a câmera
//            cameraHelper = new CameraHelper(world.getCamera(),  new SimpleVector(0,0,0),new SimpleVector(0,50,-50), 0, 0);
//            cameraHelper.addShaderDataListener(new CameraHelper.ShaderDataListerner() {
//                @Override
//                public void apply(CameraHelper cameraHelper) {
//                    //Passa a posição da câmera pro shader
//                    terrain.setCameraPosition(cameraHelper.getCameraPosition());
//                }
//            });
//            //Seta a posição do sol
//            SimpleVector sv = new SimpleVector();
//            sv.set(SimpleVector.ORIGIN);
//            sv.x += 0;
//            sv.y += 500;
//            sv.z += 0;
//            sun.setPosition(sv);
//            MemoryHelper.compact();
//        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }
    private int fps = 0;
    private long time = System.currentTimeMillis();
    float xR = 0;
    @Override
    public void onDrawFrame(GL10 gl10) {
        //Atualiza a câmera
//        cameraHelper.update();

        // Draw the main screen
        fb.clear(RGBColor.GREEN);
        cameraHelper.update();
        mundo.render(fb);
//        world.renderScene(fb);
//        world.draw(fb);
        fb.display();

        if (System.currentTimeMillis() - time >= 1000) {
            Log.d("SimCity_FPS", ""+fps);
            fps = 0;
            time = System.currentTimeMillis();
        }
        fps++;
    }

    public void incrementCameraZ() {
//        cameraHelper.moveFoward();
    }

    public void decrementCameraZ() {
//        cameraHelper.moveBackward();
    }
    public void decrementCameraX() {
//        cameraHelper.moveLeft();
    }

    public void incrementCameraX() {
//        cameraHelper.moveRight();
    }

    public void resetCamera() {
//        cameraHelper.reset();
    }
}
