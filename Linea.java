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
    int nlinea;
    String etq, codop, oper, error;
    boolean end, coment;
    //String comentario;
    
    public Linea(int numero_linea){
        etq= "NULL";
        codop = "NULL";
        oper = "NULL";
        error = null;
        end = false;
        coment = false;
        //comentario = null;
        nlinea = numero_linea;
    }
    
    private boolean Tokenizar(String linea){
        boolean correcto = true;
        StringTokenizer tokens = new StringTokenizer(linea, ";", true);
        if(tokens.countTokens() > 0){
            linea = tokens.nextToken();
            if(linea.charAt(0) != ';'){
                tokens = new StringTokenizer(linea);
                if(linea.charAt(0) == ' ' || linea.charAt(0) == '\t'){
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
                                error = "Demasiados argumentos en la linea, se esperaba 'CODOP' o 'CODOP OPER'";
                                correcto = false;
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
                                    error = "Demasiados argumentos en la linea, se esperaban < 4.'ETQ CODOP OPER'";
                                    correcto = false;
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
    
    public boolean Validar(String linea){
        boolean correcto = true;
        Pattern expReg;
        Matcher comprobador;
        if(Tokenizar(linea)){
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
                expReg = Pattern.compile(".+");
                comprobador = expReg.matcher(oper);
                if(!comprobador.matches()){
                    error = "OPER (operando) no valido";
                    correcto = false;
                }
            }
        }
        else
            correcto = false;
        return correcto;
    }
}
