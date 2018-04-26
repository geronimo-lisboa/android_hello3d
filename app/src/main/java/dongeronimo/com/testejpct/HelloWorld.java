package dongeronimo.com.testejpct;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
/**
 * Essa tela mostra a cena 3d. No momento seu layout é construído manualmente, ignorando o arquivo
 * de layout.
 * */
public class HelloWorld extends Activity {
    private GLSurfaceView glView;
    private MyRenderer renderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Invoca o construtor herdado antes de fazer as coisas do filho, necessário
        super.onCreate(savedInstanceState);
        //Cria a surface view
        glView = new GLSurfaceView(getApplication());
        glView.setEGLContextClientVersion(2);
        glView.setPreserveEGLContextOnPause(true);
        //Cria o renderer e o põe na surface view
        renderer = new MyRenderer(getApplication());
        glView.setRenderer(renderer);
        //Põe a surface view na tela
        setContentView(glView);
        //setContentView(R.layout.activity_hello_world);//não vai ser usado nesse momento.
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
}
