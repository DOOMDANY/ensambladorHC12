
package ensambladorhc12;

import java.util.*;
import java.util.regex.*;

public class ModDir {
    
    String error;
    
    public ModDir(){
        error = null;
    }
    
    public String Master(Tabop codop_inf, String oper){
        /*FORMATOS DE OPERANDOS:
         * 0    desconocido
         * 1    NULL (INH, IMM0, los que no usen operando)
         * 2    # (IMM8, IMM16)
         * 3    n (DIR)
         * 4    ETQ (EXT, REL8, REL9, REL16)
         * 5    ,r (IDX de 5 bits)
         * 6    n,r (IDX de 5 bits, IDX1, IDX2)
         * 7    [n,r] ([IDX2])
         * 8    [D,r] ([D,IDX])
         * 9    n,-+r-+ (IDX con pre/post decremento/incremento)
         * 10   ABD,r (IDX indizado de acumulador)
         */
        int formato;
        String mod_dir = "";
        boolean aux;
        aux = CodopConOper(codop_inf, oper);
        if(error == null){
            if(aux)
                formato = FormatoOper(oper);
            else
                formato = 1;
        }
        else
            formato = 11;
        switch(formato){
            case 0:
                error = "formato de OPER (operando) no valido para ningun CODOP";
            break;
            case 1:
                /*if((mod_dir = ModINH(codop_inf)).equals(""))
                    if((mod_dir = ModIMM(codop_inf, oper)).equals(""))
                        error = "el CODOP necesita OPER(s) (operando(s))";*/
                mod_dir = ModSinOper(codop_inf);
            break;
            case 2:
                mod_dir = ModIMM(codop_inf, oper);
            break;
            case 3:
                if((mod_dir = ModDIR(codop_inf, oper)).equals(""))
                    if(error == null)
                        if((mod_dir = ModEXT(codop_inf, oper)).equals(""))
                            if(error == null)
                                if((mod_dir = ModREL(codop_inf, oper)).equals(""))
                                    if(error == null)
                                        error = "el CODOP no soporta direccionamiento \"DIR\", \"EXT\", \"REL\"";
            break;
            case 4:
                if((mod_dir = ModEXT(codop_inf, oper)).equals(""))
                    if(error == null)
                        if((mod_dir = ModREL(codop_inf, oper)).equals(""))
                            if(error == null)
                                error = "el CODOP no soporta direccionamiento \"EXT\", \"REL\"";
            break;
            case 5:
                if((mod_dir = ModIDX_5b(codop_inf, oper)).equals(""))
                    if(error == null)
                        error = "el CODOP no soporta direccionamiento \"IDX\"";
            break;
            case 6:
                if((mod_dir = ModIDX_5b(codop_inf, oper)).equals(""))
                    if(error == null)
                        if((mod_dir = ModIDX1(codop_inf, oper)).equals(""))
                            if(error == null)
                                if((mod_dir = ModIDX2(codop_inf, oper)).equals(""))
                                    if(error == null)
                                        error = "el CODOP no soporta direccionamiento \"IDX\", \"IDX1\", \"IDX2\"";
            break;
            case 7:
                mod_dir = ModIDX2_I(codop_inf, oper);
            break;
            case 8:
                mod_dir = ModDIDX(codop_inf, oper);
            break;
            case 9:
                mod_dir = ModIDX_ppdi(codop_inf, oper);
            break;
            case 10:
                mod_dir = ModIDX_ida(codop_inf, oper);
            break;
        }
        //System.out.println(error);
        return mod_dir;
    }
    
    private int FormatoOper(String oper){
        int formato = 0;
        Pattern expReg;
        Matcher comprobador;
        expReg = Pattern.compile("NULL"); //NULL (INH, IMM0, los que no usen operando)
        comprobador = expReg.matcher(oper);
        if(comprobador.matches())
            formato = 1;
        else{
            expReg = Pattern.compile("#(\\$|%|@|\\-|)([A-F]|[0-9])+"); //# (IMM8, IMM16)
            comprobador = expReg.matcher(oper);
            if(comprobador.matches())
                formato = 2;
            else{
                expReg = Pattern.compile("(\\$|%|@|\\-|)([A-F]|[0-9])+"); //n (DIR, EXT, REL8, REL9, REL16)
                comprobador = expReg.matcher(oper);
                if(comprobador.matches())
                    formato = 3;
                else{
                    expReg = Pattern.compile("[A-Z][\\w]+"); //ETQ (EXT, REL8, REL9, REL16)
                    comprobador = expReg.matcher(oper);
                    if(comprobador.matches()){
                        formato = 4;
                    }
                    else{
                        expReg = Pattern.compile(",[A-Z]+"); //,r (IDX de 5 bits)
                        comprobador = expReg.matcher(oper);
                        if(comprobador.matches())
                            formato = 5;
                        else{
                            expReg = Pattern.compile("[A-Z],[A-Z]+"); //ABD,r (IDX indizado de acumulador)
                            comprobador = expReg.matcher(oper);
                            if(comprobador.matches())
                                formato = 10;
                            else{
                                expReg = Pattern.compile("\\[[A-Z]+,[A-Z]+\\]"); //[D,r] ([D,IDX])
                                comprobador = expReg.matcher(oper);
                                if(comprobador.matches())
                                    formato = 8;
                                else{
                                    expReg = Pattern.compile("\\[(\\$|%|@|\\-|)([A-F]|[0-9])+,[A-Z]+\\]"); //[n,r] ([IDX2])
                                    comprobador = expReg.matcher(oper);
                                    if(comprobador.matches())
                                        formato = 7;
                                    else{
                                        expReg = Pattern.compile("(\\$|%|@|-|)([A-F]|[0-9])+,((-|\\+)[A-Z]+|[A-Z]+(-|\\+))");//n,-+r-+ (IDX con pre/post decremento/incremento)
                                        comprobador = expReg.matcher(oper);
                                        if(comprobador.matches())
                                            formato = 9;
                                        else{
                                            expReg = Pattern.compile("(\\$|%|@|\\-|)([A-F]|[0-9])+,[A-Z]+"); //n,r (IDX de 5 bits, IDX1, IDX2)
                                            comprobador = expReg.matcher(oper);
                                            if(comprobador.matches())
                                                formato = 6;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return formato;
    }
    
    private boolean CodopConOper(Tabop codop_inf, String oper){
        int i = 0;
        boolean bandoper = false;
        while(i < codop_inf.nmodos && !bandoper){
            if(codop_inf.b_calcular.get(i) > 0)
                bandoper = true;
            i++;
        }
        if(!bandoper && !oper.equals("NULL"))
            error = "no se necesita OPER (operando)";
        if(bandoper && oper.equals("NULL"))
            error = "se requiere OPER (operando)";
        return bandoper;
    }
    
    private String ModSinOper(Tabop codop_inf){
        int i = 0;
        while(i < codop_inf.nmodos){
            if(codop_inf.b_calcular.get(i) == 0)
                return codop_inf.mod_dir.get(i);
            i++;
        }
        return "";
    }
    
    /*private String ModINH(Tabop codop_inf){
        System.out.println("---------------------------");
        if(codop_inf.mod_dir.get(0).equals("INH"))
            return "INH";
        return "";
    }*/
    
    private String ModIMM(Tabop codop_inf, String oper){
        String modo = "", aux = "";
        /*if(oper.equals("NULL")){
            if(codop_inf.mod_dir.get(0).equals("IMM0"))
                modo = "IMM0";
        }*/
        //else{
            int i = 0;
            while(i < codop_inf.nmodos){
                if(codop_inf.mod_dir.get(i).equals("IMM8"))
                    aux = "IMM8";
                if(codop_inf.mod_dir.get(i).equals("IMM16"))
                    aux = "IMM16";
                i++;
            }
            try{
                BasesNumericas numero;
                if(aux.equals("IMM8")){
                    numero = new BasesNumericas(oper.substring(1));
                    if(numero.ValorEntero() >= -256 && numero.ValorEntero() <= 255)
                        modo = aux;
                    else
                        error = "valor fuera de rango para \"IMM8\"";
                }
                else
                    if(aux.equals("IMM16")){
                        numero = new BasesNumericas(oper.substring(1));
                        if(numero.ValorEntero() >= -32768 && numero.ValorEntero() <= 65535)
                            modo = aux;
                        else
                            error = "valor fuera de rango para \"IMM16\"";
                    }
                    else
                        error = "el CODOP no soporta direccionamiento \"IMM\"";
            }
            catch(NumberFormatException nfe){
                error = "formato de OPER (operando) invalido \"en (IMM)\"";
            }
        //}
        return modo;
    }
    
    private String ModDIR(Tabop codop_inf, String oper){
        String modo = "", aux = "";
        int i = 0;
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("DIR"))
                aux = "DIR";
            i++;
        }
        try{
            BasesNumericas numero;
            if(aux.equals("DIR")){
                numero = new BasesNumericas(oper);
                if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 255)
                    modo = aux;
                /*else
                    error = "valor fuera de rango para \"DIR\"";*/
            }
            /*else
                error = "el CODOP no soporta direccionamiento \"DIR\"";*/
        }
        catch(NumberFormatException nfe){
            error = "formato de OPER (operando) invalido \"en (DIR)\"";
        }
        return modo;
    }
    
    private String ModEXT(Tabop codop_inf, String oper){
        String modo = "", aux = "";

        int i = 0;
        while(i < codop_inf.nmodos  && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("EXT"))
                aux = "EXT";
            i++;
        }
        try{
            if(aux.equals("EXT")){
                BasesNumericas numero;
                numero = new BasesNumericas(oper);
                if(numero.ValorEntero() >= -32768 && numero.ValorEntero() <= 65535)
                    modo = aux;
                else
                    error = "valor fuera de rango para \"EXT\"";
            }
            /*else
                error = "el CODOP no soporta direccionamiento \"EXT\"";*/
        }
        catch(NumberFormatException nfe){
        	Pattern expReg;
	        Matcher comprobador;
	        expReg = Pattern.compile("[a-zA-Z][\\w]{0,7}");
	        comprobador = expReg.matcher(oper);
            if(comprobador.matches())
                    modo = aux;
            else{
            	expReg = Pattern.compile("(\\$|%|@|\\-|)([A-F]|[0-9])+");
	        	comprobador = expReg.matcher(oper);
            	if(comprobador.matches())
            		error = "formato de OPER (operando) invalido \"en (EXT)\"";
            	else
                	error = "etiqueta invalida \"en (EXT)\"";
            }
        }
        return modo;
    }
    
    private String ModIDX_ida(Tabop codop_inf, String oper){//indizado de acumulador
        String modo = "", aux = "", oper1, oper2;
        int i = 0;
        StringTokenizer tokens = new StringTokenizer(oper, ",", true);
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("IDX"))
                aux = "IDX";
            i++;
        }
        if(aux.equals("IDX")){
            oper1 = tokens.nextToken();
            tokens.nextToken();
            oper2 = tokens.nextToken();
            if(oper1.equals("A") | oper1.equals("B") | oper1.equals("D")){
                if(oper2.equals("X")| oper2.equals("Y")| oper2.equals("SP")| oper2.equals("PC"))
                    modo = aux;
                else
                    error = "\" " + oper2 + "\"" + "no es un REGISTRO valido para \"IDX\"";
            }
            else
                error = "\"" + oper1 + "\" " + "no es un ACUMULADOR valido para \"IDX\"";
        }
        else
            error = "el CODOP no soporta direccionamiento \"IDX\"";
        return modo;
    }
    
    private String ModIDX_5b(Tabop codop_inf, String oper){//5 BITS
        String modo = "", aux = "", oper1, oper2;
        int i = 0;
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("IDX"))
                aux = "IDX";
            i++;
        }
        try{
            if(aux.equals("IDX")){
                StringTokenizer tokens = new StringTokenizer(oper, ",", true);
                if(tokens.countTokens() == 3){
                    oper1 = tokens.nextToken();
                    tokens.nextToken();
                    oper2 = tokens.nextToken();
                }
                else{
                    oper1 = "0";
                    tokens.nextToken();
                    oper2 = tokens.nextToken();
                }
                BasesNumericas numero = new BasesNumericas(oper1);
                if(oper2.equals("X")| oper2.equals("Y")| oper2.equals("SP")| oper2.equals("PC")){
                    if(numero.ValorEntero() >= -16 && numero.ValorEntero() <= 15)
                        modo = aux;
                    /*else
                        error = "valor fuera de rango para \"IDX\"";*/
                }
                else
                    error = "\"" + oper2 + "\" " + "no es un REGISTRO valido para \"IDX\"";
            }
            /*else
                error = "el CODOP no soporta direccionamiento \"IDX\"";*/
        }
        catch(NumberFormatException nfe){
            error = "formato de OPER (operando) invalido \"en (IDX)\"";
        }
        return modo;
    }
    
    private String ModIDX_ppdi(Tabop codop_inf, String oper){//PRE/POST decremento/incremento
        String modo = "", aux = "", oper1, oper2;
        int i = 0;
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("IDX"))
                aux = "IDX";
            i++;
        }
        try{
            if(aux.equals("IDX")){
                StringTokenizer tokens = new StringTokenizer(oper, ",", true);
                oper1 = tokens.nextToken();
                tokens.nextToken();
                oper2 = tokens.nextToken();
                BasesNumericas numero = new BasesNumericas(oper1);
                Pattern expReg;
                Matcher comprobador;
                expReg = Pattern.compile("((\\+|-)(X|Y|SP))|((X|Y|SP)(\\+|-))");
                comprobador = expReg.matcher(oper2);
                if(comprobador.matches()){
                    if(numero.ValorEntero() >= 1 && numero.ValorEntero() <= 8)
                        modo = aux;
                    else
                        error = "valor fuera de rango para \"IDX\" (n,-+r-+)";                
                }
                else
                    error = "\"" + oper2 + "\" " + "no es un REGISTRO valido para \"IDX\" (n,-+r-+)";
            }
            else
                error = "el CODOP no soporta direccionamiento \"IDX\"";
        }
        catch(NumberFormatException nfe){
            error = "formato de OPER (operando) invalido \"en (IDX)\" (n,-+r-+)";
        }
        return modo;
    }
    
    private String ModIDX1(Tabop codop_inf, String oper){//9 BITS
        String modo = "", aux = "", oper1, oper2;
        int i = 0;
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("IDX1"))
                aux = "IDX1";
            i++;
        }
        try{
            if(aux.equals("IDX1")){
                StringTokenizer tokens = new StringTokenizer(oper, ",", true);
                oper1 = tokens.nextToken();
                tokens.nextToken();
                oper2 = tokens.nextToken();
                BasesNumericas numero = new BasesNumericas(oper1);
                if(oper2.equals("X")| oper2.equals("Y")| oper2.equals("SP")| oper2.equals("PC")){
                    if(numero.ValorEntero() >= -256 && numero.ValorEntero() <= 255)
                        modo = aux;
                    /*else
                        error = "valor fuera de rango para \"IDX1\"";*/
                }
                else
                    error = "\"" + oper2 + "\" " + "no es un REGISTRO valido para \"IDX1\"";
            }
            /*else
                error = "el CODOP no soporta direccionamiento \"IDX1\"";*/
        }
        catch(NumberFormatException nfe){
            error = "formato de OPER (operando) invalido \"en (IDX1)\"";
        }
        return modo;
    }
    
    private String ModIDX2(Tabop codop_inf, String oper){//16 BITS
        String modo = "", aux = "", oper1, oper2;
        int i = 0;
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("IDX2"))
                aux = "IDX2";
            i++;
        }
        try{
            if(aux.equals("IDX2")){
                StringTokenizer tokens = new StringTokenizer(oper, ",", true);
                oper1 = tokens.nextToken();
                tokens.nextToken();
                oper2 = tokens.nextToken();
                BasesNumericas numero = new BasesNumericas(oper1);
                if(oper2.equals("X")| oper2.equals("Y")| oper2.equals("SP")| oper2.equals("PC")){
                    if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 65535)
                        modo = aux;
                    else
                        error = "valor fuera de rango para \"IDX2\"";
                }
                else
                    error = "\"" + oper2 + "\" " + "no es un REGISTRO valido para \"IDX2\"";
            }
            /*else
                error = "el CODOP no soporta direccionamiento \"IDX2\"";*/
        }
        catch(NumberFormatException nfe){
            error = "formato de OPER (operando) invalido \"en (IDX2)\"";
        }
        return modo;
    }
    
    private String ModIDX2_I(Tabop codop_inf, String oper){//16 BITS Indirecto "[IDX2]"
        String modo = "", aux = "", oper1, oper2;
        int i = 0;
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("[IDX2]"))
                aux = "[IDX2]";
            i++;
        }
        try{
            if(aux.equals("[IDX2]")){
                StringTokenizer tokens = new StringTokenizer(oper, ",", true);
                oper1 = tokens.nextToken().substring(1);
                tokens.nextToken();
                oper2 = tokens.nextToken();
                oper2 = oper2.substring(0, oper2.length() - 1);
                BasesNumericas numero = new BasesNumericas(oper1);
                if(oper2.equals("X")| oper2.equals("Y")| oper2.equals("SP")| oper2.equals("PC")){
                    if(numero.ValorEntero() >= 0 && numero.ValorEntero() <= 65535)
                        modo = aux;
                    else
                        error = "valor fuera de rango para \"[IDX2]\"";
                }
                else
                    error = "\"" + oper2 + "\" " + "no es un REGISTRO valido para \"[IDX2]\"";
            }
            else
                error = "el CODOP no soporta direccionamiento \"[IDX2]\"";
        }
        catch(NumberFormatException nfe){
            error = "formato de OPER (operando) invalido \"en ([IDX2])\"";
        }
        return modo;
    }
    
    private String ModDIDX(Tabop codop_inf, String oper){//indizado de acumulador D indirecto "[D,IDX]"
        String modo = "", aux = "", oper1, oper2;
        int i = 0;
        while(i < codop_inf.nmodos && aux.equals("")){
            if(codop_inf.mod_dir.get(i).equals("[D,IDX]"))
                aux = "[D,IDX]";
            i++;
        }
        if(aux.equals("[D,IDX]")){
            StringTokenizer tokens = new StringTokenizer(oper, ",", true);
            oper1 = tokens.nextToken().substring(1);
            tokens.nextToken();
            oper2 = tokens.nextToken();
            oper2 = oper2.substring(0, oper2.length() - 1);
            if(oper1.equals("D")){
                if(oper2.equals("X")| oper2.equals("Y")| oper2.equals("SP")| oper2.equals("PC"))
                    modo = aux;
                else
                    error = "\"" + oper2 + "\" " + "no es un REGISTRO valido para \"[D,IDX]\"";
            }
            else
                error = "\"" + oper1 + "\" " + "no es un ACUMULADOR valido para \"[D,IDX]\"";
        }
        else
            error = "el CODOP no soporta direccionamiento \"[D,IDX]\"";
        return modo;
    }
    
    private String ModREL(Tabop codop_inf, String oper){
        String modo = "", aux = "";
        if(codop_inf.mod_dir.get(0).equals("REL8"))
            aux = "REL8";
        else
            if(codop_inf.mod_dir.get(0).equals("REL9"))
                aux = "REL9";
            else
                if(codop_inf.mod_dir.get(0).equals("REL16"))
                    aux = "REL16";
        try{
            BasesNumericas numero;
            if(aux.equals("REL8")){
            	numero = new BasesNumericas(oper);
            	if(numero.ValorEntero() >= -128 && numero.ValorEntero() <= 127)
            		modo = aux;
            	else
            		error = "valor fuera de rango para \"REL8\"";
            }
            else{
                if(aux.equals("REL9")){
                	numero = new BasesNumericas(oper);
                	if(numero.ValorEntero() >= -256 && numero.ValorEntero() <= 255)
                		modo = aux;
                	else
                		error = "valor fuera de rango para \"REL9\"";
                }
                else{
                    if(aux.equals("REL16")){
                    	numero = new BasesNumericas(oper);
                    	if(numero.ValorEntero() >= -32768 && numero.ValorEntero() <= 65535)
                    		modo = aux;
                    	else
                    		error = "valor fuera de rango para \"REL16\"";
                    }
                    /*else
                        error = "el CODOP no soporta direccionamiento \"REL\"";*/
                }
            }
        }
        catch(NumberFormatException nfe){
        	Pattern expReg;
	        Matcher comprobador;
	        expReg = Pattern.compile("[a-zA-Z][\\w]{0,7}");
	        comprobador = expReg.matcher(oper);
            if(comprobador.matches())
                    modo = aux;
            else{
            	expReg = Pattern.compile("(\\$|%|@|\\-|)([A-F]|[0-9])+");
	        	comprobador = expReg.matcher(oper);
            	if(comprobador.matches())
            		error = "formato de OPER (operando) invalido \"en (REL)\"";
            	else
                	error = "etiqueta invalida \"en (REL)\"";
            }
        }
        return modo;
    }
}
