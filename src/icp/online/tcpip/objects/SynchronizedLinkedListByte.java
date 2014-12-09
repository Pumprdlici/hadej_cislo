package icp.online.tcpip.objects;

import java.util.LinkedList;

/**
 * Název úlohy: Jednoduché BCI
 * Tøída: SynchronizedLinkedListByte
 * @author Michal Patoèka
 * První verze vytvoøena: 3.3.2010
 * @version 1.0
 * 
 * Thread-safe linked list, používaný jako buffer bajtù pro tcp/ip clienta.
 * Pøetíženy jsou pouze používané metody.
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
