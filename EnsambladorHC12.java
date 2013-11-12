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

public class EnsambladorHC12 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArchAsm A = new ArchAsm();
        ArchInst inst;
        ArchError err;
        String linea;
        int i = 1;
        Linea ln, lnAux;
        ArrayList<Linea> cola = new ArrayList<Linea>();
        if(A.Abrir()){
            ln = new Linea(i);
            while((linea = A.Leerln()) != null && ln.end == false){
                ln.Validar(linea);
                cola.add(ln);
                if(!ln.end){
                    i++;
                    ln = new Linea(i);
                }
            }
            A.Cerrar();
            inst = new ArchInst(A.archivo);
            err = new ArchError(A.archivo);
            if(inst.Abrir() && err.Abrir()){
                //lnAux = null;
                while(!cola.isEmpty()){
                    lnAux = cola.remove(0);
                    if(!lnAux.coment && lnAux.error == null)
                        inst.Escribirln(lnAux);
                    else
                        if(lnAux.error != null)
                            err.Escribirln(lnAux);
                }
                if(!ln.end){
                    ln.nlinea -= 1;
                    ln.error = "no se encontro la directiva 'END'";
                    err.Escribirln(ln);
                }
                inst.Cerrar();
                err.Cerrar();
            }
        }
    }
}
