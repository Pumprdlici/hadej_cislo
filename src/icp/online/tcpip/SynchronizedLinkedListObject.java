package icp.online.tcpip;

import java.util.LinkedList;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: SynchronizedLinkedListObject
 * @author Michal Patočka
 * První verze vytvořena: 3.3.2010
 * @version 1.0
 * 
 * Thread-safe linked list, používaný jako buffer objektů typu RDA.
 * Přetíženy jsou pouze používané metody.
 */

public class SynchronizedLinkedListObject extends LinkedList<Object> {
	private static final long serialVersionUID = 1L;

	public synchronized void addLast(Object o){
		super.add(o);
	}
	
	public synchronized Object removeFirst(){
		return super.removeFirst();
	}
	
	public synchronized boolean isEmpty(){
		return super.isEmpty();
	}

}
