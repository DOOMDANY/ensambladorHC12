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

public class ListaTabsim extends ArrayList<Tabsim>{    
    
    public ListaTabsim(){
    }
    
    public boolean Agregar(Tabsim simb){
        int i = 0;
        while(i < this.size()){
            if(!simb.etq.equals(this.get(i).etq)){
                if(simb.etq.compareTo(this.get(i).etq) > 0)
                    i++;
                else{
                    this.add(i, simb);
                    return true;
                }
            }
            else
                return false;
        }
        this.add(simb);
        return true;
    }
    
    public Tabsim Buscar(String etq){
        Tabsim aux;
        int i, m, j;
        i = 0;
        j = this.size() - 1;
        while(i <= j){
            m = (i + j) / 2;
            aux = this.get(m);
            if(!etq.equals(aux.etq))
                if(etq.compareTo(this.get(m).etq) < 0)
                    j = m - 1;
                else
                    i = m + 1;
            else
                return aux;
        }
        return null;
    }
}
