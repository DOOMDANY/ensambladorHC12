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
        /*BasesNumericas num = new BasesNumericas("65535");
        System.out.println(num.HexnD(4));*/
        ArchAsm asm = new ArchAsm();
        ArchInst inst;
        ArchError err;
        ArchTabsim tds;
        String linea;
        int i = 1, pc = 0;
        boolean aux, banderror = false;
        Linea ln, lnAux;
        ArrayList<Linea> cola = new ArrayList<Linea>();
        ArrayList<Linea> listaInst = new ArrayList<Linea>();
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
            inst.Crear();
            err.Crear();
            tds.Crear();
            if(/*inst.Abrir() && */err.Abrir()){
                //lnAux = null;
                while(!cola.isEmpty()){
                    lnAux = cola.remove(0);
                    if(!lnAux.coment && lnAux.error == null){
                        //inst.Escribirln(lnAux);
                        listaInst.add(lnAux);
                    }
                    else
                        if(lnAux.error != null){
                            err.Escribirln(lnAux);
                            banderror = true;
                        }
                }
                if(!ln.end){
                    ln.nlinea -= 1;
                    ln.error = "no se encontro la directiva 'END'";
                    err.Escribirln(ln);
                    banderror = true;
                }
                CodigoMaquina paso2 = new CodigoMaquina(lista_tabop, lista_tabsim, listaInst);
                ArrayList<String> lista_error = new ArrayList<String>();
                String error;
                do{
                	error = paso2.ComprobarEtq();
                	if(error != null){
                		lista_error.add(error);
                		listaInst = paso2.listaInst;
                		lista_tabsim = paso2.lista_tabsim;
                		listaInst.get(0).pc = 0;
                		listaInst.get(0).ValidarCodop(lista_tabop);
                		for(i = 1; i < listaInst.size(); i++){
                			listaInst.get(i).pc = listaInst.get(i - 1).conloc.PC;
                			listaInst.get(i).ValidarCodop(lista_tabop);
                		}
                	}
                }while(error != null);
                if(!lista_error.isEmpty() || banderror){
                    err.Escribir("_____________________________________________________________________");
                    err.Escribir("se detectaron errores, imposible entrar al paso 2 del ENSAMBLADOR");
                    while(!lista_error.isEmpty())
                    	err.Escribir(lista_error.remove(0));
                    if(inst.Abrir()){
                        cola = listaInst;
                        while(!cola.isEmpty())
                            inst.Escribirln(cola.remove(0));
                        inst.Cerrar();
                    }
                }
                else{
                    cola = paso2.CMMaster();
                    if(cola != null && inst.Abrir()){
                        while(!cola.isEmpty())
                            inst.Escribirln(cola.remove(0));
                        inst.Cerrar();
                    }
                }
                err.Cerrar();
                if(tds.Abrir()){
	                for(int j = 0; j< lista_tabsim.size(); j++){
	                    tds.Escribirln(lista_tabsim.get(j));
	                }
	                tds.Cerrar();
	            }
            }
        }
    }
}
