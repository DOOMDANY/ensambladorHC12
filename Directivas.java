/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author DOOMDANY
 */
import java.util.regex.*;

public class Directivas {
    String error, etq, codop, oper;
    int pc;
    boolean org;
    final String[] nom_directiva = new String[]{"DB",   //[0-255] conloc +1
                                                "DC.B", //[0-255] conloc +1
                                                "DC.W", //[0-65535] conloc +2
                                                "DS",   //[0-65535] conloc +OPER
                                                "DS.B", //[0-65535] conloc +OPER
                                                "DS.W", //[0-65535] conloc +2*OPER
                                                "DW",   //[0-65535] conloc +2
                                                "END",  //sin OPER
                                                "EQU",  //necesita ETQ [0-65535] conloc =OPER
                                                "FCB",  //[0-255] conloc +1
                                                "FCC",  //conloc +String.lentgh
                                                "FDB",  //[0-65535] conloc +2
                                                "ORG",  //solo 1 [0-65535] conloc =OPER
                                                "RMB",  //[0-65535] conloc +OPER
                                                "RMW"}; //[0-65535] conloc +2*OPER
    
    public Directivas(String _etq, String _codop, String _oper, int _pc){
        etq = _etq;
        codop = _codop;
        oper = _oper;
        pc = _pc;
        error = null;
    }
    
    public Contloc Validar(){
    	/*FORMATOS DE OPERANDOS:
         * 0    desconocido
         * 1    directiva END
         * 2    directiva FCC
         * 3    directivas con operandos numericos
         * 4    no existe la directiva
         */
        int formato = 4;
        /*Pattern expReg;
        Matcher comprobador;*/
        for(int i = 0; i < nom_directiva.length && formato == 4; i++){
            if(nom_directiva[i].equals(codop))
                formato = 0;
        }
        if(formato != 4)
            formato = FormatoOper();
        Contloc conloc = new Contloc(pc);
        switch(formato){
            case 0:
                error = "formato de OPER (operando) no valido para alguna directiva";
            break;
            case 1:
                /*expReg = Pattern.compile("END");
                comprobador = expReg.matcher(codop);*/
                if(codop.equals("END"))
                    conloc = End();
                else
                    error = "La directiva [" + codop + "] necesita un OPER (operando)";
            break;
            case 2:
                if(codop.equals("FCC"))
                    conloc = Fcc();
                else
                    error = "la directiva [" + codop + "] necesita un OPER (operando) con valor numerico";
            break;
            case 3:
                /*expReg = Pattern.compile("DB|DC\\.B|FCB");
                comprobador = expReg.matcher(codop);*/
                if(codop.equals("DB") || codop.equals("DC.B") || codop.equals("FCB"))
                    conloc = DConstantes1B();
                else{
                    /*expReg = Pattern.compile("DW|DC\\.W|FDB");
                    comprobador = expReg.matcher(codop);*/
                    if(codop.equals("DW") || codop.equals("DC.W") || codop.equals("FDB"))
                        conloc = DConstantes2B();
                    else{
                        /*expReg = Pattern.compile("DS|DS\\.B|RMB");
                        comprobador = expReg.matcher(codop);*/
                        if(codop.equals("DS") || codop.equals("DS.B") || codop.equals("RMB"))
                            conloc = Drem1B();
                        else{
                            /*expReg = Pattern.compile("DS\\.W|RMW");
                            comprobador = expReg.matcher(codop);*/
                            if(codop.equals("DS.W") || codop.equals("RMW"))
                                conloc = Drem2B();
                            else{
                                if(codop.equals("EQU"))
                                    conloc = Equ();
                                else{
                                    if(codop.equals("ORG"))
                                        conloc = Org();
                                    else
                                        error = "la directiva [" + codop + "] no utiliza OPER (operando) numerico";
                                }
                            }
                        }
                    }
                }
            break;
            case 4:
                error = oper + "no es una DIRECTIVA valida";
            break;
        }
        return conloc;
    }
    
    private int FormatoOper(){
        int formato = 0;
        Pattern expReg;
        Matcher comprobador;
        expReg = Pattern.compile("NULL");// directiva END
        comprobador = expReg.matcher(oper);
        if(comprobador.matches())
            formato = 1;
        else{
            expReg = Pattern.compile("\".+\"");// directiva FCC
            comprobador = expReg.matcher(oper);
            if(comprobador.matches()){
                formato = 2;
            }
            else{
                expReg = Pattern.compile("(\\$|@|%|\\-|)([A-F]|[0-9])+");// directiva c/numero
                comprobador = expReg.matcher(oper);
                if(comprobador.matches()){
                    formato = 3;
                }
            }
        }
        return formato;
    }
    
    private Contloc End(){
        Contloc conloc = new Contloc(pc);
        if(oper.equals("NULL"))
            conloc = new Contloc(pc);
        else
            error = "la DIRECTIVA [END] no debe tener OPER (operando)";
        return conloc;
    }
    
    private Contloc Fcc(){
        String aux = new String(oper.substring(1, oper.length() - 1));
        //Contloc conloc = new Contloc(pc, aux.length());
        return new Contloc(pc, aux.length());
    }
    
    private Contloc DConstantes1B(){
        Contloc conloc = new Contloc(pc);
        try{
            BasesNumericas numero = new BasesNumericas(oper);
            if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 255){
                conloc = new Contloc(pc, 1);
            }
            else
                error = "valor fuera de rango para DIRECTIVAS de 1 Byte [DB, DC.B, FCB]";
        }
        catch(NumberFormatException nfe){
            error = "el OPER (operando) no es un valor numerico valido para [DB, DC.B, FCB]";
        }
        return conloc;
    }
    
    private Contloc DConstantes2B(){
        Contloc conloc = new Contloc(pc);
        try{
            BasesNumericas numero = new BasesNumericas(oper);
            if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 65535){
                conloc = new Contloc(pc, 2);
            }
            else
                error = "valor fuera de rango para DIRECTIVAS de 2 Bytes [DW, DC.W, FDB]";
        }
        catch(NumberFormatException nfe){
            error = "el OPER (operando) no es un valor numerico valido para [DW, DC.W, FDB]";
        }
        return conloc;
    }
    
    private Contloc Drem1B(){
        Contloc conloc = new Contloc(pc);
        try{
            BasesNumericas numero = new BasesNumericas(oper);
            if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 65535){
                conloc = new Contloc(pc, numero.ValorEntero());
            }
            else
                error = "valor fuera de rango para DIRECTIVAS de reserva de espacio en memoria de 1 Byte [DS, DS.B, RMB]";
        }
        catch(NumberFormatException nfe){
            error = "el OPER (operando) no es un valor numerico valido para [DS, DS.B, RMB]";
        }
        return conloc;
    }
    
    private Contloc Drem2B(){
        Contloc conloc = new Contloc(pc);
        try{
            BasesNumericas numero = new BasesNumericas(oper);
            if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 65535){
                conloc = new Contloc(pc, numero.ValorEntero()*2);
            }
            else
                error = "valor fuera de rango para DIRECTIVAS de reserva de espacio en memoria de 2 Bytes [DS.W, RMW]";
        }
        catch(NumberFormatException nfe){
            error = "el OPER (operando) no es un valor numerico valido para [DS.W, RMW]";
        }
        return conloc;
    }
    
    private Contloc Equ(){
        Contloc conloc = new Contloc(pc);
        if(!etq.equals("NULL")){
            try{
                BasesNumericas numero = new BasesNumericas(oper);
                if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 65535){
                    conloc = new Contloc(numero.ValorEntero());
                    conloc.PC = pc;
                }
                else
                    error = "valor fuera de rango para DIRECTIVA [EQU]";
            }
            catch(NumberFormatException nfe){
                error = "el OPER (operando) no es un valor numerico valido para [EQU]";
            }
        }
        else
            error = "laDIRECTIVA [EQU] necesita una ETQ (etiqueta)";
        return conloc;
    }
    
    private Contloc Org(){
        Contloc conloc = new Contloc(pc);
        if(!org){
            try{
                BasesNumericas numero = new BasesNumericas(oper);
                if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 65535){
                    conloc = new Contloc(numero.ValorEntero());
                    org = true;
                }
                else
                    error = "valor fuera de rango para DIRECTIVA [ORG]";
            }
            catch(NumberFormatException nfe){
                error = "el OPER (operando) no es un valor numerico valido para [ORG]";
            }
        }
        else
            error = "la DIRECTIVA [ORG] ya existe una vez";
        return conloc;
    }
}
