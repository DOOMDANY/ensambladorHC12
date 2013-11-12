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

public class ListaTabop {
    ArrayList<Tabop> lista;
    
    public ListaTabop(){
        lista = new ArrayList<Tabop>();
    }
    
    public boolean Insertar(Tabop codop_inf){
        if(codop_inf != null)
            if(!lista.isEmpty())
                if(lista.get(lista.size()-1).codop.equals(codop_inf.codop))
                    lista.get(lista.size()-1).AgregarDireccionamiento(codop_inf);
                else
                    lista.add(codop_inf);
            else
                lista.add(codop_inf);
        else
            return false;
        return true;
    }
    
    public Tabop Buscar(String codop){
        Tabop codop_inf;
        int i, m, j;
        i = 0;
        j = lista.size() - 1;
        while(i <= j){
            m = (i + j) / 2;
            codop_inf = lista.get(m);
            if(!codop.equals(codop_inf.codop))
                if(codop.compareTo(lista.get(m).codop) < 0)
                    j = m - 1;
                else
                    i = m + 1;
            else
                return codop_inf;
        }
        return null;
    }
}
