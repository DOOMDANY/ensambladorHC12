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
        Pattern expReg;
        Matcher comprobador;
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
                        else{
                            if(listaInst.get(i).mod_dirs.equals("IDX")){
                                expReg = Pattern.compile("(A|B|D),[A-Za-z]+");
                                comprobador = expReg.matcher(listaInst.get(i).oper);
                                if(comprobador.matches()){
                                    listaInst.get(i).cod_maq = CMIDXida(listaInst.get(i));
                                }
                                else{
                                    expReg = Pattern.compile("(\\$|%|@|-|)([A-Fa-f]|[0-9])+,((-|\\+)[A-Za-z]+|[A-Za-z]+(-|\\+))");
                                    comprobador = expReg.matcher(listaInst.get(i).oper);
                                    if(comprobador.matches()){
                                        listaInst.get(i).cod_maq = CMIDXppdi(listaInst.get(i));
                                    }
                                    else{
                                        expReg = Pattern.compile("((\\$|%|@|\\-|)([A-Fa-f]|[0-9])+)?,[A-Za-z]+");
                                        comprobador = expReg.matcher(listaInst.get(i).oper);
                                        if(comprobador.matches()){
                                            listaInst.get(i).cod_maq = CMIDX5b(listaInst.get(i));
                                        }
                                    }
                                }
                            }
                            else{
                                if(listaInst.get(i).mod_dirs.equals("IDX1"))
                                    listaInst.get(i).cod_maq = CMIDX1(listaInst.get(i));
                                else{
                                    if(listaInst.get(i).mod_dirs.equals("IDX2"))
                                        listaInst.get(i).cod_maq = CMIDX2(listaInst.get(i));
                                    else{
                                        if(listaInst.get(i).mod_dirs.equals("[IDX2]"))
                                            listaInst.get(i).cod_maq = CMIDXI(listaInst.get(i));
                                        else{
                                            if(listaInst.get(i).mod_dirs.equals("[D,IDX]"))
                                                listaInst.get(i).cod_maq = CMIDXD(listaInst.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            i++;
        }
        return listaInst;
    }
    
    private String CMIDXI(Linea ln){
        String[] reg = new String[]{"X", "Y", "SP", "PC"};
        String[] rr = new String[]{"00", "01", "10", "11"};
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux1 = "", aux2, aux3, xb;
        while(tokens.hasMoreTokens())
            aux1 += tokens.nextToken();
        tokens = new StringTokenizer(ln.oper.substring(1, ln.oper.length() - 1), ",");
        aux2 = tokens.nextToken();
        try{
            BasesNumericas numero = new BasesNumericas(aux2);
            aux3 = numero.HexnD(4);
            aux2 = tokens.nextToken();
            i = 0;
            while(i < reg.length && !aux2.toUpperCase().equals(reg[i])) i++;
            xb = "111" + rr[i] + "011";
            numero = new BasesNumericas("%" + xb);
            aux1 += numero.HexnD(2) + aux3;
        }
        catch(NumberFormatException nfe){
            
        }
        return aux1;
    }
    
    private String CMIDXD(Linea ln){
        String[] reg = new String[]{"X", "Y", "SP", "PC"};
        String[] rr = new String[]{"00", "01", "10", "11"};
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux1 = "", aux2, xb;
        while(tokens.hasMoreTokens())
            aux1 += tokens.nextToken();
        tokens = new StringTokenizer(ln.oper.substring(1, ln.oper.length() - 1), ",");
        tokens.nextToken();
        aux2 = tokens.nextToken();
        try{
            i = 0;
            while(i < reg.length && !aux2.toUpperCase().equals(reg[i])) i++;
            xb = "111" + rr[i] + "111";
            BasesNumericas numero = new BasesNumericas("%" + xb);
            aux1 += numero.HexnD(2);
        }
        catch(NumberFormatException nfe){
            
        }
        return aux1;
    }
    
    private String CMIDXppdi(Linea ln){
        String[] reg = new String[]{"X", "Y", "SP"};
        String[] rr = new String[]{"00", "01", "10"};
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux1 = "", aux2, aux3, nnnn, xb, p;
        while(tokens.hasMoreTokens())
            aux1 += tokens.nextToken();
        tokens = new StringTokenizer(ln.oper, ",");
        aux2 = tokens.nextToken();
        BasesNumericas numero;
        try{
            numero = new BasesNumericas(aux2);
            aux2 = tokens.nextToken();
            if(aux2.endsWith("+") || aux2.endsWith("-")){
                p = "1";
                aux3 = aux2.substring(aux2.length() - 1, aux2.length());
                aux2 = aux2.substring(0, aux2.length() - 1);
            }
            else{
                p = "0";
                aux3 = aux2.substring(0, 1);
                aux2 = aux2.substring(1);
            }
            numero = new BasesNumericas(numero.ValorEntero() - 1, 10);
            if(aux3.charAt(0) == '-'){
                numero.Cauno();
            }
            nnnn = numero.BinnD(4);
            i = 0;
            while(i < reg.length && !aux2.toUpperCase().equals(reg[i])) i++;
            xb = rr[i] + "1" + p + nnnn;
            numero = new BasesNumericas("%" + xb);
            aux1 += numero.HexnD(2);
        }
        catch(NumberFormatException nfe){
            
        }
        return aux1;
    }
    
    private String CMIDXida(Linea ln){
        String[] reg = new String[]{"X", "Y", "SP", "PC"};
        String[] rr = new String[]{"00", "01", "10", "11"};
        String[] acum = new String[]{"A", "B", "D"};
        String[] aa = new String[]{"00", "01", "10"};
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux1 = "", aux2, xb;
        while(tokens.hasMoreTokens())
            aux1 += tokens.nextToken();
        tokens = new StringTokenizer(ln.oper, ",");
        aux2 = tokens.nextToken();
        i = 0;
        while(i < acum.length && !aux2.toUpperCase().equals(acum[i])) i++;
        aux2 = tokens.nextToken();
        int j = 0;
        while(j < reg.length && !aux2.toUpperCase().equals(reg[j])) j++;
        xb = "111" + rr[j] + "1" + aa[i];
        try{
            BasesNumericas numero = new BasesNumericas("%" + xb);
            aux1 += numero.HexnD(2);
        }
        catch(NumberFormatException nfe){
            
        }
        return aux1;
    }

    private String CMIDX5b(Linea ln){
        String[] reg = new String[]{"X", "Y", "SP", "PC"};
        String[] rr = new String[]{"00", "01", "10", "11"};
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux1 = "", aux2, nnnnn = "", xb;
        while(tokens.hasMoreTokens())
            aux1 += tokens.nextToken();
        tokens = new StringTokenizer(ln.oper, ",");
        aux2 = tokens.nextToken();
        BasesNumericas numero;
        try{
            numero = new BasesNumericas(aux2);
            nnnnn = numero.BinnD(5);
            aux2 = tokens.nextToken();
        }
        catch(NumberFormatException nfe){
            nnnnn = "00000";
        }
        finally{
            i = 0;
            while(i < reg.length && !aux2.toUpperCase().equals(reg[i])) i++;
            xb = rr[i] + "0" + nnnnn;
            try{
                numero = new BasesNumericas("%" + xb);
                aux1 += numero.HexnD(2);
            }
            catch(NumberFormatException nfe){
                
            }
        }
        return aux1;
    }
    
    private String CMIDX1(Linea ln){
        String[] reg = new String[]{"X", "Y", "SP", "PC"};
        String[] rr = new String[]{"00", "01", "10", "11"};
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux1 = "", aux2, xb, z = "0", s = "0", ff;
        while(tokens.hasMoreTokens())
            aux1 += tokens.nextToken();
        tokens = new StringTokenizer(ln.oper, ",");
        aux2 = tokens.nextToken();
        BasesNumericas numero;
        try{
            numero = new BasesNumericas(aux2);
            ff = numero.HexnD(2);
            if(numero.ValorEntero() < 0)
                s = "1";            
            aux2 = tokens.nextToken();
            i = 0;
            while(i < reg.length && !aux2.toUpperCase().equals(reg[i])) i++;
            xb = "111" + rr[i] + "0" + z + s;
            numero = new BasesNumericas("%" + xb);
            aux1 += numero.HexnD(2) + ff;
        }
        catch(NumberFormatException nfe){
            
        }
        return aux1;
    }
    
    private String CMIDX2(Linea ln){
        String[] reg = new String[]{"X", "Y", "SP", "PC"};
        String[] rr = new String[]{"00", "01", "10", "11"};
        int i = 0;
        while(!ln.codop_inf.mod_dir.get(i).equals(ln.mod_dirs)) i++;
        StringTokenizer tokens = new StringTokenizer(ln.codop_inf.cod_hex.get(i), " ");
        String aux1 = "", aux2, xb, z = "1", s = "0", eeff;
        while(tokens.hasMoreTokens())
            aux1 += tokens.nextToken();
        tokens = new StringTokenizer(ln.oper, ",");
        aux2 = tokens.nextToken();
        BasesNumericas numero;
        try{
            numero = new BasesNumericas(aux2);
            eeff = numero.HexnD(4);
            if(numero.ValorEntero() < 0)
                s = "1";            
            aux2 = tokens.nextToken();
            i = 0;
            while(i < reg.length && !aux2.toUpperCase().equals(reg[i])) i++;
            xb = "111" + rr[i] + "0" + z + s;
            numero = new BasesNumericas("%" + xb);
            aux1 += numero.HexnD(2) + eeff;
        }
        catch(NumberFormatException nfe){
            
        }
        return aux1;
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
