/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensambladorhc12;

/**
 *
 * @author DOOMDANY
 */
public class Contloc {
    int CONLOC, PC, nbytes;
    
    public Contloc(int pc_anterior, int bytes){
    	CONLOC = pc_anterior;
    	nbytes = bytes;
    	PC = CONLOC + bytes;
    }
    
    public Contloc(int pc_anterior){
    	PC = CONLOC = pc_anterior;
    	nbytes = 0;
    }
}
