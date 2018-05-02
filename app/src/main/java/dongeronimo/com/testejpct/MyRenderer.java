package dongeronimo.com.testejpct;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
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

    private Camera cam;
    private Context context;
    private float touchTurn;
    private float touchTurnUp;
    private Terrain terrain;


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
            Bitmap heightmapBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.hm02, heightmapLoadOption);
            //Criação do terreno
            terrain = new Terrain(heightmapBmp, context);
            world.addObject(terrain.getSurface());
            //Cria a câmera
            cam  = world.getCamera();
            cam.setPosition(0, 20, -20);
            SimpleVector zero = new SimpleVector(0,0,0);
            cam.lookAt(zero);  //superficie.getCenter());
            //Seta a posição do sol
            SimpleVector sv = new SimpleVector();
            sv.set(SimpleVector.ORIGIN);
            sv.y += 500;
            sv.z -= 800;
            sun.setPosition(sv);
            MemoryHelper.compact();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //antigamente rodava o terreno, agora vai rodar a câmera, o rotaciona ao redor do eixo y,
        //o y ao redor do eixo x;
        if(touchTurn!=0){//rotação ao redor do y
            //A rotação está sendo na posição da câmera e não ao redor do foco dela como eu queria que fosse.
            SimpleVector yV = new SimpleVector(0,1,0);
            cam.rotateAxis(yV, touchTurn);
            touchTurn = 0;
        }
        if(touchTurnUp!=0){
//            SimpleVector xV = n
//            terrain.getSurface().rotateY(touchTurnUp);
//            touchTurnUp = 0;
        }

        // Draw the main screen
        fb.clear(RGBColor.GREEN);
        world.renderScene(fb);
        world.draw(fb);
        fb.display();

    }

    public void incrementCameraZ() {
        SimpleVector incVec = new SimpleVector(0,0,1.0);
        cam.moveCamera(incVec, 1);
    }

    public void decrementCameraZ() {
        SimpleVector incVec = new SimpleVector(0,0,-1.0);
        cam.moveCamera(incVec, 1);
    }

    public void decrementCameraX() {
        SimpleVector incVec = new SimpleVector(-1,0,0);
        cam.moveCamera(incVec, 1);
    }

    public void incrementCameraX() {
        SimpleVector incVec = new SimpleVector(1,0,0.0);
        cam.moveCamera(incVec, 1);
    }
}
