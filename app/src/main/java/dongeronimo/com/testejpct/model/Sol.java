package dongeronimo.com.testejpct.model;



import android.util.Log;

import com.threed.jpct.Light;
import com.threed.jpct.SimpleVector;

public class Sol implements IUpdatable {
    private Mundo mundo;
    private long minutes = 0;

    //Baixo nivel
    Light lightSource;


    private void calculaPosicao(long minutos){
        //6h é 0, 12 é 90, 18 é 180, 24 é 270
        double angleInDegs = minutos / 4;//   elapsedTimeInMs/1000.0f * 15;
        double x = Math.cos(Math.toRadians(angleInDegs)) * 1000;
        double y = Math.sin(Math.toRadians(angleInDegs)) * 1000;
        lightSource.setPosition(new SimpleVector(x,y, 0));
        setTint();
    }
    private void setTint(){
        double angleInDegs = minutes / 4;
        float r = (float) (Math.pow(Math.E, Math.sin(Math.toRadians(angleInDegs)))-1); //   Math.sin(Math.toRadians(angleInDegs));
        float g = (float) Math.sin(Math.toRadians(angleInDegs));//Math.sin(Math.toRadians(angleInDegs));
        float b = (float) (Math.sin(Math.toRadians(angleInDegs))); //0;

        lightSource.setIntensity(r,g,b);
    }

    public SimpleVector getPosicao(){
        return lightSource.getPosition();
    }
    public SimpleVector getTint(){return lightSource.getIntensity();}
    public boolean isNight(){
        double angleInDegs = minutes / 4;
        if(190 < angleInDegs && angleInDegs < 350)
            return true;
        else
            return false;
    }

    public Sol(int horaAtual, Mundo m){
        this.mundo = m;
        lightSource = new Light(m.getWorldObject());
        lightSource.setIntensity(255,255,255);
        minutes = 0;//horaAtual * 60;
        calculaPosicao(minutes);
    }

    @Override
    public void avancarTempo() {
        minutes += 2;
        calculaPosicao(minutes);
    }
}
