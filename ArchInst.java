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
        archivo = new File(archivoAux.getPath().substring(0, archivoAux.getPath().length()-4)+".INST");
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
            bw.write("LINEA\t\tCONTLOC\t\tETQ\t\tCODOP\t\tOPER\t\tMODOS\t\tCODMAQ");
            bw.newLine();
            bw.write(".......................................................................................................................");
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
        BasesNumericas numero = new BasesNumericas(Integer.toString(ln.conloc.CONLOC));
        try{
            buffer = ln.nlinea + "\t\t" + numero.HexnD(4) + "\t\t" + ln.etq + "\t\t" + ln.codop + "\t\t" + ln.oper + "\t\t" + ln.mod_dirs + "\t\t" + ln.cod_maq;
            bw.write(buffer);
            bw.newLine();
        }
        catch(IOException ioe){
            return false;
        }
        return true;
    }
}
