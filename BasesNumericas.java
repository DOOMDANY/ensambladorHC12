
package ensambladorhc12;

//import

public class BasesNumericas{
    String valor;
    int base;
    
    protected BasesNumericas(int valorNum, int baseNum){
        valor = Integer.toString(valorNum, baseNum);
        base = baseNum;
    }
    
    protected BasesNumericas(String valorNum){
        valor = ValNeg(valorNum);
        //System.out.println(valor);
        base = 10;
    }
    
    private String ValNeg(String valorNum){
        char aux = valorNum.charAt(0);
        String cad_aux = "";
        int bit;
        if(aux == '$'){
            if(valorNum.charAt(1) == 'F'){
                for(int i = 1; i < valorNum.length(); i++){
                    if(valorNum.charAt(i) != 'F'){
                        cad_aux = "1";
                        for(; i < valorNum.length(); i++)
                            cad_aux += valorNum.charAt(i);
                    }
                }
                if(cad_aux.equals(""))
                    cad_aux = "1";
                bit = Integer.valueOf(cad_aux, 16);
                cad_aux = "%" + Integer.toString(bit, 2);
            }
            else{
                bit = Integer.valueOf(valorNum.substring(1), 16);
                return Integer.toString(bit);
            }
        }
        else{
            if(aux == '@'){
                if(valorNum.charAt(1) == '7'){
                    for(int i = 1; i < valorNum.length(); i++){
                        if(valorNum.charAt(i) != '7'){
                            cad_aux = "1";
                            for(; i < valorNum.length(); i++)
                                cad_aux += valorNum.charAt(i);
                        }
                    }
                    if(cad_aux.equals(""))
                        cad_aux = "1";
                    bit = Integer.valueOf(cad_aux, 8);
                    cad_aux = "%" + Integer.toString(bit, 2);
                }
                else{
                    bit = Integer.valueOf(valorNum.substring(1), 8);
                    return Integer.toString(bit);
                }
            }
        }
        if(aux == '%' || !cad_aux.equals("")){
             if(!cad_aux.equals(""))
                if(cad_aux.charAt(0) == '%'){
                    valorNum = cad_aux;
                    cad_aux = "";
                }
             if(valorNum.charAt(1) == '1'){
                 for(int i = 1; i < valorNum.length(); i++){
                    if(valorNum.charAt(i) != '1'){
                        cad_aux = "1";
                        for(; i < valorNum.length(); i++)
                            cad_aux += valorNum.charAt(i);
                    }
                }
                if(cad_aux.equals(""))
                    cad_aux = "1";
                bit = Integer.valueOf(cad_aux, 2);
                bit = Cados(bit);
            }
            else
                bit = Integer.valueOf(valorNum.substring(1), 2);
        }
        else
            return valorNum;
        return Integer.toString(bit);
    }
    
    private int Cados(int bits){
        return (~bits) + 1;
    }
    
    protected void CambioBase(int baseNum){
        int valorNum;
        valorNum = Integer.parseInt(valor, base);
        valor = Integer.toString(valorNum, baseNum);
        base = baseNum;
    }
    
    protected int ValorEntero(){
        return Integer.parseInt(valor);
    }
    
    public String Hex4D(){
        int i;
        i = valor.length();
        String aux = "";
        while(i < 4){
            aux += "0";
            i++;
        }
        aux += valor;
        return aux.toUpperCase();
    }
}
