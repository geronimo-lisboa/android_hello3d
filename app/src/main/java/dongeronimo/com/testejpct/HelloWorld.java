package dongeronimo.com.testejpct;

import android.app.Activity;
import android.os.Bundle;

public class HelloWorld extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Invoca o construtor herdado antes de fazer as coisas do filho, necessário
        super.onCreate(savedInstanceState);
        //Cria a surface view
        //Cria o renderer e o põe na surface view
        //Põe a surface view na tela

        //setContentView(R.layout.activity_hello_world);//não vai ser usado nesse momento.
    }
}
