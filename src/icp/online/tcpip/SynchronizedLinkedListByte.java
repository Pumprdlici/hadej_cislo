package icp.online.tcpip;

import java.util.LinkedList;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: SynchronizedLinkedListByte
 * @author Michal Patočka
 * První verze vytvořena: 3.3.2010
 * @version 1.0
 * 
 * Thread-safe linked list, používaný jako buffer bajtů pro tcp/ip clienta.
 * Přetíženy jsou pouze používané metody.
 */


public class SynchronizedLinkedListByte extends LinkedList<Byte> {
	private static final long serialVersionUID = 1L;

	public synchronized void addLast(Byte b){
		super.add(b);
	}
	
	public synchronized Byte removeFirst(){
		return super.removeFirst();
	}
	
	public synchronized boolean isEmpty(){
		return super.isEmpty();
	}

}
