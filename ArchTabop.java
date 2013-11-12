/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author DOOMDANY
 */
import java.io.*;
import java.util.StringTokenizer;

public class ArchTabop {
    public File archivo;
    FileReader fr;
    BufferedReader br;
    
    public ArchTabop(File archivoAux){
        String aux1 = archivoAux.getPath(), aux2 = "";
        StringTokenizer tokens = new StringTokenizer(aux1,"\\");
        while(tokens.countTokens() > 1){
            aux2 = aux2 + tokens.nextToken() + "\\";
        }
        aux2 = aux2 + "TABOP.TXT";
        archivo = new File(aux2);
    }
    
    public boolean Abrir(){
        try{
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
        }
        catch(IOException ioe){
            try{
                fr = new FileReader("TABOP.TXT");
                br = new BufferedReader(fr);
            }
            catch(IOException ioe2){
                return false;
            }
        }
        return true;
    }
    
    public boolean Cerrar(){
        try{
            br.close();
            fr.close();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
    
    public Tabop Leerln(){
        String buffer;
        Tabop aux = new Tabop();
        try{
            buffer = br.readLine();
            StringTokenizer tokens = new StringTokenizer(buffer,"|");
            aux.codop = tokens.nextToken();
            aux.mod_dir.add(tokens.nextToken());
            aux.cod_hex.add(tokens.nextToken());
            aux.b_calculados.add(Integer.parseInt(tokens.nextToken()));
            aux.b_calcular.add(Integer.parseInt(tokens.nextToken()));
            aux.b_total.add(Integer.parseInt(tokens.nextToken()));
            return aux;
        }
        catch(IOException ioe){
            //ioe.getMessage();
        }
        catch(NullPointerException npe){
            
        }
        return null;
    }
}