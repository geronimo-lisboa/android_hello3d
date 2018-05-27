package dongeronimo.com.testejpct.model;

public class Ceu {
    private Sol sol;
    private Mundo mundo;

    public void mudarClima(){

    }

    public Ceu(int horaAtual, Mundo mundo){
        this.mundo = mundo;
        sol = new Sol(horaAtual, mundo);
    }
}
