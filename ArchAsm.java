/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author DOOMDANY
 */
import javax.swing.*;
import java.io.*;

public class ArchAsm {
    public File archivo;
    FileReader fr;
    BufferedReader br;
    //boolean leido;
    
    public ArchAsm(){
        //leido = false;
    }
    
    public boolean Abrir(){
        JFileChooser selector = new JFileChooser();
        selector.setVisible(true);
        int returnVal = selector.showOpenDialog(selector);
        if(returnVal == JFileChooser.APPROVE_OPTION)
            if(selector.getSelectedFile().getName().substring(selector.getSelectedFile().getName().length()-4).toUpperCase().equals(".ASM")){
                try{
                    archivo = selector.getSelectedFile();
                    fr = new FileReader(archivo);
                    br = new BufferedReader(fr);
                }
                catch(IOException ioe){
                    return false;
                }
                return true;
            }
        return false;
    }
    
    public boolean Cerrar(){
        try{
            br.close();
            fr.close();
            return true;
        }
        catch(IOException ioe){
            return false;
        }
    }
    
    public String Leerln(){
        String buffer = null;
        try{
            buffer = br.readLine();
        }
        catch(IOException ioe){
            //ioe.getMessage();
        }
        return buffer;
    }
}
