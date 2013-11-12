/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author DOOMDANY
 */
//import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CodigoMaquina{
    ListaTabop lista_tabop;
    ListaTabsim lista_tabsim;
    ArrayList<Linea> listaInst = new ArrayList<Linea>();
    
    public CodigoMaquina(ListaTabop _lista_tabop, ListaTabsim _lista_tabsim, ArrayList<Linea> _listaInst){
        lista_tabop = _lista_tabop;
        lista_tabsim = _lista_tabsim;
        listaInst = _listaInst;
    }
    
    public String ComprobarEtq(){
        Pattern expReg = Pattern.compile("[a-zA-Z][\\w]{0,7}");
        Matcher comprobador;
        String oper;
        Tabsim simb;
        String error = null;
        int i = 0, nlinea;
        while(i < listaInst.size() && error == null){
            nlinea = listaInst.get(i).nlinea;
            oper = listaInst.get(i).oper;
            comprobador = expReg.matcher(oper);
            if(comprobador.matches() && !oper.equals("NULL")){
                simb = lista_tabsim.Buscar(oper);
                if(simb == null){
                	error = nlinea + "\tla ETQ a la que hace referencia el OPER no existe";
                	String etq = listaInst.get(i).etq;
                	if(!etq.equals("NULL"))
                		lista_tabsim.Eliminar(etq);
                	listaInst.remove(i);
                    //error = nlinea + "\tla ETQ a la que hace referencia el OPER no existe, imposible entrar al \"PASO 2\" del ENSAMBLADOR";
                }
            }
            i++;
        }
        return error;
    }
    
    public ArrayList<Linea> CMMaster(){
        int i = 0;
        while(i < listaInst.size()){
            if(listaInst.get(i).mod_dirs.equals("INH"))
                listaInst.get(i).cod_maq = CMINH(listaInst.get(i));
            else{
                if(listaInst.get(i).mod_dirs.equals("DIR"))
                    listaInst.get(i).cod_maq = CMDIR(listaInst.get(i));
                else{
                    if(listaInst.get(i).mod_dirs.equals("EXT"))
                        listaInst.get(i).cod_maq = CMEXT(listaInst.get(i));
                    else{
                        if(listaInst.get(i).mod_dirs.startsWith("IMM"))
                            listaInst.get(i).cod_maq = CMIMM(listaInst.get(i));
                    }
                }
            }
            i++;
        }
        return listaInst;
    }
    
    private String CMINH(Linea ln){
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(0), " ");
        String aux = "";
        while(tokens.hasMoreTokens())
            aux += tokens.nextToken();
        return aux;
    }
    
    private String CMDIR(Linea ln){
        BasesNumericas numero;
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux = "";
        while(tokens.hasMoreTokens())
            aux += tokens.nextToken();
        try{
            numero = new BasesNumericas(ln.oper);
            aux += numero.HexnD(2);
        }
        catch(NumberFormatException nfe){
            aux = "";
        }
        return aux;
    }
    
    private String CMEXT(Linea ln){
        BasesNumericas numero;
        Tabsim simb;
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux = "";
        while(tokens.hasMoreTokens())
            aux += tokens.nextToken();
        try{
            numero = new BasesNumericas(ln.oper);
            aux += numero.HexnD(4);
        }
        catch(NumberFormatException nfe1){
            simb = lista_tabsim.Buscar(ln.oper);
            if(simb != null){
                try{
                    numero = new BasesNumericas(simb.direccion, 10);
                    aux += numero.HexnD(4);
                }
                catch(NumberFormatException nfe2){
                    aux = "";
                }
            }
            else
                aux = "";
        }
        return aux;
    }
    
    private String CMIMM(Linea ln){
        BasesNumericas numero;
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux = "";
        while(tokens.hasMoreTokens())
            aux += tokens.nextToken();
        try{
            numero = new BasesNumericas(ln.oper.substring(1));
            aux += numero.HexnD(ln.codop_inf.b_calcular.get(i) * 2);
        }
        catch(NumberFormatException nfe){
            
        }
        return aux;
    }
}
