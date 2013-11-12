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

public class ArchInst {
    
    public File archivo;
    FileWriter fw;
    BufferedWriter bw;
    String buffer;
    
    public ArchInst(File archivoAux){
        try{
            archivo = new File(archivoAux.getPath().substring(0, archivoAux.getPath().length()-4)+".INST");
            archivo.createNewFile();
        }
        catch(IOException ioe){
            
        }
    }
    
    public boolean Abrir(){
        try{
            fw = new FileWriter(archivo);
            bw = new BufferedWriter(fw);
            bw.write("LINEA\t\tCONTLOC\t\tETQ\t\tCODOP\t\tOPER\t\tMODOS");
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
    
    public boolean Escribirln(Linea ln){
        BasesNumericas numero = new BasesNumericas(ln.conloc.CONLOC, 16);
        try{
            buffer = ln.nlinea + "\t\t" + numero.Hex4D() + "\t\t" + ln.etq + "\t\t" + ln.codop + "\t\t" + ln.oper + "\t\t" + ln.mod_dirs;
            bw.write(buffer);
            bw.newLine();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
}
