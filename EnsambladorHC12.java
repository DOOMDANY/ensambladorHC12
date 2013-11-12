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
        ArchAsm asm = new ArchAsm();
        ArchInst inst;
        ArchError err;
        String linea;
        int i = 1;
        Linea ln, lnAux;
        ArrayList<Linea> cola = new ArrayList<Linea>();
        if(asm.Abrir()){
            ListaTabop lista_tabop = new ListaTabop();
            ArchTabop atabop = new ArchTabop(asm.archivo);
            Tabop codop_inf; 
            if(atabop.Abrir()){
                while((codop_inf = atabop.Leerln()) != null)
                    lista_tabop.Insertar(codop_inf);
            }
            ln = new Linea(i);
            while((linea = asm.Leerln()) != null && ln.end == false){
                ln.Validar(linea, lista_tabop);
                cola.add(ln);
                if(!ln.end){
                    i++;
                    ln = new Linea(i);
                }
            }
            asm.Cerrar();
            inst = new ArchInst(asm.archivo);
            err = new ArchError(asm.archivo);
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
