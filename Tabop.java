/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author DOOMDANY
 */
import java.util.*;

public class Tabop {
    String codop;
    int nmodos;
    ArrayList<String> mod_dir, cod_hex;
    ArrayList<Integer> b_calculados, b_calcular, b_total;
    
    public Tabop(){
        codop = "";
        nmodos = 1;
        mod_dir = new ArrayList<String>();
        cod_hex = new ArrayList<String>();
        b_calculados = new ArrayList<Integer>();
        b_calcular = new ArrayList<Integer>();
        b_total = new ArrayList<Integer>();
    }
    
    public void AgregarDireccionamiento(Tabop codop_inf){
        mod_dir.add(codop_inf.mod_dir.remove(0));
        cod_hex.add(codop_inf.cod_hex.remove(0));
        b_calculados.add(codop_inf.b_calculados.remove(0));
        b_calcular.add(codop_inf.b_calcular.remove(0));
        b_total.add(codop_inf.b_total.remove(0));
        nmodos++;
    }
}
