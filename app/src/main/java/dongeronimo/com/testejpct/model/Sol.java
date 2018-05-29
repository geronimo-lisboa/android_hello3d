package dongeronimo.com.testejpct.model;

import android.util.Log;

import com.threed.jpct.Light;
import com.threed.jpct.SimpleVector;

public class Sol implements IUpdatable {
    private Mundo mundo;
    private long currentTime = 0;
    private int horaToAddToTime;
    //Baixo nivel
    Light lightSource;

    private void calculaPosicao(double hora){
        //6h é 0, 12 é 90, 18 é 180, 24 é 270
        double angle = hora * 15 - 90;
        double x = Math.cos(angle) * 100;
        double y = Math.sin(angle) * 100;
        lightSource.setPosition(new SimpleVector(x,y, 0));
        Log.d("SIMCITY", "posicao do sol:"+x+", "+y);
    }

    public SimpleVector getPosicao(){
        return lightSource.getPosition();
    }

    public Sol(int horaAtual, Mundo m){
        this.mundo = m;
        horaToAddToTime = horaAtual * 1000;
        lightSource = new Light(m.getWorldObject());
        lightSource.setIntensity(255,255,255);
        //calculaPosicao(hora);
    }

    @Override
    public void avancarTempo(long deltaTime) {
        currentTime += deltaTime;//Isso está em milissegundos, cada segundo será 1h no meu tempo no momento.
        float _hora = currentTime / 1000 + horaToAddToTime;
        calculaPosicao(_hora);
    }
}
