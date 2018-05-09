package dongeronimo.com.testejpct;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Essa tela mostra a cena 3d. No momento seu layout é construído manualmente, ignorando o arquivo
 * de layout.
 * */
public class HelloWorld extends Activity implements ScaleGestureDetector.OnScaleGestureListener {
    private GLSurfaceView glView;
    private MyRenderer renderer;
    private ScaleGestureDetector gestureDec;
    private float scale;
    private float xpos = -1;
    private float ypos = -1;
    private float touchTurn = 0;
    private float touchTurnUp = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Invoca o construtor herdado antes de fazer as coisas do filho, necessário
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);//não vai ser usado nesse momento.

        //Cria a surface view
        glView = findViewById(R.id.glView);//new GLSurfaceView(getApplication());
        glView.setEGLContextClientVersion(2);
        glView.setPreserveEGLContextOnPause(true);
        //Cria o renderer e o põe na surface view
        renderer = new MyRenderer(getApplication());
        glView.setRenderer(renderer);
        //Põe a surface view na tela
        //setContentView(glView);
        //setContentView(R.layout.activity_hello_world);//não vai ser usado nesse momento.
        gestureDec = new ScaleGestureDetector(this.getApplicationContext(), this);
    }
    @Override
    protected void onPause(){
        super.onPause();
        glView.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        glView.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
        System.exit(0);
    }

    private boolean flyOnWorldEvent(MotionEvent me){
        //Pega as dimensões da tela
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screenHeight = displayMetrics.heightPixels;
        final int screenWidth = displayMetrics.widthPixels;
        //Calcula as regiões nas bordas da tela que permitem o movimento pra dentro e pros lados na tela
        final float upperY = screenHeight / 10;
        final float lowerY = screenHeight /10 * 9;
        final float leftX = screenWidth / 8;
        final float rightX = screenWidth / 8 * 7;
        //Vê se é pra voar no mundo.
        gestureDec.onTouchEvent(me);
        final float x = me.getX();
        final float y = me.getY();
        final int action = me.getAction();
        boolean eventHasBeenProcessed = false;
        if(y < upperY){//é na parte superior da tela, avança pra dentro da tela
            if(action==MotionEvent.ACTION_DOWN || action ==MotionEvent.ACTION_MOVE){
                renderer.incrementCameraZ();
                //Incrementa o z da câmera
                eventHasBeenProcessed = true;
            }
        }
        if(y > lowerY){//é na parte inferior da tela, recua pra fora da tela
            if(action==MotionEvent.ACTION_DOWN || action ==MotionEvent.ACTION_MOVE){
                //Decrementa o z da câmera
                renderer.decrementCameraZ();
                eventHasBeenProcessed = true;
            }
        }
        if(x < leftX){//strafe pra esquerda
            if(action==MotionEvent.ACTION_DOWN || action ==MotionEvent.ACTION_MOVE){
                //decrementa o x da câmera
                renderer.decrementCameraX();
                eventHasBeenProcessed = true;
            }
        }
        if(x > rightX){//strafe pra direita
            if(action==MotionEvent.ACTION_DOWN || action ==MotionEvent.ACTION_MOVE){
                //incrementa o x da câmera
                renderer.incrementCameraX();
                eventHasBeenProcessed = true;
            }
        }
        return eventHasBeenProcessed;
    }

    private boolean rotateCamera(MotionEvent me){
        gestureDec.onTouchEvent(me);
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            xpos = me.getX();
            ypos = me.getY();
            return true;
        }
        if (me.getAction() == MotionEvent.ACTION_UP) {
            xpos = -1;
            ypos = -1;
            touchTurn = 0;
            touchTurnUp = 0;
            return true;
        }
        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            float xd = me.getX() - xpos;
            float yd = me.getY() - ypos;
            xpos = me.getX();
            ypos = me.getY();
            touchTurn = xd / -100f;
            touchTurnUp = yd / -100f;
            renderer.setTouchTurn(touchTurn);
            renderer.setTouchTurnUp(touchTurnUp);
            return true;
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent me){
        //Testa se é pra voar no mundo
        if(flyOnWorldEvent(me)){
            return true;
        }
        if(rotateCamera(me)){
            return true;
        }
        try{
            Thread.sleep(50);
        }
        catch(Exception ex){

        }
        //Testa se é pra rotacionar a câmera no mundo
        return super.onTouchEvent(me);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float div = detector.getCurrentSpan() - detector.getPreviousSpan();
        div /= 5000;
        scale += div;
        if (scale > 0.063f) {
            scale = 0.063f;
        }
        if (scale < 0) {
            scale = 0;
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        //Faz nada
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        //Faz nada
    }
}
