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
import java.util.regex.*;

public class Linea{
    int nlinea, pc;
    Contloc conloc;
    String etq, codop, oper, error, mod_dirs;
    boolean org, end, coment;
    Tabop codop_inf;
    ListaTabsim lista_tabsim;
    //String comentario;
    
    public Linea(int numero_linea, int _pc){
        etq= "NULL";
        codop = "NULL";
        oper = "NULL";
        error = null;
        org = false;
        end = false;
        coment = false;
        mod_dirs = "";
        //comentario = null;
        nlinea = numero_linea;
        pc = _pc;
        conloc = new Contloc(pc);
    }
    
    private boolean Tokenizar(String linea){
        boolean correcto = true;
        String aux = linea;
        Pattern expReg;
        Matcher comprobador;
        expReg = Pattern.compile(".+\".*\\\\;.*\".*");
        comprobador = expReg.matcher(aux);
        while(comprobador.matches()){
            expReg = Pattern.compile("\\\\;");
            comprobador = expReg.matcher(aux);
            aux = comprobador.replaceFirst("_aux(punto_y_coma)");
        }
        StringTokenizer tokens = new StringTokenizer(aux, ";", true);
        if(tokens.countTokens() > 0){
            aux = tokens.nextToken();
            if(aux.charAt(0) != ';'){
                expReg = Pattern.compile("_aux\\(punto_y_coma\\)");
                comprobador = expReg.matcher(aux);
                aux = comprobador.replaceAll(";");
                tokens = new StringTokenizer(aux);
                if(aux.charAt(0) == ' ' || aux.charAt(0) == '\t'){
                    if(tokens.countTokens() < 1)
                        coment = true;
                    else
                        if(tokens.countTokens() < 2)
                            codop = tokens.nextToken();
                        else
                            if(tokens.countTokens() < 3){
                                codop = tokens.nextToken();
                                oper = tokens.nextToken();
                            }
                            else{
                                codop = tokens.nextToken();
                                aux = tokens.nextToken();
                                aux += tokens.nextToken("");
                                int i = aux.length() - 1;
                                while((aux.charAt(i) == '\t' || aux.charAt(i) == ' ') && i >= 0) i--;
                                aux = aux.substring(0, i + 1);
                                expReg = Pattern.compile("\".+\"");
                                comprobador = expReg.matcher(aux);
                                if(comprobador.matches()){
                                    expReg = Pattern.compile("\\\\\"");
                                    comprobador = expReg.matcher(aux);
                                    aux = comprobador.replaceAll("\"");
                                    oper = aux;
                                }
                                else{
                                    error = "Demasiados argumentos en la linea, se esperaba 'CODOP' o 'CODOP OPER'";
                                    correcto = false;
                                }
                            }
                }
                else{
                    if(tokens.countTokens() < 1)
                        coment = true;
                    else
                        if(tokens.countTokens() < 2){
                            error = "Faltan argumentos en la linea, se esperaban > 1. 'ETQ CODOP'";
                            correcto = false;
                        }
                        else
                            if(tokens.countTokens() < 3){
                                etq = tokens.nextToken();
                                codop = tokens.nextToken();
                            }
                            else 
                                if(tokens.countTokens() < 4){
                                    etq = tokens.nextToken();
                                    codop = tokens.nextToken();
                                    oper = tokens.nextToken();
                                }
                                else{
                                    etq = tokens.nextToken();
                                    codop = tokens.nextToken();
                                    aux = tokens.nextToken();
                                    aux += tokens.nextToken("");
                                    int i = aux.length() - 1;
                                    while((aux.charAt(i) == '\t' || aux.charAt(i) == ' ') && i >= 0) i--;
                                    aux = aux.substring(0, i + 1);
                                    expReg = Pattern.compile("\".+\"");
                                    comprobador = expReg.matcher(aux);
                                    if(comprobador.matches()){
                                        expReg = Pattern.compile("\\\\\"");
                                        comprobador = expReg.matcher(aux);
                                        aux = comprobador.replaceAll("\"");
                                        oper = aux;
                                    }
                                    else{
                                        error = "Demasiados argumentos en la linea, se esperaban < 4.'ETQ CODOP OPER'";
                                        correcto = false;
                                    }
                                }
                }
            }
            else
                coment = true;
        }
        else
            coment = true;
        return correcto;
    }
    
    public boolean Validar(String linea, ListaTabop lista_tabop){
        boolean correcto = true;
        Pattern expReg;
        Matcher comprobador;
        correcto = Tokenizar(linea);
        if(correcto && !coment){
            if(etq != null){
                expReg = Pattern.compile("[a-zA-Z][\\w]{0,7}");
                comprobador = expReg.matcher(etq);
                if(!comprobador.matches()){
                    error = "ETQ (Etiqueta) no valida";
                    correcto = false;
                }
            }
            if(codop != null && correcto){
                expReg = Pattern.compile("[a-zA-Z]((\\.[a-zA-Z]{0,3}|[a-zA-Z]{0,1}\\.[a-zA-Z]{0,2}|[a-zA-Z]{0,2}\\.[a-zA-Z]{0,1}|[a-zA-Z]{0,3}\\.)|[a-zA-Z]{0,4})");
                comprobador = expReg.matcher(codop);
                if(codop.toUpperCase().equals("END"))
                    end = true;
                if(!comprobador.matches()){
                    error = "CODOP (Codigo de Operacion) no valido";
                    correcto = false;
                }
            }
            if(oper != null && correcto){
            	if(end && !oper.equals("NULL")){
            		error = "la directiva \"END\" no debe tener OPER (operando)";
            		correcto = false;
            	}
                expReg = Pattern.compile(".+");
                comprobador = expReg.matcher(oper);
                if(!comprobador.matches()){
                    error = "OPER (operando) no valido";
                    correcto = false;
                }
            }
            /*if(correcto && !etq.equals("NULL"))
                correcto = ValidarEtq();*/
            //if(!end){
                if(correcto)
                    correcto = ValidarCodop(lista_tabop);
            //}
        }
        else
            correcto = false;
        return correcto;
    }
    
    public boolean ValidarEtq(Contloc conlocaux){
        boolean correcto = true;
        Tabsim simb = new Tabsim(etq, conlocaux.CONLOC);
        if(!lista_tabsim.Agregar(simb)){
            error = "ETQ (etiqueta) duplicada";
            correcto = false;
        }
        return correcto;
    }
    
    public boolean ValidarCodop(ListaTabop lista_tabop){
        boolean correcto = true, bandoper = false;
        ModDir direccionamiento = new ModDir();
        Contloc conlocaux = new Contloc(pc);
        codop_inf = lista_tabop.Buscar(codop.toUpperCase());
        if(codop_inf != null){
            mod_dirs = direccionamiento.Master(codop_inf, oper.toUpperCase());
            if(direccionamiento.error != null){
                error = direccionamiento.error;
                correcto = false;
            }
            else{
                for(int i = 0; i < codop_inf.nmodos; i++){
                    if(codop_inf.mod_dir.get(i).equals(mod_dirs))
                        if(correcto)
                            conlocaux = new Contloc(pc, codop_inf.b_total.get(i));
                }
            }
            /*mod_dirs = codop_inf.mod_dir.get(0);
            while(i < codop_inf.nmodos){
                mod_dirs = mod_dirs + ", " + codop_inf.mod_dir.get(i);
                i++;
            }*/
            /*i = 0;
            while(i < codop_inf.nmodos && !bandoper){
                if(codop_inf.b_calcular.get(i) > 0)
                    bandoper = true;
                i++;
            }
            if(!bandoper && !oper.equals("NULL")){
                System.out.println(bandoper+" "+oper);
                correcto = false;
                error = "no se necesita OPER (operando)";
            }
            if(bandoper && oper.equals("NULL")){
                correcto = false;
                error = "se requiere OPER (operando)";
            }*/
        }
        else{
            Directivas directiva = new Directivas(etq, codop.toUpperCase(), oper.toUpperCase(), pc);
            directiva.org = org;
            conlocaux = directiva.Validar();
            if(directiva.error != null){
                error = directiva.error;
                correcto = false;
            }
            else
                org = directiva.org;
            /*correcto = false;
            error = "no existe el CODOP (codigo de operacion)";*/
        }
        if(correcto && !etq.equals("NULL"))
            correcto = ValidarEtq(conlocaux);
        if(correcto)
            conloc = conlocaux;
        return correcto;
    }
}
