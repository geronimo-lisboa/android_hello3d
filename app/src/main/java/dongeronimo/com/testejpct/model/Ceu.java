package dongeronimo.com.testejpct.model;

public class Ceu implements IUpdatable{
    private Sol sol;
    private Mundo mundo;

    public void mudarClima(){

    }

    public Ceu(int horaAtual, Mundo mundo){
        this.mundo = mundo;
        sol = new Sol(horaAtual, mundo);
    }

    @Override
    public void avancarTempo() {
        sol.avancarTempo();
    }

    public Sol getSol() {
        return sol;
    }
}
