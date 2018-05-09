package dongeronimo.com.testejpct;

import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;

import java.util.ArrayList;
import java.util.List;

public class CameraHelper {
    public interface ShaderDataListerner{
        public void apply(CameraHelper cameraHelper);
    }

    private List<ShaderDataListerner> shaderDataListeners = new ArrayList<>();
    private enum DirecaoRotacao {H, V}
    private Camera camera;
    private final SimpleVector initialCameraFocus;
    private final SimpleVector initialCameraPosition;
    private final float initialHorizontalAxis, initialVerticalAxis;
    private SimpleVector cameraFocus;
    private SimpleVector cameraPosition;
    private float horizontalAxisRotation = 0;
    private float verticalAxisRotation = 0;

    public void moveLeft(){
        SimpleVector vec = new SimpleVector(1,0,0);
        vec.scalarMul(-1.0f);
        cameraPosition = cameraPosition.calcAdd(vec);
        cameraFocus = cameraFocus.calcAdd(vec);
    }
    public void moveRight(){
        SimpleVector vec = new SimpleVector(1,0,0);
        cameraPosition = cameraPosition.calcAdd(vec);
        cameraFocus = cameraFocus.calcAdd(vec);
    }
    public void moveFoward(){
        final SimpleVector vec = new SimpleVector(0,0,1);
        cameraPosition = cameraPosition.calcAdd(vec);
        cameraFocus = cameraFocus.calcAdd(vec);
    }
    public void moveBackward(){
        SimpleVector vec = new SimpleVector(0,0,1);
        vec.scalarMul(-1.0f);
        cameraPosition = cameraPosition.calcAdd(vec);
        cameraFocus = cameraFocus.calcAdd(vec);
    }

    public SimpleVector getCameraPosition() {
         return cameraPosition;
    }

    public void addShaderDataListener(ShaderDataListerner l){
        shaderDataListeners.add(l);
    }
    /**
     * É aqui que eu uso o focus e posição, pra fazer um lookat*/
    private void lookAt(){
        camera.setPosition(cameraPosition);
        camera.lookAt(cameraFocus);  //superficie.getCenter());
    }

    public void addHorizontalRotation(float v){
        horizontalAxisRotation += v;
    }
    public void addVerticalRotation(float v){
        verticalAxisRotation += v;
    }
    /**
     * Rotação ao redor da origem. O enum controla se é rotação na horizontal ou na vertical.
     * */
    private void rotateAroundOrigin(DirecaoRotacao dir){
        //Vai pra origem
        SimpleVector vecFromOrigin = cameraPosition.calcSub(cameraFocus);//O vetor olho-foco, no sist. de coordenadas da origem
        final float len = vecFromOrigin.length();
        camera.moveCamera(Camera.CAMERA_MOVEIN, len);
        //rotaciona
        switch (dir){
            case H:
                final SimpleVector vX = camera.getXAxis();
                camera.rotateAxis(vX, horizontalAxisRotation);
                break;
            case V:
                final SimpleVector vY = camera.getYAxis();
                camera.rotateAxis(vY, verticalAxisRotation);
                break;
        }
        //retorna.
        camera.moveCamera(Camera.CAMERA_MOVEOUT, len);
    }
    /**
     * O eixo Y da jpct vai do topo do aparelho pra base do aparelho. Isso é o contrário do que se espera e não há
     * como corrigir isso dentro da jpct. Como não quero ter que pensar que y negativo é na vardade positivo me parece
     * mais cômodo rotacionar a câmera.*/
    private void correctJPCTYAxis(){
        camera.rotateCameraZ((float)Math.toRadians(180));
    }
    /**
     * Cria a câmera guardando suas propriedades para poder fazer reset.
     * */
    public CameraHelper(Camera cam, SimpleVector initialFocus, SimpleVector initialPosition,
                        float initialHRot, float initialVRot){
        camera = cam;
        initialCameraFocus = initialFocus;
        initialCameraPosition = initialPosition;
        cameraFocus = initialCameraFocus;
        cameraPosition = initialCameraPosition;
        initialHorizontalAxis = initialHRot;
        initialVerticalAxis = initialVRot;
    }
    /**
     * Atualiza a câmera a partir de suas propriedades.
     * */
    public void update(){
        lookAt();
        rotateAroundOrigin(DirecaoRotacao.H);
        rotateAroundOrigin(DirecaoRotacao.V);
        correctJPCTYAxis();
        for(ShaderDataListerner l:shaderDataListeners){
            l.apply(this);
        }
    }

    public void reset(){
        cameraPosition = initialCameraPosition;
        cameraFocus = initialCameraFocus;
        horizontalAxisRotation = initialHorizontalAxis;
        verticalAxisRotation = initialVerticalAxis;
    }


}
