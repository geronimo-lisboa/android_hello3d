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

    private float touchTurnUp;
    private Terrain terrain;
    private SimpleVector cameraFocus;
    private SimpleVector cameraPosition;

    private float touchTurn = 0;

    public void setTouchTurn(float v){
        touchTurn += v;
    }
    public void setTouchTurnUp(float v){
        touchTurnUp += v;

    }

    public MyRenderer(Context ctx){
        this.context = ctx;
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
            Bitmap heightmapBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapa_100, heightmapLoadOption);
            //Criação do terreno
            terrain = new Terrain(heightmapBmp, context);
            world.addObject(terrain.getSurface());
            //Cria a câmera
            cam  = world.getCamera();
            cameraPosition = new SimpleVector(0,20,-20);
            cameraFocus = new SimpleVector(0,0,0);
            cam.setPosition(cameraPosition);
            cam.lookAt(cameraFocus);
            SimpleVector camDirection = cam.getDirection();

            //Agora o cálculo do azimute e elevação iniciais
            SimpleVector vecFromOrigin = cameraPosition.calcSub(cameraFocus);//O vetor olho-foco, no sist. de coordenadas da origem
            final float r = (float) Math.sqrt(vecFromOrigin.x * vecFromOrigin.x + vecFromOrigin.y*vecFromOrigin.y + vecFromOrigin.z*vecFromOrigin.z );

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
    float xR = 0;
    @Override
    public void onDrawFrame(GL10 gl10) {
        //Atualiza a câmera
        //Posiciona
        cam  = world.getCamera();
        cam.setPosition(cameraPosition);
        cam.lookAt(cameraFocus);  //superficie.getCenter());
        //Rotaciona ao redor do eixo
        if(touchTurn!=0){
            SimpleVector vecFromOrigin = cameraPosition.calcSub(cameraFocus);//O vetor olho-foco, no sist. de coordenadas da origem
            final float len = vecFromOrigin.length();
            cam.moveCamera(Camera.CAMERA_MOVEIN, len);
            SimpleVector vY = cam.getYAxis();
            cam.rotateAxis(vY, touchTurn);
            cam.moveCamera(Camera.CAMERA_MOVEOUT, len);
            Log.d("ANGULO_h", ""+Math.toDegrees(touchTurn));
        }
        if(touchTurnUp!=0){
            double angAsDeg =Math.toDegrees(touchTurnUp);
            if(angAsDeg <= -45)
                angAsDeg = -45;
            if(angAsDeg >= 45)
                angAsDeg = 45;
            final float angAsRad = (float)Math.toRadians(angAsDeg);
            touchTurnUp = angAsRad;
            SimpleVector vecFromOrigin = cameraPosition.calcSub(cameraFocus);//O vetor olho-foco, no sist. de coordenadas da origem
            final float len = vecFromOrigin.length();
            cam.moveCamera(Camera.CAMERA_MOVEIN, len);
            final SimpleVector vX = cam.getXAxis();
            cam.rotateAxis(vX, angAsRad);
            cam.moveCamera(Camera.CAMERA_MOVEOUT, len);
            Log.d("ANGULO_touchturnup", ""+Math.toDegrees(touchTurnUp));
        }
        //Flipa a câmera pra corrigir o y
        cam.rotateCameraZ((float)Math.toRadians(180));
        // Draw the main screen
        fb.clear(RGBColor.GREEN);
        world.renderScene(fb);
        world.draw(fb);
        fb.display();

    }

    public void incrementCameraZ() {
        cameraPosition = cameraPosition.calcAdd(new SimpleVector(0,0,1));
        cameraFocus = cameraFocus.calcAdd(new SimpleVector(0,0,1));
    }

    public void decrementCameraZ() {
        cameraPosition = cameraPosition.calcAdd(new SimpleVector(0, 0, -1));
        cameraFocus = cameraFocus.calcAdd(new SimpleVector(0, 0, -1));
    }
    public void decrementCameraX() {
        cameraPosition = cameraPosition.calcAdd(new SimpleVector(-1, 0, 0));
        cameraFocus = cameraFocus.calcAdd(new SimpleVector(-1, 0, 0));
    }

    public void incrementCameraX() {
        cameraPosition = cameraPosition.calcAdd(new SimpleVector(1, 0, 0));
        cameraFocus = cameraFocus.calcAdd(new SimpleVector(1, 0, 0));
    }
}
