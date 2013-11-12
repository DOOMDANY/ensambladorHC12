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

public class ArchError {
    public File archivo;
    FileWriter fw;
    BufferedWriter bw;
    String buffer;
    
    public ArchError(File archivoAux){
        archivo = new File(archivoAux.getPath().substring(0, archivoAux.getPath().length()-4)+".ERR");
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
            bw.write("LINEA\tERROR");
            bw.newLine();
            bw.write(".....................................................................");
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
    
    public boolean Escribirln(Linea ln){
        try{
            buffer = ln.nlinea + "\t" + ln.error;
            bw.write(buffer);
            bw.newLine();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
    
    public boolean Escribir(String buffer){
        try{
            bw.write(buffer);
            bw.newLine();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
}
