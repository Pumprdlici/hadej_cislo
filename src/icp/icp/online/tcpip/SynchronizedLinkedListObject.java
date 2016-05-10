package icp.online.tcpip;

import java.util.LinkedList;

/**
 * Název úlohy: Jednoduché BCI
 * Tøída: SynchronizedLinkedListObject
 * @author Michal Patoèka
 * První verze vytvoøena: 3.3.2010
 * @version 1.0
 * 
 * Thread-safe linked list, používaný jako buffer objektù typu RDA.
 * Pøetíženy jsou pouze používané metody.
 */

public class SynchronizedLinkedListObject extends LinkedList<Object> {
	private static final long serialVersionUID = 1L;

        @Override
	public synchronized void addLast(Object o){
		super.add(o);
	}
	
        @Override
	public synchronized Object removeFirst(){
		return super.removeFirst();
	}
	
        @Override
	public synchronized boolean isEmpty(){
		return super.isEmpty();
	}

}
