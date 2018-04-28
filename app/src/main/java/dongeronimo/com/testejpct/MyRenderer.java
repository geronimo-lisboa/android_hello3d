package dongeronimo.com.testejpct;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
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

    private Context context;

    private Terrain terrain;

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
            //cria a textura
            //texture = new Texture(rescale(convert(context.getResources().getDrawable(R.drawable.imagem)), 256,256));
            //TextureManager.getInstance().addTexture("texture", texture);
            //criação do terreno
            Object3D ground = Terrain.Generate(context.getResources().getDrawable(R.drawable.imagem));
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
        // Draw the main screen
        fb.clear(RGBColor.GREEN);
        world.renderScene(fb);
        world.draw(fb);
        fb.display();

    }
}
