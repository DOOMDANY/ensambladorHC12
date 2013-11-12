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

public class ArchTabsim {
    public File archivo;
    FileWriter fw;
    BufferedWriter bw;
    String buffer;
    
    public ArchTabsim(File archivoAux){
        archivo = new File(archivoAux.getPath().substring(0, archivoAux.getPath().length()-4)+".TDS");
    }
    
    public boolean Crear(){
        try{
            archivo.createNewFile();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
    
    public boolean Abrir(){
        try{
            fw = new FileWriter(archivo);
            bw = new BufferedWriter(fw);
            bw.write("ETQ\t\tVALOR");
            bw.newLine();
            bw.write("...................................................................................................");
            bw.newLine();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
    
    public boolean Cerrar(){
        try{
            bw.close();
            fw.close();
            return true;
        }
        catch(IOException ioe){
            return false;
        }
    }
    
    public boolean Escribirln(Tabsim simb){
        BasesNumericas numero = new BasesNumericas(Integer.toString(simb.direccion));
        try{
            buffer = simb.etq + "\t\t" + numero.HexnD(4);
            bw.write(buffer);
            bw.newLine();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
}
