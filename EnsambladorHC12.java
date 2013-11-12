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
        /*ModDir M = new ModDir();
        System.out.println(M.FormatoOper("8,+PC"));*/
        /*BasesNumericas num = new BasesNumericas("%7FFF", 16);
        System.out.println(num.ValorEntero());*/
        ArchAsm asm = new ArchAsm();
        ArchInst inst;
        ArchError err;
        ArchTabsim tds;
        String linea;
        int i = 1, pc = 0;
        boolean aux;
        Linea ln, lnAux;
        ArrayList<Linea> cola = new ArrayList<Linea>();
        if(asm.Abrir()){
            ListaTabop lista_tabop = new ListaTabop();
            ArchTabop atabop = new ArchTabop(asm.archivo);
            ListaTabsim lista_tabsim = new ListaTabsim();
            Tabop codop_inf; 
            if(atabop.Abrir()){
                while((codop_inf = atabop.Leerln()) != null)
                    lista_tabop.Insertar(codop_inf);
            }
            ln = new Linea(i, pc);
            while((linea = asm.Leerln()) != null && ln.end == false){
                //System.out.println("LINEA "+ i);
                ln.lista_tabsim = lista_tabsim;
                ln.Validar(linea, lista_tabop);
                cola.add(ln);
                lista_tabsim = ln.lista_tabsim;
                if(!ln.end){
                    i++;
                    pc = ln.conloc.PC;
                    aux = ln.org;
                    ln = new Linea(i, pc);
                    ln.org = aux;
                }
            }
            asm.Cerrar();
            inst = new ArchInst(asm.archivo);
            err = new ArchError(asm.archivo);
            tds = new ArchTabsim(asm.archivo);
            if(tds.Abrir()){
                for(int j = 0; j< lista_tabsim.size(); j++){
                    tds.Escribirln(lista_tabsim.get(j));
                }
                tds.Cerrar();
            }
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
